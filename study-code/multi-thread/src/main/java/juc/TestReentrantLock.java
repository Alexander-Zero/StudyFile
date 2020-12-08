package juc;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @version 1.0.0
 * @auther Alexander Zero
 * @date 2020/11/20
 */
public class TestReentrantLock {
    ReentrantLock lock = new ReentrantLock(true);

    public void m1() {

        boolean locked = lock.tryLock();
        try {
            System.out.println("tryLock");
            System.out.println(locked);//锁定与否都要执行
        } finally {
            if (locked) {
                lock.unlock();
            }
        }
    }

    public void m3() {
        boolean locked = false;
        try {
            locked = lock.tryLock(2, TimeUnit.SECONDS);
            System.out.println("tryLock--2s");
            System.out.println(locked);
            System.out.println("锁定与否都执行");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (locked) {
                lock.unlock();
            }
        }
    }

    public void m2() {
        try {
            lock.lock();
            System.out.println("lock获取锁");
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        System.out.println("Lock锁");
    }

    public static void main(String[] args) {
        TestReentrantLock t = new TestReentrantLock();
        new Thread(t::m2).start();
//        new Thread(t::m1).start();
        new Thread(t::m3).start();

    }
}
