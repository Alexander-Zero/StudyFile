package juc;

/**
 * @version 1.0.0
 * @auther Alexander Zero
 * @date 2020/11/20
 * 线程概念
 */
public class HowToCreateThread {
    public static void main(String[] args) throws InterruptedException {

        Thread t1 = new Thread(() -> {
            System.out.println("T1睡之前");
            for (int i = 0; i < 100; i++) {
                System.out.println("xx");
            }
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            System.out.println("T1睡之后;结束");
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                System.out.println("线程T2中的循环次数: " + i);
//                if (i == 2) {
//                    try {
//                        t1.wait();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }

                if (i == 6) {
                    t1.notify();
                    try {
                        t1.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("这是线程T2");
        });


        t1.start();
        t2.start();
        t1.wait();



    }
}
