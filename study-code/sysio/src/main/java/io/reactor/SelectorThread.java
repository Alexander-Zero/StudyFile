package io.reactor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Alexander Zero
 * @version 1.0.0
 * @date 2021/3/29
 */
public class SelectorThread implements Runnable {

    Selector selector = null;
    LinkedBlockingQueue<Channel> lbq = new LinkedBlockingQueue<>();
    SelectorThreadGroup group;

    public SelectorThread(SelectorThreadGroup group) {
        this.group = group;
        try {
            selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        //Loop
        while (true) {
            try {

                //step 1, select
//                System.out.println(Thread.currentThread().getName() + " before select :" + selector.keys().size());
                int nums = selector.select();//阻塞,只是那一刻的FDS, wakeup
//                System.out.println(Thread.currentThread().getName() + " after select :" + selector.keys().size());


                //step 2
                if (nums > 0) {
                    Set<SelectionKey> keys = selector.selectedKeys();
                    Iterator<SelectionKey> iter = keys.iterator();
                    while (iter.hasNext()) {  //线性处理
                        SelectionKey key = iter.next();
                        iter.remove();

                        if (key.isAcceptable()) {//多线程注册到哪?
                            acceptHandler(key);
                        } else if (key.isReadable()) {
                            readHandler(key);
                        } else if (key.isWritable()) {

                        }
                    }

                }


                //step 3 runAllTask
                if (!lbq.isEmpty()) {
                    Channel c = lbq.take();
                    if (c instanceof ServerSocketChannel) {
                        ServerSocketChannel server = (ServerSocketChannel) c;
                        server.register(this.selector, SelectionKey.OP_ACCEPT);
                        System.out.println(Thread.currentThread().getName() + " register a server :" + server.getLocalAddress());
                    } else if (c instanceof SocketChannel) {
                        SocketChannel client = (SocketChannel) c;
                        ByteBuffer buffer = ByteBuffer.allocate(4096);
                        client.register(selector, SelectionKey.OP_READ, buffer);
                        System.out.println(Thread.currentThread().getName() + " register a client :" + client.getRemoteAddress());
                    }
                }


            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void readHandler(SelectionKey key) {
        ByteBuffer buffer = (ByteBuffer) key.attachment();
        SocketChannel client = (SocketChannel) key.channel();
        buffer.clear();

        while (true) {
            try {
                int num = client.read(buffer);
                if (num > 0) {
                    buffer.flip();//读到的内容直接写出
                    while (buffer.hasRemaining()) {
                        client.write(buffer);
                    }
                    buffer.clear();
                } else if (num == 0) {
                    break;
                } else if (num < 0) {
                    //客户端断开了
                    System.out.println("client: " + client.getRemoteAddress() + " closed.....");
                    key.cancel();
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void acceptHandler(SelectionKey key) {
        System.out.println(Thread.currentThread().getName() + " accept client ~~~");
        ServerSocketChannel server = (ServerSocketChannel) key.channel();
        try {
            SocketChannel client = server.accept();
            client.configureBlocking(false);

            group.nextSelector(client);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
