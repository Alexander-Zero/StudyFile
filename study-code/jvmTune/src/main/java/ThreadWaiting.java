import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @version 1.0.0
 * @auther Alexander Zero
 * @date 2021/1/9
 */
public class ThreadWaiting implements Runnable {

    private static Object mutex = new Object();

    @Override
    public void run() {
        synchronized (mutex) {
            for (; ; ) {

            }
        }
    }

    public static void main(String[] args) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(50, 50, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(),Executors.defaultThreadFactory(),new ThreadPoolExecutor.DiscardOldestPolicy());

        for (int i = 0; i < 5; i++) {
            new Thread(new ThreadWaiting(), "t" + i).start();
        }
    }
}
