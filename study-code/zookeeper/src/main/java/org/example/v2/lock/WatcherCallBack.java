package org.example.v2.lock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @version 1.0.0
 * @auther Alexander Zero
 * @date 2021/4/17
 */
public class WatcherCallBack implements Watcher, AsyncCallback.StringCallback, AsyncCallback.Children2Callback, AsyncCallback.StatCallback {
    public ZooKeeper getZooKeeper() {
        return zooKeeper;
    }

    public void setZooKeeper(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    private ZooKeeper zooKeeper;
    private String threadName;
    private CountDownLatch latch = new CountDownLatch(1);
    private String curPath;

    @Override
    public void process(WatchedEvent event) {
        switch (event.getType()) {
            case None:
                break;
            case NodeCreated:
                break;
            case NodeDeleted:
                //step 4.1.  previous is deleted, current can exec,
                //may the previous is failure not normally.
                zooKeeper.getChildren("/", false, this, "xxx");
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
    }

    public void tryLock() {
        //step 1.  create lock
        //there has two type call back , what's the difference
        //可设置重入锁
//        byte[] data = zooKeeper.getData("/", false, null);
//        if(!curPath.equals(new String(data))){
//            执行下面的操作，
//        }

        zooKeeper.create("/lock", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL, this, "xxx");

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void unLock() {
        try {
            zooKeeper.delete(curPath, -1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void processResult(int rc, String path, Object ctx, String name) {
        //step 2. create call back
        //need to get children of parent
        //there is no need to watch / 's event
        curPath = name;
        System.out.println(threadName + " create " + curPath);
        zooKeeper.getChildren("/", false, this, "getChildren");
    }


    @Override
    public void processResult(int rc, String path, Object ctx, List<String> children, Stat stat) {
        //step 3 : watch current node is the first
        Collections.sort(children);

        int i = children.indexOf(curPath.substring(1));

        //it's the first
        if (i == 0) {
            //重入锁
            //这里可以把持有锁的编号放入parent, 重入时直接判断
            //zooKeeper.setData("/",curPath.getBytes(),-1,this,"xx");
            latch.countDown();
        }
        // not the first
        else {
            String s = "/" + children.get(i - 1);
            zooKeeper.exists("/" + children.get(i - 1), this, this, "xxx");
        }


    }

    @Override
    public void processResult(int rc, String path, Object ctx, Stat stat) {
        //step 4.2
        if (stat == null) {
            //the previous is
            zooKeeper.getChildren("/", false, this, "xxx");
        }
    }
}
