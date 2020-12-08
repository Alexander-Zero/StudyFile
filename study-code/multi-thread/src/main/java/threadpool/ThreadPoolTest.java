package threadpool;

import java.util.concurrent.*;

/**
 * @version 1.0.0
 * @auther Alexander Zero
 * @date 2020/12/8
 */
public class ThreadPoolTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Executor executor = Executors.newSingleThreadExecutor();
        ExecutorService service = Executors.newCachedThreadPool();
        service.execute(new MyRunnable());
        Future<String> future = new FutureTask<>(new MyCallable());
        String s = future.get();

        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        ThreadPoolExecutor 
    }
}
