package io.rpcdemo.rpc;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Alexander Zero
 * @version 1.0.0
 * @date 2021/3/31
 */
public class ResponseMappingCallback {
    static ConcurrentHashMap<Long, CompletableFuture> mapping = new ConcurrentHashMap<>();

    public static void addCallBack(long requestId, CompletableFuture cb) {
        mapping.put(requestId, cb);
    }

    public static void runCallBack(PackMsg packMsg) {
        CompletableFuture future = mapping.get(packMsg.getHeader().getRequestId());
        future.complete(packMsg.getContent().getRes());
        removeCallBack(packMsg.getHeader().getRequestId());
    }

    private static void removeCallBack(long requestId) {
        mapping.remove(requestId);
    }
}

