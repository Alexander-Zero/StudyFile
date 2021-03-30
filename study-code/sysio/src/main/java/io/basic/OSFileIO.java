package io.basic;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @version 1.0.0
 * @auther Alexander Zero
 * @date 2021/3/27
 */
public class OSFileIO {

    static byte[] data = "1234567890".getBytes();
    static String path = "/root/testfileio/out.txt";

    public static void main(String[] args) throws IOException, InterruptedException {
        switch (args[0]) {
            case "0":
                testBasicFileIO();
                break;
            case "1":
                testBufferedFileIO();
                break;
            case "2":
                testRandomAccessFileIO();
                break;
            case "3":
                whatByteBuffer();
            default:
        }

    }


    private static void testBasicFileIO() throws IOException, InterruptedException {
        File file = new File(path);
        FileOutputStream os = new FileOutputStream(file);
        while (true) {
            Thread.sleep(10);
            os.write(data);
        }
    }


    private static void testBufferedFileIO() throws IOException, InterruptedException {
        File file = new File(path);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
        while (true) {
            Thread.sleep(10);
            bos.write(data);
        }
    }


    private static void testRandomAccessFileIO() throws InterruptedException, IOException {
        RandomAccessFile raf = new RandomAccessFile(path, "rw");
        raf.write("abcTest\n".getBytes());
        raf.write("xxddTest\n".getBytes());
        System.out.println("write-----------");
        System.in.read();

        raf.seek(4);
        raf.write("BBBB".getBytes());
        System.out.println("seek--------------");
        System.in.read();

        FileChannel fileChannel = raf.getChannel();
        MappedByteBuffer map = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, 4096);

        map.put("@@@".getBytes());
        System.out.println("map-put----------");
        System.in.read();

        map.force();// flush


        raf.seek(0);

        ByteBuffer buffer = ByteBuffer.allocate(4096);


        int read = fileChannel.read(buffer);
        System.out.println(buffer);
        buffer.flip();
        System.out.println(buffer);


        for (int i = 0; i < buffer.limit(); i++) {
            Thread.sleep(200);
            System.out.println((char) buffer.get(i));
        }


    }


    private static void whatByteBuffer() {
        ByteBuffer buffer = ByteBuffer.allocateDirect(8192);

        System.out.println("position: " + buffer.position());
        System.out.println("limit: " + buffer.limit());
        System.out.println("capacity: " + buffer.capacity());
        System.out.println("mark: " + buffer.mark());

        buffer.put("123".getBytes());
        System.out.println("-------------------put-------------------");

        System.out.println("position: " + buffer.position());
        System.out.println("limit: " + buffer.limit());
        System.out.println("capacity: " + buffer.capacity());
        System.out.println("mark: " + buffer.mark());

        buffer.flip();
        System.out.println("-------------------flip-------------------");

        System.out.println("position: " + buffer.position());
        System.out.println("limit: " + buffer.limit());
        System.out.println("capacity: " + buffer.capacity());
        System.out.println("mark: " + buffer.mark());

        byte b = buffer.get();
        System.out.println("-------------------get-------------------");

        System.out.println("position: " + buffer.position());
        System.out.println("limit: " + buffer.limit());
        System.out.println("capacity: " + buffer.capacity());
        System.out.println("mark: " + buffer.mark());

        buffer.compact();
        System.out.println("-------------------compact-------------------");

        System.out.println("position: " + buffer.position());
        System.out.println("limit: " + buffer.limit());
        System.out.println("capacity: " + buffer.capacity());
        System.out.println("mark: " + buffer.mark());

        buffer.clear();
        System.out.println("-------------------clear-------------------");

        System.out.println("position: " + buffer.position());
        System.out.println("limit: " + buffer.limit());
        System.out.println("capacity: " + buffer.capacity());
        System.out.println("mark: " + buffer.mark());

    }


}
