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
//        factory.setVirtualHost("store");
        factory.setUsername("guest");
        factory.setPassword("guest");

        //创建连接
        Connection connection = factory.newConnection();

        //创建通道
        Channel channel = connection.createChannel();
        //绑定队列
        //参数1:队列名 参数2:持久化,存磁盘  参数3:独占,一般false, 参数4:消息消费完了,删除队列
        channel.queueDeclare("hello", true, false, false, null);
        //发布消息
        for (int i = 0; i < 20; i++) {
            channel.basicPublish("", "hello", MessageProperties.PERSISTENT_TEXT_PLAIN, (i+": hello world").getBytes());
        }


        if (channel != null) {
            channel.close();
        }

        if (connection != null) {
            connection.close();
        }


    }
}
