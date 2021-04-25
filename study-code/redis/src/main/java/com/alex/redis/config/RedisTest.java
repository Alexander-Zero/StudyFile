package com.alex.redis.config;

import io.rebloom.client.Client;
import io.rebloom.client.ClusterClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import redis.clients.jedis.HostAndPort;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Alexander Zero
 * @version 1.0.0
 * @date 2021/4/19
 */
@Component
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;


    public void test() {
        RedisConnection conn = redisTemplate.getConnectionFactory().getConnection();
        Client client = new Client("localhost", 6379);
        client.add("aaaxxx", "xxx");
        client.cfAdd("xxx", "xx");
        client.cfDel("xxx", "xxx");
        boolean exists = client.exists("xxoo", "xxx");
        client.addMulti("key", "val1", "val2", "val3", "val4");

        //集群
        Set<HostAndPort> redisClient = new HashSet<>();
        redisClient.add(new HostAndPort("localhost", 6379));
        ClusterClient cclient = new ClusterClient(redisClient);

//        cclient.getbit()

        cclient.addMulti("key2", "xxx","xxoo","ooxx","oxox","oxxo");
    }

}
