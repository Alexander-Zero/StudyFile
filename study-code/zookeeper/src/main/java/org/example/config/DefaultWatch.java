package org.example.config;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.concurrent.CountDownLatch;

/**
 * @author Alexander Zero
 * @version 1.0.0
 * @date 2021/4/16
 */
public class DefaultWatch implements Watcher {
    private CountDownLatch latch;

    public DefaultWatch(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void process(WatchedEvent event) {
        switch (event.getState()) {
            case Unknown:
                break;
            case Disconnected:
                break;
            case NoSyncConnected:
                break;
            case SyncConnected:
                System.out.println("client connected servers");
                latch.countDown();
                break;
            case AuthFailed:
                break;
            case ConnectedReadOnly:
                break;
            case SaslAuthenticated:
                break;
            case Expired:
                break;
            case Closed:
                break;
        }
    }
}
