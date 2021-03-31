package io.rpcdemo.rpc.transport;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.rpcdemo.rpc.protocol.MyContent;
import io.rpcdemo.rpc.protocol.MyHeader;
import io.rpcdemo.util.PackMsg;
import io.rpcdemo.util.SerDerUtil;

import java.util.List;

/**
 * @author Alexander Zero
 * @version 1.0.0
 * @date 2021/3/31
 */
public class ServerDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf buf, List<Object> list) throws Exception {
        int headerSize = 105;
        while (buf.readableBytes() > headerSize) {
            byte[] bytes = new byte[headerSize];
            buf.getBytes(buf.readerIndex(), bytes);
            MyHeader header = SerDerUtil.der(bytes, MyHeader.class);

//            if (header.getFlag() == 0x14141414) {
//                System.out.println("request ");
//            } else if (header.getFlag() == 0x24242424) {
//                System.out.println("response");
//            }

            if (buf.readableBytes() >= header.getDataLength() + headerSize) {
                byte[] data = new byte[(int) header.getDataLength()];
                buf.readBytes(bytes);
                buf.readBytes(data);

                MyContent content = SerDerUtil.der(data, MyContent.class);
                list.add(new PackMsg(header, content));
            } else {
                break;
            }
        }
    }
}