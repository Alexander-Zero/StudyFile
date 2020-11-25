import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

/**
 * @version 1.0.0
 * @auther Alexander Zero
 * @date 2020/11/24
 */
public class TestLongAdder {
    public static void main(String[] args) {
CountDownLatch latch = new CountDownLatch(10000);

LongAdder adder = new LongAdder();
for (int i = 0; i < 10000; i++) {
    new Thread(() -> {
        for (int j = 0; j < 100; j++) {
            adder.increment();
        }
        latch.countDown();
    }).start();
}
try {
    latch.await();
} catch (InterruptedException e) {
    e.printStackTrace();
}

long value = adder.longValue();
System.out.println(value);
    }
}
