package io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @version 1.0.0
 * @auther Alexander Zero
 * @date 2021/3/27
 */
public class NIOClient {
    public static void main(String[] args) throws IOException {
        SocketChannel client = SocketChannel.open();
        client.connect(new InetSocketAddress("localhost", 9090));

        InputStream in = System.in;
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        while (true) {
            String line = reader.readLine();
            if (line != null && !line.isEmpty()) {
                if ("q".equals(line)) {
                    reader.close();
                    in.close();
                    client.close();
                    break;
                }
                ByteBuffer wrap = ByteBuffer.wrap(line.getBytes());
                client.write(wrap);
            }
        }


    }

}
