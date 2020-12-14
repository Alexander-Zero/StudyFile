package com.alex.topics;

import com.alex.RabbitMqUtils;
import com.rabbitmq.client.*;
import sun.plugin2.message.LaunchJVMAppletMessage;

import java.io.IOException;

/**
 * @version 1.0.0
 * @auther Alexander Zero
 * @date 2020/12/14
 */
public class Consumer2 {
    public static void main(String[] args) throws IOException {
        Connection connection = RabbitMqUtils.getConn();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare("topicsExchange", "topic");
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, "topicsExchange", "alex.#");

        channel.basicConsume(queueName, false, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("alex.# consumer: " + new String(body));
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        });

    }
}
