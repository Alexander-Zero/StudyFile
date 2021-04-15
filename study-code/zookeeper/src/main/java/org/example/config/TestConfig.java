package org.example.config;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @version 1.0.0
 * @auther Alexander Zero
 * @date 2021/4/15
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
    public void configTest() throws InterruptedException {
        WatchCallBack watchCallBack = new WatchCallBack();

        zooKeeper.exists("/AppConf", watchCallBack, watchCallBack, "Abc");
        watchCallBack.await();
        //1.不存在 ， 应该阻塞等待

        //2.存在，往后执行


        while (true) {
            System.out.println("循环出来");
            Thread.sleep(300);
        }

    }
}
  



