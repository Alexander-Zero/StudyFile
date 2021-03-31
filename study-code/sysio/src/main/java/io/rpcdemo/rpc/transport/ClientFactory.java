package io.rpcdemo.rpc.transport;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.Random;
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
