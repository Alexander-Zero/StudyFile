package com.alex.routing;

import com.alex.RabbitMqUtils;
import com.alex.helloworld.Consumer;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeoutException;

/**
 * @version 1.0.0
 * @auther Alexander Zero
 * @date 2020/12/14
 */
public class Consumer1 {
    public static void main(String[] args) throws IOException, TimeoutException {

        Connection connection = RabbitMqUtils.getConn();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare("routeExchange", "direct", true, true, null);
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, "routeExchange", "info", null);

        channel.basicConsume(queueName, false, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("info worker: " + new String(body));
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        });


    }
}
