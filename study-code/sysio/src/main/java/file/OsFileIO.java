package file;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @version 1.0.0
 * @auther Alexander Zero
 * @date 2021/3/24
 */
public class OsFileIO {
    public static void main(String[] args) throws IOException {

        //基本IO
        String path  = "/temp/test.txt";
        FileOutputStream basicOut = new FileOutputStream(new File(path));
        //每次写 将字节数组写道pageCache中, 并调用system call flush到磁盘
        basicOut.write("helloWorld!".getBytes());


        //BufferIO
        BufferedOutputStream bufferedOut = new BufferedOutputStream(new FileOutputStream(new File(path)));
        //每次写将字节数组写道pageCache中, 当达到8K时调用system Call , flush到磁盘
        bufferedOut.write("helloWorld".getBytes());


        //随机读写,可实现断点续传
        RandomAccessFile raf = new RandomAccessFile(path,"rw");
        //每次写更新到PageCache, 并未flush到磁盘, 由系统控制
        raf.write("Abc".getBytes());
        raf.write("dsfas".getBytes());

        //可移动seek,随机读写
        raf.seek(2);
        raf.write("xxx".getBytes());

        FileChannel channel = raf.getChannel();

        //获取mmap, 即java程序的Linux heap空间外,有个mmap空间直接映射到pageCache
        MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, 4096);
        //此方法不调用系统调用
        map.put("abc".getBytes());

        //强制flush
        channel.force(false);

        ByteBuffer buffer = ByteBuffer.allocate(4096); // on jvm heap(of cource on Linux heap)
        ByteBuffer buffer1 = ByteBuffer.allocateDirect(4096);//Linux heap, off jvm heap

        channel.read(buffer); //读到buffer中
        //恢复到可读状态
        buffer.flip();
        //恢复到可写状态
        buffer.compact();

    }

}
