package org.example.lock;

import org.apache.zookeeper.ZKUtil;
import org.apache.zookeeper.ZooKeeper;
import org.example.config.ZkUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Alexander Zero
 * @version 1.0.0
 * @date 2021/4/16
 */
public class TestLock {
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
    public void lockTest() {

        for (int i = 0; i < 500; i++) {
            new Thread(() -> {
                WatchCallBack wb = new WatchCallBack();
                wb.setZooKeeper(zooKeeper);
                wb.setThreadName(Thread.currentThread().getName());


                wb.tryLock();

                System.out.println(Thread.currentThread().getName() + " is working ");

                wb.unLock();


            }).start();
        }

        while (true) {
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
