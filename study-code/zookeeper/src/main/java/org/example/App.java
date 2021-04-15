package org.example;

import com.sun.xml.internal.messaging.saaj.soap.Envelope;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.proto.WatcherEvent;
import org.apache.zookeeper.server.ZooKeeperServer;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        System.out.println("Hello World!");

        final CountDownLatch latch = new CountDownLatch(1);
        String servers = "192.168.1.109:2181,192.168.1.110:2181,192.168.1.111:2181,192.168.1.113:2181";
//        String servers = "192.168.146.128:2181,192.168.146.129:2181,192.168.146.130:2181,192.168.146.132:2181";
        final ZooKeeper zooKeeper = new ZooKeeper(servers, 3000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                String path = event.getPath();
                Event.KeeperState state = event.getState();
                Event.EventType type = event.getType();
                WatcherEvent wrapper = event.getWrapper();
                System.out.println("new watcher: " + event);

                switch (state) {
                    case Unknown:
                        break;
                    case Disconnected:
                        break;
                    case NoSyncConnected:
                        break;
                    case SyncConnected:
                        System.out.println("connected");
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

                switch (type) {
                    case None:
                        break;
                    case NodeCreated:
                        break;
                    case NodeDeleted:
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
        });


        latch.await();
        ZooKeeper.States state = zooKeeper.getState();

        switch (state) {
            case CONNECTING:
                System.out.println("ing -----");
                break;
            case ASSOCIATING:
                break;
            case CONNECTED:
                System.out.println("ed ---------");
                break;
            case CONNECTEDREADONLY:
                break;
            case CLOSED:
                break;
            case AUTH_FAILED:
                break;
            case NOT_CONNECTED:
                break;
        }

        //不用latch.await 可能存在未连接就开始创建了
        String resultPath = zooKeeper.create("/alex", "hello world".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

        final Stat stat = new Stat();

        //getWatch 中的watcher 只执行一次, 若需要监测每一次, 需要在process中在注册进去,
        //若参数true, 注册的时默认watcher, 即create注册的那个watcher
        byte[] data = zooKeeper.getData("/alex", new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("get Watch :　" + event.toString());
                try {
                    byte[] data1 = zooKeeper.getData("/alex", true, stat);
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, stat);

        System.out.println(new String(data));

        System.out.println("stat : " + stat);

        Stat stat1 = zooKeeper.setData("/alex", "moxomox".getBytes(), 0);

        System.out.println("stat1 :　" + stat1);
        System.out.println(stat1);

        Stat stat2 = zooKeeper.setData("/alex", "moxomox".getBytes(), stat1.getVersion());

        Thread.sleep(4000);
        System.out.println("closed");
    }
}
