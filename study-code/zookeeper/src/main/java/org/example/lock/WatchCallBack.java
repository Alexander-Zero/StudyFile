package org.example.lock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author Alexander Zero
 * @version 1.0.0
 * @date 2021/4/16
 */
public class WatchCallBack implements Watcher, AsyncCallback.Children2Callback, AsyncCallback.StatCallback {
    private String threadName;
    private ZooKeeper zooKeeper;
    CountDownLatch latch = new CountDownLatch(1);
    private String seq;

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public ZooKeeper getZooKeeper() {
        return zooKeeper;
    }

    public void setZooKeeper(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    public void tryLock() {
        try {
            seq = zooKeeper.create("/lock", "ooxx".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            zooKeeper.getChildren("/", this, this, "xxx");
            latch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void unLock() {
        try {
            zooKeeper.delete(seq, -1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    @Override
    //children changed trigger
    public void process(WatchedEvent watchedEvent) {
        switch (watchedEvent.getType()) {
            case NodeDeleted:
                //前一个节点消失, 如何处理,有可能第一个执行完成并删除了Node, 第二的监听还没开始
                //如何处理????
                System.out.println("watch: node delete");
                zooKeeper.getChildren("/", false, this, "xxx");
                break;
        }


    }

    @Override
    //children changed
    public void processResult(int i, String s, Object o, List<String> list, Stat stat) {

        //s 是 parentPath , List是childrenPath
        Collections.sort(list);
        //争抢锁, 若为最小可以争抢到锁]
        int index = list.indexOf(seq.substring(1));
        if (index == 0) {
            latch.countDown();
        } else {
            //若没, 则盯住前面一把锁???? 序列锁???? 还没来得及注册
            String prefix = "/" + list.get(index - 1);
            System.out.println("prefix" + prefix);
            zooKeeper.exists(prefix, this, (StatCallback) this, o);
        }
    }


    @Override
    public void processResult(int i, String s, Object o, Stat stat) {
        if (stat == null) {
            zooKeeper.getChildren("/", false, this, "xxx");
        }
    }
}
