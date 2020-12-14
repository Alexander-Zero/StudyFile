package threadpool;

/**
 * @version 1.0.0
 * @auther Alexander Zero
 * @date 2020/12/8
 */
public class MyRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println("this thread is running");
    }
}
