package io.rpcdemo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.rpcdemo.proxy.MyProxy;
import io.rpcdemo.rpc.Dispatcher;
import io.rpcdemo.rpc.protocol.MyContent;
import io.rpcdemo.rpc.protocol.MyHeader;
import io.rpcdemo.rpc.transport.MyHttpRpcHandler;
import io.rpcdemo.rpc.transport.ServerDecoder;
import io.rpcdemo.rpc.transport.ServerResponseHandler;
import io.rpcdemo.service.Car;
import io.rpcdemo.service.Fly;
import io.rpcdemo.service.MyCar;
import io.rpcdemo.service.MyFly;
import io.rpcdemo.util.SerDerUtil;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
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

    @Test
    public void startHttpServer() {
        Dispatcher dispatcher = Dispatcher.getInstance();

        dispatcher.register(Car.class.getName(), new MyCar());
        dispatcher.register(Fly.class.getName(), new MyFly());


        //tomcat 或 jetty
        Server server = new Server(new InetSocketAddress("localhost", 9090));
        ServletContextHandler handler = new ServletContextHandler(server, "/");
        server.setHandler(handler);

        handler.addServlet(MyHttpRpcHandler.class, "/*");

        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //模拟server
    @Test
    public void startServer() {

        Dispatcher dispatcher = Dispatcher.getInstance();

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
//                pipeline.addLast(new ServerDecoder());
//                pipeline.addLast(new ServerResponseHandler(dispatcher));
                //http协议
                pipeline.addLast(new HttpServerCodec());
                pipeline.addLast(new HttpObjectAggregator(1024 * 512));
                pipeline.addLast(new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        FullHttpRequest request = (FullHttpRequest) msg;
                        System.out.println(request);

                        ByteBuf content = request.content();
                        byte[] data = new byte[content.readableBytes()];
                        content.readBytes(data);
                        ObjectInputStream oin = new ObjectInputStream(new ByteArrayInputStream(data));
                        MyContent myContent = (MyContent) oin.readObject();


                        //获取对象
                        String serviceName = myContent.getName();
                        String methodName = myContent.getMethod();
                        Object[] args = myContent.getArgs();
                        Class<?>[] parameterTypes = myContent.getParameterTypes();

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


                        MyContent respContent = new MyContent();
                        respContent.setRes(res);
                        byte[] respBytes = SerDerUtil.ser(respContent);

                        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_0,
                                HttpResponseStatus.OK,
                                Unpooled.copiedBuffer(respBytes));

                        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, respBytes.length);

                        ctx.writeAndFlush(response);

                    }
                });
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
}