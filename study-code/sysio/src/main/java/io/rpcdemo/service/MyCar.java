package io.rpcdemo.service;

/**
 * @author Alexander Zero
 * @version 1.0.0
 * @date 2021/3/31
 */
public class MyCar implements Car {
    @Override
    public String ooxx(String msg) {
//        System.out.println("server get client : " + msg);
        return "server recv : " + msg;
    }
}