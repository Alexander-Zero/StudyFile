import java.util.Random;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

/**
 * @version 1.0.0
 * @auther Alexander Zero
 * @date 2020/11/24
 */
public class TestPhaser {
    private static Random r = new Random();
    private static MarriagePhaser phaser = new MarriagePhaser();


    public static void main(String[] args) {
        phaser.bulkRegister(7);
        for (int i = 0; i < 5; i++) {
            int finalI = i;
            new Thread(new Person("P:" + i)).start();
        }
        new Thread(new Person("新郎")).start();
        new Thread(new Person("新娘")).start();

    }

    static class MarriagePhaser extends Phaser {
        @Override
        protected boolean onAdvance(int phase, int registeredParties) {
            switch (phase) {
                case 0:
                    System.out.println("-------------所有人都到齐了,婚礼开始:"+registeredParties);
                    return false;
                case 1:
                    System.out.println("-------------所有人都吃饱喝足了,等待闹洞房环节:"+registeredParties);
                    return false;
                case 2:
                    System.out.println("-------------闹洞房环节结束,婚礼结束:"+registeredParties);
                    return false;
                case 3:
                    System.out.println("-------------洞房花烛夜:"+registeredParties);
                    return false;
                case 4:
                    System.out.println("-------------自报姓名结束:"+registeredParties);
                    return true;
                default:
                    return true;
            }
        }
    }

    static class Person implements Runnable {
        String name;

        public Person(String name) {
            this.name = name;
        }

        public void arrive() {
            milliSleep(r.nextInt(1000));
            System.out.printf("%s 到达现场！\n", name);
            phaser.arriveAndAwaitAdvance();
        }

        public void eat() {
            milliSleep(r.nextInt(1000));
            System.out.printf("%s 吃完!\n", name);
            phaser.arriveAndAwaitAdvance();
        }

        public void leave() {
            milliSleep(r.nextInt(1000));
            System.out.printf("%s 离开！\n", name);
            phaser.arriveAndAwaitAdvance();

        }

        public void hug() {
            if ("新郎".equals(name) || "新娘".equals(name)) {
                System.out.println(name + "到齐");
                phaser.arriveAndAwaitAdvance();
                System.out.println(name + ":" + "婚礼结束, 洞房花烛夜");
            } else {
                phaser.arriveAndDeregister();
            }
        }

        public void sayName() {
            System.out.println("我是" + name);
            phaser.arriveAndAwaitAdvance();
        }

        @Override
        public void run() {
            arrive();

            eat();

            leave();

            hug();

            sayName();

        }
    }

    static void milliSleep(int milli) {
        try {
            TimeUnit.MILLISECONDS.sleep(milli);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
