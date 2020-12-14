package juc;

import java.util.WeakHashMap;
import java.util.concurrent.Exchanger;

/**
 * @version 1.0.0
 * @auther Alexander Zero
 * @date 2020/11/24
 */
public class TestExchanger {
    public static void main(String[] args) {
        Exchanger<String> exchanger = new Exchanger<>();
        for (int i = 0; i < 20; i++) {
            int finalI = i + 1;
            new Thread(() -> {
                try {
                    String value = "T" + finalI;
                    System.out.println("t" + finalI + "交换前:" + value);
                    String exchange = exchanger.exchange(value);
                    System.out.println("t" + finalI + "交换后:" + exchange);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

        }

    }
}
