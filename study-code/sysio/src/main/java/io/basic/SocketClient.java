package io.basic;

import java.io.*;
import java.net.Socket;

/**
 * @version 1.0.0
 * @auther Alexander Zero
 * @date 2021/3/27
 */
public class SocketClient {
    public static void main(String[] args) throws IOException {
        Socket client = new Socket("localhost", 9090);
        OutputStream out = client.getOutputStream();
        InputStream in = System.in;

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        while (true) {
            String line = reader.readLine();
            if (line != null && !line.isEmpty()) {
                in.close();
                out.close();
                client.close();
                break;
            }
            byte[] bytes = line.getBytes();
            out.write(bytes);


        }

    }

}
