package juc;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;

/**
 * @version 1.0.0
 * @auther Alexander Zero
 * @date 2020/11/24
 */
public class TestLockSupport {
    public static void main(String[] args) {

        Thread t1 = new Thread(() -> {
            System.out.println("T1线程开始前");
            LockSupport.park();
            System.out.println("T1线程开始后");
        });

        Thread t2 = new Thread(() -> {
            System.out.println("T2线程开始前");
            LockSupport.park();
            System.out.println("T2线程kaikai");
        });

        t1.start();
        t2.start();

        LockSupport.unpark(t2);
        LockSupport.unpark(t1);

    }
}
