package com.alex.routing;

import com.alex.RabbitMqUtils;
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
public class Provider {
    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = RabbitMqUtils.getConn();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare("routeExchange", "direct", true, true, null);

        channel.basicPublish("routeExchange", "error", null, "error message".getBytes());
        channel.basicPublish("routeExchange", "info", null, "info message".getBytes());
        channel.basicPublish("routeExchange", "info", null, "info message".getBytes());
        channel.basicPublish("routeExchange", "warn", null, "warn message".getBytes());

        RabbitMqUtils.close(channel, connection);

    }
}
