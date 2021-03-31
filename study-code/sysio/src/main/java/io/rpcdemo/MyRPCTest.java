package io.rpcdemo;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.rpcdemo.proxy.MyProxy;
import org.junit.Test;

import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Alexander Zero
 * @version 1.0.0
 * @date 2021/3/30
 * <p>
 * 1. 先驾驶一个需求,写个rpc
 * 2. 来回通信, 连接数量, 拆包?
 * 3. 动态代理, 序列化, 协议封装
 * 4. 连接池
 * 5. 就像本地方法一样去调用远程的方法, 面向java, 面向interface
 */
public class MyRPCTest {

    //模拟server
    @Test
    public void startServer() {

        Dispatcher dispatcher = new Dispatcher();

        dispatcher.register(Car.class.getName(), new MyCar());
        dispatcher.register(Fly.class.getName(), new MyFly());


        NioEventLoopGroup boss = new NioEventLoopGroup(5);
        NioEventLoopGroup worker = new NioEventLoopGroup(20);

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss, worker);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel server) throws Exception {
                System.out.println("server accpet a client, this port is " + server.remoteAddress().getPort());
                ChannelPipeline pipeline = server.pipeline();
                pipeline.addLast(new ServerDecoder());
                pipeline.addLast(new ServerResponseHandler(dispatcher));
            }
        });
        ChannelFuture bind = bootstrap
                .bind(new InetSocketAddress("localhost", 9090));

        System.out.println("server started~~~~");
        try {
            bind.sync().channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    //模拟客户端
    @Test
    public void startClient() {

        int size = 50;
        Thread[] threads = new Thread[size];
        AtomicInteger ai = new AtomicInteger(1);
        for (int i = 0; i < size; i++) {
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    Car car = MyProxy.proxyGet(Car.class); //动态代理
                    int args = ai.getAndIncrement();
                    String res = car.ooxx("args: " + args);
//                    String res = car.ooxx("request exec thread is:　" + Thread.currentThread().getName());
                    System.out.println("send args: " + args + " ; " + res);
                }
            });
        }
        for (Thread thread : threads) {
            thread.start();
        }
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        Car car = proxyGet(Car.class); //动态代理
//        car.ooxx("hello");
    }


    public static MyHeader createHeader(byte[] msgBody) {
        MyHeader header = new MyHeader();
        int dataLength = msgBody.length;
        int flag = 0x14141414;//具体的协议
        long requestId = UUID.randomUUID().getLeastSignificantBits();

        header.setFlag(flag);
        header.setRequestId(requestId);
        header.setDataLength(dataLength);


        return header;
    }


}

interface Car {
    public String ooxx(String msg);
}

interface Fly {
    public void xxoo(String msg);
}







class ClientPool {

    NioSocketChannel[] clients;
    Object[] lock;

    ClientPool(int size) {
        clients = new NioSocketChannel[size];
        lock = new Object[size];
        for (int i = 0; i < lock.length; i++) {
            lock[i] = new Object();
        }
    }

}


class ClientFactory {

    int poolSize = 5;
    Random r = new Random();
    NioEventLoopGroup workerGroup;

    private static final ClientFactory factory = new ClientFactory();

    private ClientFactory() {
    }


    public static ClientFactory getInstance() {
        return factory;
    }

    ConcurrentHashMap<InetSocketAddress, ClientPool> outboxes = new ConcurrentHashMap<>();


    public synchronized NioSocketChannel getClient(InetSocketAddress address) {
        ClientPool clientPool = outboxes.get(address);

        if (clientPool == null) {
            outboxes.putIfAbsent(address, new ClientPool(poolSize));
            clientPool = outboxes.get(address);
        }

        int i = r.nextInt(poolSize);
        if (clientPool.clients[i] != null && clientPool.clients[i].isActive()) {
            return clientPool.clients[i];
        }

        synchronized (clientPool.lock[i]) {
            NioSocketChannel channel = create(address);
            clientPool.clients[i] = channel;
            System.out.println("request thread is: " + Thread.currentThread().getName());
            return clientPool.clients[i];
        }
    }

