package io.rpc;

import org.junit.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author Alexander Zero
 * @version 1.0.0
 * @date 2021/3/30
 * <p>
 * 1. 先驾驶一个需求,写个rpc
 * 2. 来回通信, 连接数量, 拆包?
 * 3. 动态代理, 序列化, 协议封装
 * 4. 连接池
 * 5. 就像本地方法一样去调用远程的方法, 面向java, 面向interface
 */
public class MyRPCTest {

    //模拟客户端
    @Test
    public void get() {
        Car car = proxyGet(Car.class); //动态代理
        car.ooxx("hello");

        Fly fly = proxyGet(Fly.class);
        fly.xxoo("hello");

    }

    public static <T> T proxyGet(Class<T> interfaceInfo) {
        //动态代理
        ClassLoader loader = interfaceInfo.getClassLoader();
        Class<?>[] methodInfo = {interfaceInfo};

        return (T)Proxy.newProxyInstance(loader, methodInfo, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //如何设计consumer对provider的调用过程

                //1. 调用服务, 方法,参数  ==> 封装成message

                //2. requestID +　Message, 本地需要缓存requestID

                //3. 连接池 ::　取得连接

                //4. 发送 -> 走 IO  out -> 走netty

                //5. IO in, 如何将代码往下执行, 睡眠/回调, 如何让线程停下来并继续
                // CountDownLatch

                return null;
            }
        });

    }


}

interface Car {
    public void ooxx(String msg);
}

interface Fly {
    public void xxoo(String msg);
}