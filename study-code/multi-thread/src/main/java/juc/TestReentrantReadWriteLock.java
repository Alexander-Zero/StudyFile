package juc;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @version 1.0.0
 * @author Alexander Zero
 * @date 2020/11/24
 */
public class TestReentrantReadWriteLock {
    static ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    static ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    static ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
    static Random random = new Random();
    private static int value;

    public static void main(String[] args) {
        for (int i = 0; i < 18; i++) {
            new Thread(() -> read(readLock)).start();
        }
        for (int i = 0; i < 2; i++) {
            new Thread(() -> write(writeLock, random.nextInt(20))).start();
        }
    }

    public static void read(Lock lock) {
        try {
            lock.lock();
            Thread.sleep(1000);
            System.out.println("读取成功:" + value);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public static void write(Lock lock, int value) {
        try {
            lock.lock();
            Thread.sleep(1000);
            TestReentrantReadWriteLock.value = value;
            System.out.println("写入成功:" + value);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
