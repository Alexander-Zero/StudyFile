package io.rpcdemo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.rpcdemo.proxy.MyProxy;
import io.rpcdemo.rpc.Dispatcher;
import io.rpcdemo.rpc.transport.ServerDecoder;
import io.rpcdemo.rpc.transport.ServerResponseHandler;
import io.rpcdemo.service.Car;
import io.rpcdemo.service.Fly;
import io.rpcdemo.service.MyCar;
import io.rpcdemo.service.MyFly;
import org.junit.Test;

import java.io.IOException;
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


}





































