package com.alex;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @version 1.0.0
 * @auther Alexander Zero
 * @date 2020/12/14
 */
public class RabbitMqUtils {
    private static final ConnectionFactory FACTORY = new ConnectionFactory();

    static {
        FACTORY.setHost("47.115.129.137");
        FACTORY.setPort(5672);
//        FACTORY.setVirtualHost();
        FACTORY.setUsername("guest");
        FACTORY.setPassword("guest");
    }

    public static Connection getConn() {
        try {
            return FACTORY.newConnection();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void close(Channel channel, Connection connection) {
        try {
            if (channel != null) {
                channel.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
