package threadpool;

import java.util.concurrent.Callable;

/**
 * @version 1.0.0
 * @auther Alexander Zero
 * @date 2020/12/8
 */
public class MyCallable implements Callable<String> {
    @Override
    public String call() throws Exception {
        return "hello world!";
    }
}
