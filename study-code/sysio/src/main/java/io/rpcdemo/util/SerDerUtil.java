package io.rpcdemo.util;

import java.io.*;

/**
 * @author Alexander Zero
 * @version 1.0.0
 * @date 2021/3/31
 */
public class SerDerUtil {
    static ByteArrayOutputStream out = new ByteArrayOutputStream();
    static ByteArrayInputStream in;

    public synchronized static byte[] ser(Object obj) {
        out.reset();
        ObjectOutputStream oout = null;
        try {
            oout = new ObjectOutputStream(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            oout.writeObject(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] msgBody = out.toByteArray();
        return msgBody;
    }

    public synchronized static <T> T der(byte[] bytes, Class<T> calzz) {
        in = new ByteArrayInputStream(bytes);
        ObjectInputStream oin = null;
        try {
            oin = new ObjectInputStream(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return (T) oin.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
