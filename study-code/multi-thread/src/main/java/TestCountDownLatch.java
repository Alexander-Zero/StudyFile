import java.util.concurrent.CountDownLatch;

/**
 * @version 1.0.0
 * @auther Alexander Zero
 * @date 2020/11/23
 */
public class TestCountDownLatch {
    public static void main(String[] args) {
        CountDownLatch latch = new CountDownLatch(5);
        for (int i = 0; i <20; i++) {
            int finalI = i;
            new Thread(() -> {
                System.out.println("ssss:" + finalI);
                latch.countDown();
            }).start();
        }
        try {
            latch.await();
            System.out.println("门闩已打开");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
