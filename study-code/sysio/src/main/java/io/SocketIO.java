package io;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @version 1.0.0
 * @auther Alexander Zero
 * @date 2021/3/27
 */
public class SocketIO {
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(9090, 20);
        System.out.println("step 1: new serverSocket(9090) ");

        while (true) {
            Socket client = server.accept();//阻塞
            System.out.println("step 2:  client\t" + client.getPort());
            new Thread(() -> {
                InputStream is = null;
                try {
                    is = client.getInputStream();

                    byte[] buf = new byte[512];
                    while (true) {
                        int offset = is.read(buf);
                        if (offset == -1) {
                            is.close();
                            client.close();
                            break;
                        }
                        String content = new String(buf, 0, offset);
                        System.out.println(content);
                    }
                    System.out.println("客户端断开了");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

        }


    }

}
