package threadpool;

import java.util.Random;
import java.util.Stack;
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
//        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor();
//        Stack
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        MyTask myTask = new MyTask(0, 10000);

        forkJoinPool.execute(myTask);
        Long sum = myTask.join();
        System.out.println(sum);//汇总值
    }

    static int[] nums = new int[100000];
    static Random r = new Random();
    static {
        for (int i = 0; i < 100000; i++) {
            nums[i] = r.nextInt(100);
        }
    }
    static class MyTask extends RecursiveTask<Long> {
        int start;
        int end;

        public MyTask(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        protected Long compute() {
            if (this.end - this.start <= 100) {
                long sum = 0L;
                for (int i = start; i < end; i++) sum += nums[i];
                return sum;
            } else {
                int middle = start + (end - start) / 2;

                MyTask subTask1 = new MyTask(start, middle);
                MyTask subTask2 = new MyTask(middle, end);

                return subTask1.join() + subTask2.join();
            }
        }
    }
}
