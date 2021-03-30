package io.basic;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @version 1.0.0
 * @auther Alexander Zero
 * @date 2021/3/27
 */
public class SocketMultiplexingSingleThreadV2 {


    private ServerSocketChannel server = null;
    private Selector selector = null;

    private void initServer() {
        try {
            server = ServerSocketChannel.open();
            selector = Selector.open();
            server.configureBlocking(false);
            server.bind(new InetSocketAddress(9090));
            server.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("服务器启动了~~~~~~~~~~~");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws IOException {
        SocketMultiplexingSingleThreadV2 service = new SocketMultiplexingSingleThreadV2();
        service.start();
    }

    private void start() throws IOException {
        initServer();
        while (true) {
            if (selector.select() > 0) {
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iter = keys.iterator();
                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    iter.remove();
                    if (key.isAcceptable()) {
                        acceptHandler(key);
                    } else if (key.isReadable()) {
                        readHandler(key);
                    } else if (key.isWritable()) {
                        writeHandler(key);
                    }
                }
            }
        }
    }

    private void writeHandler(SelectionKey key) {
        new Thread(() -> {
            System.out.println("writeHandler~~~~~~~~~~~~~~~~");
            SocketChannel client = (SocketChannel) key.channel();
            ByteBuffer buf = (ByteBuffer) key.attachment();
            buf.flip();
            while (buf.hasRemaining()) {
                try {
                    client.write(buf);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            buf.clear();
            key.cancel();//key always is writable;
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void readHandler(SelectionKey key) {
        new Thread(() -> {
            System.out.println("readHandler~~~~~~~~~~");
            SocketChannel client = (SocketChannel) key.channel();
            ByteBuffer buf = (ByteBuffer) key.attachment();
            buf.clear();
            int read = 0;

            try {
                while (true) {
                    read = client.read(buf);
                    if (read > 0) {
                        client.register(selector, SelectionKey.OP_WRITE, buf);
                    } else if (read == 0) {
                        break;
                    } else if (read < 0) {
                        client.close();
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void acceptHandler(SelectionKey key) {
        try {
            ServerSocketChannel server = (ServerSocketChannel) key.channel();
            SocketChannel client = server.accept();
            client.configureBlocking(false);
            ByteBuffer buf = ByteBuffer.allocateDirect(4096);
            client.register(selector, SelectionKey.OP_READ, buf);
            System.out.println("received a client: ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
