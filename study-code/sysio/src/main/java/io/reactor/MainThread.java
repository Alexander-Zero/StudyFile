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
        SelectorThreadGroup stg = new SelectorThreadGroup(3);
//    SelectorThreadGroup stg = new SelectorThreadGroup(3);
        //2.将监听的server注册到selector
        stg.bind(9999);

    }


}
