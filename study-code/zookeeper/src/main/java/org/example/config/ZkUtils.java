package org.example.config;

import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

/**
 * @author Alexander Zero
 * @version 1.0.0
 * @date 2021/4/16
 */
public class ZkUtils {
    private static ZooKeeper zooKeeper;

    public static ZooKeeper getZk() {
        CountDownLatch latch = new CountDownLatch(1);
        DefaultWatch defaultWatch = new DefaultWatch(latch);
        String servers = "192.168.1.109:2181,192.168.1.110:2181,192.168.1.111:2181,192.168.1.113:2181/testLock";
//        String servers = "192.168.146.128:2181,192.168.146.129:2181,192.168.146.130:2181,192.168.146.132:2181/testConfig";
        try {
            zooKeeper = new ZooKeeper(servers, 1000, defaultWatch);
            latch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return zooKeeper;
    }
}
