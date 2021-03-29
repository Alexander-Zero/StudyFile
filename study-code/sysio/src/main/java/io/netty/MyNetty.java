package io.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.*;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @version 1.0.0
 * @auther Alexander Zero
 * @date 2021/3/29
 */
public class MyNetty {


    @Test
    public void myByteBuf() {
        //池化
        //堆内/堆外
//        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(8, 20);
//        ByteBuf buf = UnpooledByteBufAllocator.DEFAULT.heapBuffer(8,20);
        ByteBuf buf = PooledByteBufAllocator.DEFAULT.buffer(8, 20);

        print(buf);

        buf.writeBytes(new byte[]{1, 2, 3, 4});
        print(buf);

        buf.writeBytes(new byte[]{1, 2, 3, 4});
        print(buf);

        buf.writeBytes(new byte[]{1, 2, 3, 4});
        print(buf);

        buf.writeBytes(new byte[]{1, 2, 3, 4});
        print(buf);

        buf.writeBytes(new byte[]{1, 2, 3, 4});
        print(buf);

        buf.writeBytes(new byte[]{1, 2, 3, 4});
        print(buf);


    }


    public static void print(ByteBuf buf) {
        System.out.println("is readable : " + buf.isReadable());
        System.out.println("reader index: " + buf.readerIndex());
        System.out.println("readable bytes: " + buf.readableBytes());
        System.out.println("is writable: " + buf.isWritable());
        System.out.println("write index: " + buf.writerIndex());
        System.out.println("writable bytes: " + buf.writableBytes());

        System.out.println("capacity: " + buf.capacity());
        System.out.println("max capacity: " + buf.maxCapacity());
        System.out.println("is direct: " + buf.isDirect());

        System.out.println("------------------------------");
        System.out.println("");

    }


    @Test
    public void loopExecutor() throws IOException {
        NioEventLoopGroup selector = new NioEventLoopGroup(2);
        selector.execute(() -> {
            while (true) {
                System.out.println("hello world 001");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        selector.execute(() -> {
            while (true) {
                System.out.println("hello world 002");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        System.in.read();
    }


    @Test
    public void clientMode() throws InterruptedException {
        NioEventLoopGroup thread = new NioEventLoopGroup(1);

        NioSocketChannel client = new NioSocketChannel();
        ChannelPipeline pipeline = client.pipeline();
        pipeline.addLast(new MyInHandler());

        thread.register(client);//epoll_ctl(4,add,3)

        ChannelFuture future = client.connect(new InetSocketAddress("192.168.1.100", 9090));
        ChannelFuture sync = future.sync();

        ByteBuf buf = Unpooled.copiedBuffer("hello server".getBytes());
        ChannelFuture send = client.writeAndFlush(buf);

        send.sync();

        sync.channel().closeFuture().sync();

        System.out.println("client over");
    }


    @Test
    public void serverMode() throws InterruptedException {
        NioServerSocketChannel server = new NioServerSocketChannel();
        NioEventLoopGroup thread = new NioEventLoopGroup(1);
        ChannelPipeline pipeline = server.pipeline();
        pipeline.addLast(new MyAcceptHandler(thread, new ChannelInit()));


        thread.register(server);
        ChannelFuture fu = server.bind(new InetSocketAddress(9090));

        fu.channel().closeFuture().sync();

        System.out.println("server closed");


    }


    @Test
    public void nettyClient() throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup(1);
        Bootstrap bootstrap = new Bootstrap();
        ChannelFuture connect = bootstrap
                .group(group)
                .channel(NioSocketChannel.class)
//                .handler(new ChannelInit())
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new MyInHandler());
                    }
                })
                .connect(new InetSocketAddress("192.168.1.100", 9090));

        Channel client = connect.sync().channel();

        ByteBuf buf = Unpooled.copiedBuffer("Hello THX".getBytes());
        ChannelFuture send = client.writeAndFlush(buf);
        send.sync();

        client.closeFuture().sync();

    }

    @Test
    public void nettyServer() throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup(1);
        ServerBootstrap bs = new ServerBootstrap();

        ChannelFuture bind = bs.group(group, group)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new MyInHandler());
                    }
                })
                .bind(new InetSocketAddress(9090));

        bind.sync().channel().closeFuture().sync();

    }
}

class MyAcceptHandler extends ChannelInboundHandlerAdapter {
    NioEventLoopGroup selector;
    ChannelHandler myInHandler;

    public MyAcceptHandler(NioEventLoopGroup selector, ChannelHandler readHandler) {
        this.selector = selector;
        this.myInHandler = readHandler;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("server register~~~~~~~~");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("server active");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        SocketChannel client = (SocketChannel) msg;
        ChannelPipeline pipeline = client.pipeline();
        pipeline.addLast(myInHandler);

        selector.register(client);


    }
}


//@ChannelHandler.Sharable
class MyInHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client register success--------");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client active~~~~~~~~");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
//        CharSequence str = buf.readCharSequence(buf.readableBytes(), CharsetUtil.UTF_8);
        CharSequence str = buf.getCharSequence(0, buf.readableBytes(), CharsetUtil.UTF_8);
        System.out.println(str);

        ctx.writeAndFlush(buf);
    }
}

@ChannelHandler.Sharable
class ChannelInit extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast(new MyInHandler());

        pipeline.remove(this);

    }
}