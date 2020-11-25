import java.util.concurrent.Semaphore;

/**
 * @version 1.0.0
 * @auther Alexander Zero
 * @date 2020/11/24
 */
public class TestSemaphore {
    public static void main(String[] args) {
Semaphore semaphore = new Semaphore(4);

for (int i = 0; i < 100; i++) {
    int finalI = i;
    new Thread(() -> {
        try {
            semaphore.acquire();
            System.out.println("现在执行的线程是:"+ finalI);
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            semaphore.release();
        }
    }).start();
}
    }
}
