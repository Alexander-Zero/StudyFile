package io.rpcdemo.rpc.transport;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
<<<<<<< HEAD
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
=======
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
>>>>>>> a730da755158b3bc7cd5ecdb8606422a33da8146
import io.rpcdemo.rpc.ResponseMappingCallback;
import io.rpcdemo.rpc.protocol.MyContent;
import io.rpcdemo.rpc.protocol.MyHeader;
import io.rpcdemo.util.SerDerUtil;

<<<<<<< HEAD
import java.net.InetSocketAddress;
=======
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.*;
>>>>>>> a730da755158b3bc7cd5ecdb8606422a33da8146
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Alexander Zero
 * @version 1.0.0
 * @date 2021/3/31
 */
public class ClientFactory {

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


    //TODO 并发情况需要 特别谨慎
    public NioSocketChannel getClient(InetSocketAddress address) {
        ClientPool clientPool = outboxes.get(address);

        if (clientPool == null) {
            synchronized (outboxes) {
                if (clientPool == null) {
                    outboxes.putIfAbsent(address, new ClientPool(poolSize));
                    clientPool = outboxes.get(address);
                }
            }
        }

        int i = r.nextInt(poolSize);
        if (clientPool.clients[i] != null && clientPool.clients[i].isActive()) {
            return clientPool.clients[i];
        }

        synchronized (clientPool.lock[i]) {
            if (clientPool.clients[i] == null || !clientPool.clients[i].isActive()) {
                clientPool.clients[i] = create(address);
            }
        }
        return clientPool.clients[i];
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

<<<<<<< HEAD

    public static CompletableFuture transport(MyContent content) throws InterruptedException {
        byte[] msgBody = SerDerUtil.ser(content);

        //2. requestID +　Message, 本地需要缓存requestID
        //TODO 协议可能会改变
        MyHeader header = MyHeader.createHeader(msgBody);
        byte[] headerMsg = SerDerUtil.ser(header);

//        System.out.println("headerMsg" + headerMsg.length);

        //3. 连接池 ::　取得连接
        NioSocketChannel channel = factory.getClient(new InetSocketAddress("localhost", 9090));

        //4. 发送 -> 走 IO  out -> 走netty
        long requestId = header.getRequestId();
        //设置返回值
        CompletableFuture<Object> res = new CompletableFuture<>();
        ResponseMappingCallback.addCallBack(requestId, res);

        ByteBuf buf = PooledByteBufAllocator.DEFAULT.buffer(headerMsg.length + headerMsg.length);
        buf.writeBytes(headerMsg);
        buf.writeBytes(msgBody);
        ChannelFuture channelFuture = channel.writeAndFlush(buf);
        channelFuture.sync();

        channelFuture.channel().closeFuture().channel();

        return res;

=======
    public static CompletableFuture transport(MyContent content) {

        CompletableFuture<Object> res = new CompletableFuture<>();
        //rpc 或者 http
        String type = "http";

        if (type.equals("rpc")) {
            byte[] msgBody = SerDerUtil.ser(content);
            //2. requestID +　Message, 本地需要缓存requestID
            //协议
            MyHeader header = MyHeader.createHeader(msgBody);
            byte[] headerMsg = SerDerUtil.ser(header);
            //3. 连接池 ::　取得连接
            ClientFactory factory = ClientFactory.getInstance();
            NioSocketChannel channel = factory.getClient(new InetSocketAddress("localhost", 9090));
            //4. 发送 -> 走 IO  out -> 走netty
            long requestId = header.getRequestId();
            //设置返回值
            ResponseMappingCallback.addCallBack(requestId, res);
            ByteBuf buf = PooledByteBufAllocator.DEFAULT.buffer(headerMsg.length + headerMsg.length);
            buf.writeBytes(headerMsg);
            buf.writeBytes(msgBody);
            channel.writeAndFlush(buf);
        } else {
            //url发送
            urlTs(content, res);
            //netty发送
//            nettyTs(content, res);
        }
        return res;
    }

    private static void nettyTs(MyContent content, CompletableFuture<Object> res) {
        NioEventLoopGroup group = new NioEventLoopGroup(1);
        Bootstrap bootstrap = new Bootstrap();
        Bootstrap client = bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel c) throws Exception {
                        c.pipeline()
                                .addLast(new HttpClientCodec())
                                .addLast(new HttpObjectAggregator(1024 * 512))
                                .addLast(new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        FullHttpResponse response = (FullHttpResponse) msg;
                                        ByteBuf respConetent = response.content();
                                        byte[] data = new byte[respConetent.readableBytes()];
                                        respConetent.readBytes(data);

                                        MyContent content = SerDerUtil.der(data, MyContent.class);
                                        res.complete(content.getRes());
                                    }
                                });
                    }
                });
        ChannelFuture future = client.connect("localhost", 9090);
        Channel channel = future.channel();
        byte[] body = SerDerUtil.ser(content);
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_0,
                HttpMethod.POST,
                "/",
                Unpooled.copiedBuffer(body));

        request.headers().set(HttpHeaderNames.CONTENT_LENGTH,body.length);

        channel.writeAndFlush(request);//发送request

    }

    private static void urlTs(MyContent content, CompletableFuture<Object> res) {
        Object result = null;
        try {

            URL url = new URL("http://localhost:9090/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //post
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            //data
            OutputStream out = conn.getOutputStream();
            ObjectOutputStream oout = new ObjectOutputStream(out);
            oout.writeObject(content);

            if (conn.getResponseCode() == 200) {
                InputStream in = conn.getInputStream();
                ObjectInputStream oin = new ObjectInputStream(in);
                MyContent respContent = (MyContent) oin.readObject();
                result = respContent.getRes();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        res.complete(result);
>>>>>>> a730da755158b3bc7cd5ecdb8606422a33da8146
    }
}
