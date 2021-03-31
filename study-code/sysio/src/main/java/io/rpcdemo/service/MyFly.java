package io.rpcdemo.service;

/**
 * @author Alexander Zero
 * @version 1.0.0
 * @date 2021/3/31
 */
public class MyFly implements Fly {
    @Override
    public void xxoo(String msg) {
        System.out.println("fly server received message :　" + msg);
    }
}
