package io;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * @version 1.0.0
 * @auther Alexander Zero
 * @date 2021/3/27
 */
public class SocketNIO {
    public static void main(String[] args) throws IOException {

        LinkedList<SocketChannel> clients = new LinkedList<>();

        ServerSocketChannel server = ServerSocketChannel.open();
        server.bind(new InetSocketAddress("localhost", 9090));
        server.configureBlocking(false);

        while (true) {
            SocketChannel client = server.accept();

            if (client == null) {
                System.out.println("no client connect to server");
            } else {
                client.configureBlocking(false);
                int port = client.socket().getPort();
                System.out.println("client connect to server, port is " + port);
                clients.add(client);
            }

            ByteBuffer buffer = ByteBuffer.allocateDirect(4096);

            Iterator<SocketChannel> iterator = clients.iterator();

            while (iterator.hasNext()) {
                SocketChannel next = iterator.next();
                int read = next.read(buffer);
                //读取结束
                if (read == -1) {
                    next.close();
                    iterator.remove();
                } else if (read == 0) {
                    //todo do something
                } else if (read > 0) {
                    buffer.flip();
                    byte[] bytes = new byte[buffer.limit()];
                    buffer.get(bytes);
                    String string = new String(bytes);
                    System.out.println("port received : " + string);
                    buffer.clear();
                }
            }
        }
    }
}
