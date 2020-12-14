package com.alex.workqueue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author Alexander Zero
 * @version 1.0.0
 * @date 2020/12/14
 */
public class Provider {
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("47.115.129.137");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare("work", true, false, false, null);
        for (int i = 0; i < 30; i++) {
            channel.basicPublish("", "work", MessageProperties.PERSISTENT_TEXT_PLAIN, (i + " --> work Message").getBytes());
        }

        if (channel != null) {
            channel.close();
        }
        if (connection != null) {
            connection.close();
        }

    }

}