    private NioSocketChannel create(InetSocketAddress address) {
        workerGroup = new NioEventLoopGroup(1);

        Bootstrap bs = new Bootstrap();
        ChannelFuture connect = bs.group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        ChannelPipeline pipeline = nioSocketChannel.pipeline();
                        pipeline.addLast(new ServerDecoder());
                        pipeline.addLast(new ClientResponse());
                    }
                }).connect(address);

        NioSocketChannel channel = null;
        try {
            channel = (NioSocketChannel) connect.sync().channel();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return channel;
    }
}


class ClientResponse extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        PackMsg respPack = (PackMsg) msg;
//        System.out.println(respPack.getContent().getRes());
        ResponseMappingCallback.runCallBack(respPack);

/*     version 1.0
        ByteBuf buf = (ByteBuf) msg;
        int headerSize = 88;
       System.out.println("client handler message");
        if (buf.readableBytes() > headerSize) {
            byte[] bytes = new byte[headerSize];
            buf.getBytes(0, bytes);

            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            ObjectInputStream oin = new ObjectInputStream(in);
            MyHeader header = (MyHeader) oin.readObject();
            System.out.println("requestId is " + header.getRequestId());
            ResponseHandler.runCallBack(header.getRequestId());

            if (buf.readableBytes() >= header.getDataLength() + headerSize) {
                buf.readBytes(bytes);

                byte[] data = new byte[(int) header.getDataLength()];
                buf.readBytes(data);

                ByteArrayInputStream din = new ByteArrayInputStream(data);
                ObjectInputStream doin = new ObjectInputStream(din);
                MyContent content = (MyContent) doin.readObject();
            }
        }*/


    }
}


class ResponseMappingCallback {
    static ConcurrentHashMap<Long, CompletableFuture> mapping = new ConcurrentHashMap<>();

    public static void addCallBack(long requestId, CompletableFuture cb) {
        mapping.put(requestId, cb);
    }

    public static void runCallBack(PackMsg packMsg) {
        CompletableFuture future = mapping.get(packMsg.getHeader().getRequestId());
        future.complete(packMsg.getContent().getRes());
        removeCallBack(packMsg.getHeader().getRequestId());
    }

    private static void removeCallBack(long requestId) {
        mapping.remove(requestId);
    }
}


class ServerResponseHandler extends ChannelInboundHandlerAdapter {
    private Dispatcher dispatcher;

