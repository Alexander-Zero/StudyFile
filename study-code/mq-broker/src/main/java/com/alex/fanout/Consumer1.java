package com.alex.fanout;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @version 1.0.0
 * @auther Alexander Zero
 * @date 2020/12/14
 */
public class Consumer1 {
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("47.115.129.137");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare("pc", "fanout", true, false, null);

        String queueName = channel.queueDeclare().getQueue();

        //需要绑定交换机
        channel.queueBind(queueName, "pc", "");

        channel.basicConsume(queueName, false, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("fanout consumer1: " + new String(body));
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        });


    }
}
