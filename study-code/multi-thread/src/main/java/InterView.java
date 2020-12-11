import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Exchanger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Alexander Zero
 * @version 1.0.0
 * @date 2020/12/3
 * 1A2B3C4D5
 */
public class InterView {
    public static void main(String[] args) {
        Exchanger<String> exchanger = new Exchanger<>();
        ReentrantLock lock = new ReentrantLock();
        Condition num = lock.newCondition();
        Condition abc = lock.newCondition();
        new Thread(() -> {
            for (int i = 1; i <= 26; i++) {
                System.out.println(i);
                try {
                    abc.notify();
                    num.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(() -> {
            for (int i = 0; i < 26; i++) {
                System.out.println((char) (i + 'A'));
                try {
                    lock.lock();
                    num.signal();
                    abc.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
