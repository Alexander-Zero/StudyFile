package com.alex.helloworld;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @version 1.0.0
 * @auther Alexander Zero
 * @date 2020/12/13
 * hello world  点对点 模型
 */
public class Provider {
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("47.115.129.137");
        factory.setPort(5672);

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        //绑定队列
        channel.queueDeclare("hello",true,false,true,null);

        channel.basicPublish("","hello", MessageProperties.PERSISTENT_TEXT_PLAIN,"hello mode message".getBytes());//发布消息


    }
}
