package io.rpcdemo.rpc.transport;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.rpcdemo.rpc.ResponseMappingCallback;
import io.rpcdemo.util.PackMsg;

/**
 * @author Alexander Zero
 * @version 1.0.0
 * @date 2021/3/31
 */
public class ClientResponse extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        PackMsg respPack = (PackMsg) msg;
//        System.out.println(respPack.getContent().getRes());
        ResponseMappingCallback.runCallBack(respPack);

/*     version 1.0
        ByteBuf buf = (ByteBuf) msg;
        int headerSize = 88;
       System.out.println("client handler message");
        if (buf.readableBytes() > headerSize) {
            byte[] bytes = new byte[headerSize];
            buf.getBytes(0, bytes);

            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            ObjectInputStream oin = new ObjectInputStream(in);
            MyHeader header = (MyHeader) oin.readObject();
            System.out.println("requestId is " + header.getRequestId());
            ResponseHandler.runCallBack(header.getRequestId());

            if (buf.readableBytes() >= header.getDataLength() + headerSize) {
                buf.readBytes(bytes);

                byte[] data = new byte[(int) header.getDataLength()];
                buf.readBytes(data);

                ByteArrayInputStream din = new ByteArrayInputStream(data);
                ObjectInputStream doin = new ObjectInputStream(din);
                MyContent content = (MyContent) doin.readObject();
            }
        }*/


    }
}