package org.example.config;

import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Alexander Zero
 * @version 1.0.0
 * @date 2021/4/16
 */
public class TestConf {
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
    public void confTest() {

        WatchCallBack wb = new WatchCallBack();
        MyConf conf = new MyConf();
        wb.setMyConf(conf);
        wb.setZooKeeper(zooKeeper);

        wb.await();

        while (true) {
            System.out.println("result : " + conf.getConf());
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }
}
