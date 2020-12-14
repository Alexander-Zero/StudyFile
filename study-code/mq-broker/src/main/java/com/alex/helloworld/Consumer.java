package com.alex.helloworld;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author Alexander Zero
 * @version 1.0.0
 * @date 2020/12/13
 */
public class Consumer {
    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("47.115.129.137");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();


        channel.basicQos(1);

        channel.queueDeclare("hello", true, false, false, null);
        //autoAck自动确认
        channel.basicConsume("hello", false, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("消费者:" + new String(body));
                //if autoAck is false, need to ack manually
                channel.basicAck(envelope.getDeliveryTag(),false);
            }
        });


//        close channel first, then close connection
//        if (channel != null) {
//            channel.close();
//        }
//
//        if (connection != null) {
//            connection.close();
//        }
    }
}
