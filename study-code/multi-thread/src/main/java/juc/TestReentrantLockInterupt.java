package juc;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @version 1.0.0
 * @auther Alexander Zero
 * @date 2020/11/20
 */
public class TestReentrantLockInterupt {
    public static void main(String[] args) {
        ReentrantLock lock = new ReentrantLock();

        Thread t1 = new Thread(() -> {
            try {
                lock.lockInterruptibly();
                System.out.println("T1睡之前");
                System.out.println(lock.isHeldByCurrentThread());
                TimeUnit.SECONDS.sleep(1);
                System.out.println("T1睡之后");
            } catch (InterruptedException e) {
                System.out.println("T1被打断");
//                e.printStackTrace();
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        });


        Thread t2 = new Thread(() -> {
            try {
                lock.lock();
                System.out.println("T2睡之前");
                TimeUnit.SECONDS.sleep(5);
                System.out.println("T2睡之后");
            } catch (InterruptedException e) {
                System.out.println("T2被打断");
//                e.printStackTrace();
            } finally {
                boolean heldByCurrentThread = lock.isHeldByCurrentThread();
                if (heldByCurrentThread) {
                    lock.unlock();
                }
            }
        });

        t2.start();
        t1.start();

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        t1.interrupt(); //打断线程2的等待
    }
}
