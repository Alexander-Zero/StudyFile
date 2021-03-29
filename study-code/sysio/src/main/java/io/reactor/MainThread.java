package io.reactor;

/**
 * @author Alexander Zero
 * @version 1.0.0
 * @date 2021/3/29
 * 不做业务相关和IO相关的操作
 */
public class MainThread {

    public static void main(String[] args) {
        //1. 创建IOThread (一个或多个)
        SelectorThreadGroup bossGroup = new SelectorThreadGroup(2);

        SelectorThreadGroup workerGroup = new SelectorThreadGroup(3);

        bossGroup.setWorker(workerGroup);

//    SelectorThreadGroup stg = new SelectorThreadGroup(3);
        //2.将监听的server注册到selector
        bossGroup.bind(9999);
        bossGroup.bind(8888);
        bossGroup.bind(7777);
    }


}
