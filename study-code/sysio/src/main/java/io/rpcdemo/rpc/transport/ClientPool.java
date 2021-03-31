package io.rpcdemo.rpc.transport;

import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author Alexander Zero
 * @version 1.0.0
 * @date 2021/3/31
 */
public class ClientPool {

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