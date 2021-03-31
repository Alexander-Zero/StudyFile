package io.rpcdemo.rpc.protocol;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author Alexander Zero
 * @version 1.0.0
 * @date 2021/3/31
 */
public class MyHeader implements Serializable {
    private int flag;//协议,具体的协议
    private long requestId;
    private long dataLength;


    public static MyHeader createHeader(byte[] msgBody) {
        MyHeader header = new MyHeader();
        int dataLength = msgBody.length;
        int flag = 0x14141414;//具体的协议
        long requestId = UUID.randomUUID().getLeastSignificantBits();

        header.setFlag(flag);
        header.setRequestId(requestId);
        header.setDataLength(dataLength);
        return header;
    }


    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public long getDataLength() {
        return dataLength;
    }

    public void setDataLength(long dataLength) {
        this.dataLength = dataLength;
    }
}
