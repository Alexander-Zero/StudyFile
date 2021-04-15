package org.example.config;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.concurrent.CountDownLatch;

/**
 * @version 1.0.0
 * @auther Alexander Zero
 * @date 2021/4/15
 */
public class DefaultWatch implements Watcher {
    private CountDownLatch latch;

    public CountDownLatch getLatch() {
        return latch;
    }

    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void process(WatchedEvent event) {
        System.out.println(event);
        Event.KeeperState state = event.getState();
        if (state == Event.KeeperState.SyncConnected) {
            latch.countDown();
        }

    }
}
