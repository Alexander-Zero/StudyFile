package io.rpcdemo.proxy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.rpcdemo.rpc.Dispatcher;
import io.rpcdemo.rpc.protocol.MyContent;
import io.rpcdemo.rpc.protocol.MyHeader;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

/**
 * @author Alexander Zero
 * @version 1.0.0
 * @date 2021/3/31
 */
public class MyProxy {


    public static <T> T proxyGet(Class<T> interfaceInfo) {
        //动态代理
        ClassLoader loader = interfaceInfo.getClassLoader();
        Class<?>[] methodInfo = {interfaceInfo};


        //TODO local/remote 调用实现: Dispatcher
        Dispatcher dispatcher = Dispatcher.getInstance();

        return (T) Proxy.newProxyInstance(loader, methodInfo, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //如何设计consumer对provider的调用过程
                //可能是本机,可能是远程

                //TODO 判断是本地还是远程的
                Object o = dispatcher.get(interfaceInfo.getName());
                //rpc调用
                Object result = null;

                if (o == null) {

                    //1. 调用服务, 方法,参数  ==> 封装成message
                    String name = interfaceInfo.getName();
                    String methodName = method.getName();
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    MyContent body = new MyContent();

                    body.setName(name);
                    body.setMethod(methodName);
                    body.setParameterTypes(parameterTypes);
                    body.setArgs(args);

                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    ObjectOutputStream oout = new ObjectOutputStream(out);
                    oout.writeObject(body);
                    byte[] msgBody = out.toByteArray();


                    //2. requestID +　Message, 本地需要缓存requestID
                    //协议
                    MyHeader header = createHeader(msgBody);

                    out.reset();
                    oout = new ObjectOutputStream(out);
                    oout.writeObject(header);

                    byte[] headerMsg = out.toByteArray();

//                System.out.println("header size is " + headerMsg.length);

                    //3. 连接池 ::　取得连接
                    ClientFactory factory = ClientFactory.getInstance();
                    NioSocketChannel channel = factory.getClient(new InetSocketAddress("localhost", 9090));

                    //4. 发送 -> 走 IO  out -> 走netty

//                CountDownLatch latch = new CountDownLatch(1);
                    long requestId = header.getRequestId();

                    //设置返回值
                    CompletableFuture<Object> res = new CompletableFuture<>();
                    ResponseMappingCallback.addCallBack(requestId, res);

                    ByteBuf buf = PooledByteBufAllocator.DEFAULT.buffer(headerMsg.length + headerMsg.length);
                    buf.writeBytes(headerMsg);
                    buf.writeBytes(msgBody);
                    ChannelFuture channelFuture = channel.writeAndFlush(buf);
                    channelFuture.sync();

                    channelFuture.channel().closeFuture().channel();

//                latch.await();

                    //5. IO in, 如何将代码往下执行, 睡眠/回调, 如何让线程停下来并继续
                    // CountDownLatch

                    result = res.get();
                }
                //local调用, 走代理可扩展
                else {


                    Class<?> clazz = o.getClass();
                    Method m = clazz.getMethod(method.getName(), method.getParameterTypes());
                    try {
                        result = m.invoke(o, args);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }


                }
return result;
            }


        });

    }

}
