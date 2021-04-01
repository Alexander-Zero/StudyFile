package io.rpcdemo.rpc.transport;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.rpcdemo.rpc.Dispatcher;
import io.rpcdemo.rpc.protocol.MyContent;
import io.rpcdemo.rpc.protocol.MyHeader;
import io.rpcdemo.util.PackMsg;
import io.rpcdemo.util.SerDerUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Alexander Zero
 * @version 1.0.0
 * @date 2021/3/31
 */
public class ServerResponseHandler extends ChannelInboundHandlerAdapter {
    private Dispatcher dispatcher;

    public ServerResponseHandler(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    /* version 1
       ByteBuf buf = (ByteBuf) msg;

        int headerLength = 88;
        System.out.println("server handler message ~~~~~~");
        if (buf.readableBytes() > headerLength) {
            byte[] headerBytes = new byte[headerLength];
            buf.getBytes(0, headerBytes);

            ByteArrayInputStream in = new ByteArrayInputStream(headerBytes);
            ObjectInputStream oin = new ObjectInputStream(in);
            MyHeader header = (MyHeader) oin.readObject();
//            System.out.println(header.getDataLength());
//            System.out.println(header.getRequestId());


            if (buf.readableBytes() >= header.getDataLength() + headerLength) {
                buf.readBytes(headerBytes);

                byte[] data = new byte[(int) header.getDataLength()];
                buf.readBytes(data);

                ByteArrayInputStream din = new ByteArrayInputStream(data);
                ObjectInputStream doin = new ObjectInputStream(din);
                MyContent content = (MyContent) doin.readObject();

                ByteBuf sendBuf = PooledByteBufAllocator.DEFAULT.buffer(data.length + headerLength);
                sendBuf.writeBytes(headerBytes);
                sendBuf.writeBytes(data);
                ctx.writeAndFlush(sendBuf);
                System.out.println(content);
            }
        }*/


        PackMsg packMsg = (PackMsg) msg;

        System.out.println("server handler : message is " + packMsg.getContent().getArgs()[0]);


        //返回消息,
        // 1. 在当前线程执行IO,返回等操作
        // 2. eventloop的线程来执行
        String ioThreadName = Thread.currentThread().getName();

//        ctx.executor().execute(() -> {
        ctx.executor().parent().next().execute(() -> {

            //获取对象
            String serviceName = packMsg.getContent().getName();
            String methodName = packMsg.getContent().getMethod();
            Object[] args = packMsg.getContent().getArgs();
            Class<?>[] parameterTypes = packMsg.getContent().getParameterTypes();

            Object impl = dispatcher.get(serviceName);
            Class<?> clazz = impl.getClass();

            Object res = null;
            try {
                Method method = clazz.getMethod(methodName, parameterTypes);
                res = method.invoke(impl, args);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }


//            String executeThreadName = Thread.currentThread().getName();
            MyContent respContent = new MyContent();
//            String resp = "io thread: " + ioThreadName + ",　exec thread :　" + executeThreadName + ", from args: " + packMsg.getContent().getArgs()[0];
            respContent.setRes(res);

            byte[] body = SerDerUtil.ser(respContent);

            MyHeader respHeader = new MyHeader();
            respHeader.setRequestId(packMsg.getHeader().getRequestId());
            respHeader.setFlag(0x24242424);
            respHeader.setDataLength(body.length);

            byte[] header = SerDerUtil.ser(respHeader);


            ByteBuf buf = PooledByteBufAllocator.DEFAULT.buffer(header.length + body.length);

            buf.writeBytes(header);
            buf.writeBytes(body);

            ctx.writeAndFlush(buf);

        });


    }
}