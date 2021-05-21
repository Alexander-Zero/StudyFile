package org.example.v2.config;

import org.apache.zookeeper.ZooKeeper;
import org.example.config.ZkUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sun.text.normalizer.UBiDiProps;

/**
 * @version 1.0.0
 * @auther Alexander Zero
 * @date 2021/4/17
 */
public class TestConfig {

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
        WatcherCallBack wb = new WatcherCallBack();
        MyConfig config = new MyConfig();
        wb.setZooKeeper(zooKeeper);
        wb.setMyConfig(config);

        wb.await();


        while (true) {
            if (config.getConfig().equals("")) {
                System.out.println("service stop");
                wb.await();
            } else {
                System.out.println(config.getConfig());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }


    }


}
