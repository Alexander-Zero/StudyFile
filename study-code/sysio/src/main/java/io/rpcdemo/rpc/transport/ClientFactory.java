package io.rpcdemo.rpc.transport;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.rpcdemo.rpc.ResponseMappingCallback;
import io.rpcdemo.rpc.protocol.MyContent;
import io.rpcdemo.rpc.protocol.MyHeader;
import io.rpcdemo.util.SerDerUtil;

import java.net.InetSocketAddress;
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

    }
}
