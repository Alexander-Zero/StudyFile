package org.example.v2.config;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

/**
 * @version 1.0.0
 * @auther Alexander Zero
 * @date 2021/4/17
 */
public class WatcherCallBack implements Watcher, AsyncCallback.StatCallback, AsyncCallback.DataCallback {
    private ZooKeeper zooKeeper;
    private MyConfig myConfig;
    private CountDownLatch latch = new CountDownLatch(1);

    public ZooKeeper getZooKeeper() {
        return zooKeeper;
    }

    public void setZooKeeper(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    public MyConfig getMyConfig() {
        return myConfig;
    }

    public void setMyConfig(MyConfig myConfig) {
        this.myConfig = myConfig;
    }

    @Override
    public void process(WatchedEvent event) {
        switch (event.getType()) {
            case None:
                break;
            case NodeCreated:
                //step 1.1
                //其他线程创建后， 也是需要获取数据， 这个watch应该时 exist()方法注册进来的
                zooKeeper.getData("/AppConfig", this, this, "xxx");
                break;
            case NodeDeleted:
                latch = new CountDownLatch(1);
                myConfig.setConfig("");
                break;
            case NodeDataChanged:
                //数据改变后也要获取数据， 此时latch计数为0,再次countDown无影响, getData()注册进来的
                zooKeeper.getData("/AppConfig", this, this, "xxx");
                break;
            case NodeChildrenChanged:
                break;
            case DataWatchRemoved:
                break;
            case ChildWatchRemoved:
                break;
            case PersistentWatchRemoved:
                break;
        }

    }

    public void await() {
        try {
            //step 1
            zooKeeper.exists("/AppConfig", this, this, "xxx");
            latch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //exist call back
    @Override
    public void processResult(int rc, String path, Object ctx, Stat stat) {
        //若存在就获取数据
        zooKeeper.getData("/AppConfig", this, this, "xxx");
    }

    //get data call back
    @Override
    public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
        //获取数据后，程序可以往下执行
        String config = new String(data);
        myConfig.setConfig(config);

        latch.countDown();
    }
}
