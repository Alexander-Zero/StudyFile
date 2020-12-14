package com.alex.fanout;

import com.alex.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @version 1.0.0
 * @auther Alexander Zero
 * @date 2020/12/14
 */
public class Provider {
    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        Connection connection = RabbitMqUtils.getConn();
        Channel channel = connection.createChannel();
        channel.confirmSelect();

        channel.exchangeDeclare("pc", "fanout", true, false, null);
        channel.basicPublish("pc", "", null, "fanout message".getBytes());
        channel.waitForConfirmsOrDie(1000);
        RabbitMqUtils.close(channel, connection);
    }
}
