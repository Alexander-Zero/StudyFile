package io.basic;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * @version 1.0.0
 * @auther Alexander Zero
 * @date 2021/3/27
 */
public class SocketMultiplexingSingleThreadv1 {

    //服务端
    private ServerSocketChannel server = null;
    //多路复用器 select/poll/epoll
    private Selector selector = null;
    private int port = 9090;

    public void initServer() {
        try {

            server = ServerSocketChannel.open();
            server.configureBlocking(false);
            server.bind(new InetSocketAddress(port));


            selector = Selector.open();
            server.register(selector, SelectionKey.OP_ACCEPT);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void start() {
        initServer();

        System.out.println("服务器启动了~~~");
        try {
            while (true) {
                Set<SelectionKey> keys = selector.keys();
                System.out.println("keys : " + keys);

                while (selector.select() > 0) {

                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectionKeys.iterator();


                    while (iterator.hasNext()) {

                        SelectionKey next = iterator.next();
                        iterator.remove();

                        if (next.isAcceptable()) {

                            acceptableHandler(next);

                        } else if (next.isReadable()) {

                            readableHandler(next);

                        }
                    }

                }


            }
        } catch (Exception e) {

        }

    }

    private void readableHandler(SelectionKey key) {
        SocketChannel client = (SocketChannel) key.channel();

        ByteBuffer buf = (ByteBuffer) key.attachment();
        buf.clear();
        int read = 0;
        try {

            while (true) {
                read = client.read(buf);
                //能读到数据,然后将读到的数据发送给客户端
                if (read > 0) {
                    buf.flip();
                    while (buf.hasRemaining()) {
                        client.write(buf);
                    }
                } else if (read == 0) {
                    break;
                } else {
                    client.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void acceptableHandler(SelectionKey key) {
        try {
            ServerSocketChannel server = (ServerSocketChannel) key.channel();
            SocketChannel client = server.accept();
            client.configureBlocking(false);

            ByteBuffer buffer = ByteBuffer.allocateDirect(4096);
            client.register(selector, SelectionKey.OP_READ, buffer);
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("新客户端连接上了:　remote host is : " + client.getRemoteAddress());
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        SocketMultiplexingSingleThreadv1 service = new SocketMultiplexingSingleThreadv1();
        service.start();
    }

}
