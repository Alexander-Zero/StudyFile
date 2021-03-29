package io.reactor;

import com.sun.security.ntlm.Server;

import java.net.InetSocketAddress;
import java.nio.channels.Channel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Alexander Zero
 * @version 1.0.0
 * @date 2021/3/29
 */
public class SelectorThreadGroup {//天生都是boss
    SelectorThread[] bossSelectors;
    ServerSocketChannel server = null;
    AtomicInteger xid = new AtomicInteger(0);

    SelectorThreadGroup workerGroup = this;

    public void setWorker(SelectorThreadGroup workerGroup) {
        this.workerGroup = workerGroup;
    }


    public SelectorThreadGroup(int nThread) {
        bossSelectors = new SelectorThread[nThread];
        for (int i = 0; i < bossSelectors.length; i++) {
            bossSelectors[i] = new SelectorThread(this);
            //线程启动
            new Thread(bossSelectors[i]).start();

        }
    }

    public void bind(int port) {
        try {
            server = ServerSocketChannel.open();
            server.configureBlocking(false);
            server.bind(new InetSocketAddress(port));

            nextSelector(server);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void nextSelector(Channel c) throws ClosedChannelException {
        SelectorThread st;
        if (c instanceof ServerSocketChannel) {
            st = bossNext();
        } else {
            st = next();
        }


        //1. 通过队列传递数据
        try {
            st.lbq.put(c);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //2 通过打断阻塞, 让线程自己完成注册
        st.selector.wakeup();
//        ServerSocketChannel server = (ServerSocketChannel) c;
        //selector.select()阻塞, 下面这个方法也会被阻塞
//        st.selector.wakeup();//让selector.select()立刻返回
//        server.register(st.selector, SelectionKey.OP_ACCEPT);
//        st.selector.wakeup();//让selector.select()立刻返回
    }


    private SelectorThread next() {
        int index = xid.incrementAndGet() % workerGroup.bossSelectors.length;
        return workerGroup.bossSelectors[index];
    }

    private SelectorThread bossNext() {
        int index = xid.incrementAndGet() % bossSelectors.length;
        return bossSelectors[index];
    }


}
