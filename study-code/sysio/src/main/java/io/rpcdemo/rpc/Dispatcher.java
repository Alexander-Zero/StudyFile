package io.rpcdemo.rpc;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Alexander Zero
 * @version 1.0.0
 * @date 2021/3/31
 */
public class Dispatcher {
    public static final Dispatcher INSTANCE = new Dispatcher();

    private Dispatcher() {
    }

    public static Dispatcher getInstance() {
        return INSTANCE;
    }


    public static ConcurrentHashMap<String, Object> invokeMap = new ConcurrentHashMap<>();

    public void register(String k, Object v) {
        invokeMap.put(k, v);
    }

    public Object get(String interfaceName) {
        return invokeMap.get(interfaceName);
    }


}
