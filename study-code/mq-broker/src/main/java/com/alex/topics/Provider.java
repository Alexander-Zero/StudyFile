package com.alex.topics;

import com.alex.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;

/**
 * @version 1.0.0
 * @auther Alexander Zero
 * @date 2020/12/14
 */
public class Provider {
    public static void main(String[] args) throws IOException {
        Connection connection = RabbitMqUtils.getConn();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare("topicsExchange", "topic");

        channel.basicPublish("topicsExchange", "alex", MessageProperties.PERSISTENT_TEXT_PLAIN, "alex message".getBytes());
        channel.basicPublish("topicsExchange", "alex.zero", MessageProperties.PERSISTENT_TEXT_PLAIN, "alex.zero message".getBytes());
        channel.basicPublish("topicsExchange", "alex.zero.user", MessageProperties.PERSISTENT_TEXT_PLAIN, "alex.zero.user message".getBytes());
        channel.basicPublish("topicsExchange", "alex.zero.user.save", MessageProperties.PERSISTENT_TEXT_PLAIN, "alex.zero.user.save message".getBytes());

        RabbitMqUtils.close(channel, connection);

    }
}
