package org.example.v2.lock;

import org.apache.zookeeper.ZooKeeper;
import org.example.config.ZkUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @version 1.0.0
 * @auther Alexander Zero
 * @date 2021/4/17
 */
public class LockTest {
    private ZooKeeper zooKeeper;

    @Before
    public void conn() {
        zooKeeper = ZkUtils.getZk();
    }

    @After
    public void close() {
        try {
            zooKeeper.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void test() {

        for (int i = 0; i < 2000; i++) {
            new Thread(() -> {
                WatcherCallBack wb = new WatcherCallBack();
                wb.setZooKeeper(zooKeeper);
                String threadName = Thread.currentThread().getName();
                wb.setThreadName(threadName);


                wb.tryLock();

                System.out.println(threadName + " is working");

                wb.unLock();

            }).start();
        }


        //阻塞等待
        while (true) {

        }
    }


}
