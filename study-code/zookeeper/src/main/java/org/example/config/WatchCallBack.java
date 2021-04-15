package org.example.config;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

/**
 * @version 1.0.0
 * @auther Alexander Zero
 * @date 2021/4/15
 */
public class WatchCallBack implements Watcher, AsyncCallback.DataCallback, AsyncCallback.StatCallback {
    private CountDownLatch latch = new CountDownLatch(1);
    private ZooKeeper zooKeeper;

    public ZooKeeper getZooKeeper() {
        return zooKeeper;
    }

    public void setZooKeeper(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    @Override
    //处理note修改等
    public void process(WatchedEvent event) {
        Event.KeeperState state = event.getState();

        Event.EventType type = event.getType();

        switch (type) {
            case None:
                break;
            case NodeCreated:
                System.out.println("新建节点了");
                latch.countDown();
                break;
            case NodeDeleted:
                System.out.println("删除了节点");
                break;
            case NodeDataChanged:
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

        zooKeeper.getData("/AppConf",this,this,"Context");

    }


    @Override
    //回调函数，zookeeper取到数据后如何处理
    public void processResult(int i, String s, Object o, byte[] bytes, Stat stat) {
        String data = new String(bytes);
        System.out.println(data);
    }

    @Override
    //判断存在后如何处理
    public void processResult(int i, String s, Object o, Stat stat) {
        if (stat == null) {
            System.out.println("不存在节点");
        } else {
            System.out.println("存在节点");
            //存在节点 -1 放行
            latch.countDown();
        }
    }

    public void await() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
