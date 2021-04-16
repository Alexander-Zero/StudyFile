package org.example.config;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

/**
 * @author Alexander Zero
 * @version 1.0.0
 * @date 2021/4/16
 */
public class WatchCallBack implements Watcher, AsyncCallback.StatCallback, AsyncCallback.DataCallback {
    private CountDownLatch latch = new CountDownLatch(1);
    private ZooKeeper zooKeeper;
    private MyConf myConf;

    public ZooKeeper getZooKeeper() {
        return zooKeeper;
    }

    public void setZooKeeper(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    public MyConf getMyConf() {
        return myConf;
    }

    public void setMyConf(MyConf myConf) {
        this.myConf = myConf;
    }

    @Override
    //note change or update or delete watch
    public void process(WatchedEvent watchedEvent) {
        switch (watchedEvent.getType()) {
            case NodeCreated:
                latch.countDown();
                break;
            case NodeDeleted:
                latch = new CountDownLatch(1);
                zooKeeper.exists("/AppConf", this, this, "pppxxx");
                break;
        }

        zooKeeper.getData("/AppConf", this, this, "xxxooo");
    }

    @Override
    //exist call back
    public void processResult(int i, String s, Object o, Stat stat) {
        //node exists, pass
        if (stat != null) {
            System.out.println("note exists, pass");
            latch.countDown();
            //watch 填写与不填写区别
            //猜测: false , 若先存在后delete note , watch不生效
            zooKeeper.getData("/AppConf", this, this, "xxxxoooo");
        }
    }


    @Override
    //data call back
    public void processResult(int i, String s, Object o, byte[] bytes, Stat stat) {
        String data = new String(bytes);
        myConf.setConf(data);
        myConf.setConf("data is : " + data);
        System.out.println("exec call back ");
    }

    public void await() {
        zooKeeper.exists("/AppConf", this, this, "xxoo");

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