    public ServerResponseHandler(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    /* version 1
       ByteBuf buf = (ByteBuf) msg;

        int headerLength = 88;
        System.out.println("server handler message ~~~~~~");
        if (buf.readableBytes() > headerLength) {
            byte[] headerBytes = new byte[headerLength];
            buf.getBytes(0, headerBytes);

            ByteArrayInputStream in = new ByteArrayInputStream(headerBytes);
            ObjectInputStream oin = new ObjectInputStream(in);
            MyHeader header = (MyHeader) oin.readObject();
//            System.out.println(header.getDataLength());
//            System.out.println(header.getRequestId());


            if (buf.readableBytes() >= header.getDataLength() + headerLength) {
                buf.readBytes(headerBytes);

                byte[] data = new byte[(int) header.getDataLength()];
                buf.readBytes(data);

                ByteArrayInputStream din = new ByteArrayInputStream(data);
                ObjectInputStream doin = new ObjectInputStream(din);
                MyContent content = (MyContent) doin.readObject();

                ByteBuf sendBuf = PooledByteBufAllocator.DEFAULT.buffer(data.length + headerLength);
                sendBuf.writeBytes(headerBytes);
                sendBuf.writeBytes(data);
                ctx.writeAndFlush(sendBuf);
                System.out.println(content);
            }
        }*/


        PackMsg packMsg = (PackMsg) msg;

        System.out.println("server handler : message is " + packMsg.getContent().getArgs()[0]);


        //返回消息,
        // 1. 在当前线程执行IO,返回等操作
        // 2. eventloop的线程来执行
        String ioThreadName = Thread.currentThread().getName();

//        ctx.executor().execute(() -> {
        ctx.executor().parent().next().execute(() -> {

            //获取对象
            String serviceName = packMsg.getContent().getName();
            String methodName = packMsg.getContent().getMethod();
            Object[] args = packMsg.getContent().getArgs();
            Class<?>[] parameterTypes = packMsg.getContent().getParameterTypes();

            Object impl = dispatcher.get(serviceName);
            Class<?> clazz = impl.getClass();

            Object res = null;
            try {
                Method method = clazz.getMethod(methodName, parameterTypes);
                res = method.invoke(impl, args);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }


//            String executeThreadName = Thread.currentThread().getName();
            MyContent respContent = new MyContent();
//            String resp = "io thread: " + ioThreadName + ",　exec thread :　" + executeThreadName + ", from args: " + packMsg.getContent().getArgs()[0];
            respContent.setRes(res);

            byte[] body = SerDerUtil.ser(respContent);

            MyHeader respHeader = new MyHeader();
            respHeader.setRequestId(packMsg.getHeader().getRequestId());
            respHeader.setFlag(0x24242424);
            respHeader.setDataLength(body.length);

            byte[] header = SerDerUtil.ser(respHeader);


            ByteBuf buf = PooledByteBufAllocator.DEFAULT.buffer(header.length + body.length);

            buf.writeBytes(header);
            buf.writeBytes(body);

            ctx.writeAndFlush(buf);

        });


    }
}

class ServerDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf buf, List<Object> list) throws Exception {
        int headerSize = 88;
        while (buf.readableBytes() > headerSize) {
            byte[] bytes = new byte[headerSize];
            buf.getBytes(buf.readerIndex(), bytes);
            MyHeader header = SerDerUtil.der(bytes, MyHeader.class);

//            if (header.getFlag() == 0x14141414) {
//                System.out.println("request ");
//            } else if (header.getFlag() == 0x24242424) {
//                System.out.println("response");
//            }

            if (buf.readableBytes() >= header.getDataLength() + headerSize) {
                byte[] data = new byte[(int) header.getDataLength()];
                buf.readBytes(bytes);
                buf.readBytes(data);

                MyContent content = SerDerUtil.der(data, MyContent.class);
                list.add(new PackMsg(header, content));
            } else {
                break;
            }
        }
    }
}


class PackMsg {
    private MyHeader header;
    private MyContent content;


    public PackMsg(MyHeader header, MyContent content) {
        this.header = header;
        this.content = content;
    }

    public MyHeader getHeader() {
        return header;
    }

    public void setHeader(MyHeader header) {
        this.header = header;
    }

    public MyContent getContent() {
        return content;
    }

    public void setContent(MyContent content) {
        this.content = content;
    }
}


class SerDerUtil {
    static ByteArrayOutputStream out = new ByteArrayOutputStream();
    static ByteArrayInputStream in;

    public synchronized static byte[] ser(Object obj) {
        out.reset();
        ObjectOutputStream oout = null;
        try {
            oout = new ObjectOutputStream(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            oout.writeObject(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] msgBody = out.toByteArray();
        return msgBody;
    }

    public synchronized static <T> T der(byte[] bytes, Class<T> calzz) {
        in = new ByteArrayInputStream(bytes);
        ObjectInputStream oin = null;
        try {
            oin = new ObjectInputStream(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return (T) oin.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}


class MyCar implements Car {
    @Override
    public String ooxx(String msg) {
//        System.out.println("server get client : " + msg);
        return "server recv : " + msg;
    }
}

class MyFly implements Fly {
    @Override
    public void xxoo(String msg) {
        System.out.println("fly server received message :　" + msg);
    }
}


