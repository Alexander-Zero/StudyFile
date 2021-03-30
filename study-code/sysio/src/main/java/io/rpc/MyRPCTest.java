package io.rpc;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.junit.Test;

import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.ContentHandler;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

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
        NioEventLoopGroup boss = new NioEventLoopGroup(1);
        NioEventLoopGroup worker = boss;

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss, worker);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel server) throws Exception {
                System.out.println("server accpet a client, this port is " + server.remoteAddress().getPort());
                ChannelPipeline pipeline = server.pipeline();
                pipeline.addLast(new ServerDecoder());
                pipeline.addLast(new ServerResponseHandler());
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
    public void get() {

        int size = 20;
//        Thread[] threads = new Thread[size];
//
//        for (int i = 0; i < size; i++) {
//            threads[i] = new Thread(() -> {
//                Car car = proxyGet(Car.class); //动态代理
//                car.ooxx("hello");
//            });
//        }
//
//        for (Thread thread : threads) {
//            thread.start();
//        }

        Car car = proxyGet(Car.class); //动态代理
        car.ooxx("hello");
    }

    public static <T> T proxyGet(Class<T> interfaceInfo) {
        //动态代理
        ClassLoader loader = interfaceInfo.getClassLoader();
        Class<?>[] methodInfo = {interfaceInfo};

        return (T) Proxy.newProxyInstance(loader, methodInfo, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //如何设计consumer对provider的调用过程

                //1. 调用服务, 方法,参数  ==> 封装成message
                String name = interfaceInfo.getName();
                String methodName = method.getName();
                Class<?>[] parameterTypes = method.getParameterTypes();
                MyContent body = new MyContent();

                body.setName(name);
                body.setMethod(methodName);
                body.setParameterTypes(parameterTypes);
                body.setArgs(args);

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ObjectOutputStream oout = new ObjectOutputStream(out);
                oout.writeObject(body);
                byte[] msgBody = out.toByteArray();


                //2. requestID +　Message, 本地需要缓存requestID
                //协议
                MyHeader header = createHeader(msgBody);

                out.reset();
                oout = new ObjectOutputStream(out);
                oout.writeObject(header);

                byte[] headerMsg = out.toByteArray();

                //3. 连接池 ::　取得连接
                ClientFactory factory = ClientFactory.getInstance();
                NioSocketChannel channel = factory.getClient(new InetSocketAddress("localhost", 9090));

                //4. 发送 -> 走 IO  out -> 走netty

                CountDownLatch latch = new CountDownLatch(1);
                long requestId = header.getRequestId();
                ResponseHandler.addCallBack(requestId, new Runnable() {
                    @Override
                    public void run() {
                        latch.countDown();
                    }
                });

                ByteBuf buf = PooledByteBufAllocator.DEFAULT.buffer(headerMsg.length + headerMsg.length);
                System.out.println("headerMsg length is " + headerMsg.length);
                buf.writeBytes(headerMsg);
                buf.writeBytes(msgBody);
                ChannelFuture channelFuture = channel.writeAndFlush(buf);
                channelFuture.sync();

                channelFuture.channel().closeFuture().channel();

                latch.await();

                //5. IO in, 如何将代码往下执行, 睡眠/回调, 如何让线程停下来并继续
                // CountDownLatch

                return null;
            }


        });

    }

    public static MyHeader createHeader(byte[] msgBody) {
        MyHeader header = new MyHeader();
        int dataLength = msgBody.length;
        int flag = 0x14141414;//具体的协议
        long requestId = UUID.randomUUID().getMostSignificantBits();

        header.setFlag(flag);
        header.setRequestId(requestId);
        header.setDataLength(dataLength);


        return header;
    }


}

interface Car {
    public void ooxx(String msg);
}

interface Fly {
    public void xxoo(String msg);
}


class MyContent implements Serializable {
    private String name;
    private String method;
    private Class<?>[] parameterTypes;
    private Object[] args;
    private String res;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public String getRes() {
        return res;
    }

    public void setRes(String res) {
        this.res = res;
    }

    @Override
    public String toString() {
        return "MyContent{" +
                "name='" + name + '\'' +
                ", method='" + method + '\'' +
                ", parameterTypes=" + Arrays.toString(parameterTypes) +
                ", args=" + Arrays.toString(args) +
                '}';
    }
}


class MyHeader implements Serializable {
    private int flag;//协议,具体的协议
    private long requestId;
    private long dataLength;

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public long getDataLength() {
        return dataLength;
    }

    public void setDataLength(long dataLength) {
        this.dataLength = dataLength;
    }
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

    int poolSize = 1;
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
                       // pi
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
        ByteBuf buf = (ByteBuf) msg;
        int headerLength = 88;
        System.out.println("client handler message");
        if (buf.readableBytes() > headerLength) {
            byte[] bytes = new byte[headerLength];
            buf.getBytes(0, bytes);

            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            ObjectInputStream oin = new ObjectInputStream(in);
            MyHeader header = (MyHeader) oin.readObject();
            System.out.println("requestId is " + header.getRequestId());
            //TODO
            ResponseHandler.runCallBack(header.getRequestId());

            if (buf.readableBytes() >= header.getDataLength() + headerLength) {
                buf.readBytes(bytes);

                byte[] data = new byte[(int) header.getDataLength()];
                buf.readBytes(data);

                ByteArrayInputStream din = new ByteArrayInputStream(data);
                ObjectInputStream doin = new ObjectInputStream(din);
                MyContent content = (MyContent) doin.readObject();


            }
        }
    }
}


class ResponseHandler {
    static ConcurrentHashMap<Long, Runnable> mapping = new ConcurrentHashMap<>();

    public static void addCallBack(long requestId, Runnable cb) {
        mapping.put(requestId, cb);
    }

    public static void runCallBack(long requestId) {
        Runnable runnable = mapping.get(requestId);
        runnable.run();
        removeCallBack(requestId);
    }

    private static void removeCallBack(long requestId) {
        mapping.remove(requestId);
    }
}


class ServerResponseHandler extends ChannelInboundHandlerAdapter {
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

        System.out.println("server handler " + packMsg.getContent().getArgs()[0]);


        //返回消息,
        // 1. 在当前线程执行IO,返回等操作
        // 2. eventloop的线程来执行
        String ioThreadName = Thread.currentThread().getName();

//        ctx.executor().execute(() -> {
        ctx.executor().parent().next().execute(() -> {
            String executeThreadName = Thread.currentThread().getName();
            MyContent respContent = new MyContent();
            String resp = "io thread: " + ioThreadName + "　exec thread :　" + executeThreadName + " from args " + packMsg.getContent().getArgs()[0];
            respContent.setRes(resp);

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
        int headerLength = 88;
        System.out.println("client handler message");
        while (buf.readableBytes() > headerLength) {
            byte[] bytes = new byte[headerLength];
            buf.getBytes(0, bytes);

            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            ObjectInputStream oin = new ObjectInputStream(in);
            MyHeader header = (MyHeader) oin.readObject();
            System.out.println("requestId is " + header.getRequestId());
            //TODO
            ResponseHandler.runCallBack(header.getRequestId());

            if (buf.readableBytes() >= header.getDataLength() + headerLength) {
                byte[] data = new byte[(int) header.getDataLength()];
                buf.readBytes(headerLength);
                ByteArrayInputStream in1 = new ByteArrayInputStream(data);
                ObjectInputStream oin1 = new ObjectInputStream(in1);
                MyContent content = (MyContent) oin1.readObject();
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
}