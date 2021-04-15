package org.example.config;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import javax.lang.model.element.VariableElement;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @version 1.0.0
 * @auther Alexander Zero
 * @date 2021/4/15
 */
public class ZkUtils {
    private static ZooKeeper zk;
    private static String servers = "192.168.1.109:2181,192.168.1.110:2181,192.168.1.111:2181,192.168.1.113:2181/testConf";
    private static CountDownLatch latch = new CountDownLatch(1);
    private static DefaultWatch watch = new DefaultWatch();

    public static ZooKeeper getZk() {
        try {
            watch.setLatch(latch);
            zk = new ZooKeeper(servers, 1000, watch);
            latch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return zk;
    }
}
