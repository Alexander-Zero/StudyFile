package socket;

import org.junit.Test;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Alexander Zero
 * @version 1.0.0
 * @date 2021/3/25
 */
public class ServerSocketProperties {

    @Test
    public void server() throws IOException {
        ServerSocket server = new ServerSocket();
        server.bind(new InetSocketAddress("localhost", 9090));
        while (true) {
            Socket client = server.accept();
            InputStream inputStream = client.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String s = bufferedReader.readLine();
            System.out.println(s);
        }
    }

    @Test
    public void client() throws IOException{
        Socket client = new Socket("localhost",9090);
        OutputStream outputStream = client.getOutputStream();
        outputStream.write("test".getBytes());
        outputStream.flush();
        client.close();

    }
}
