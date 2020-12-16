package com.alex.zero.rabbitmq.topic;

import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author Alexander Zero
 * @version 1.0.0
 * @date 2020/12/15
 */
@Component
public class TopicConsumer {


    @RabbitListener(bindings = {
            @QueueBinding(value = @Queue,
                    exchange = @Exchange(value = "springTopic", type = "topic"),
                    key = {"#.zero.*"})
    })
    public void receive1(String message) {
        System.out.println("Topic Consumer1: " + message);
    }


    @RabbitListener(bindings = {
            @QueueBinding(value = @Queue,
                    exchange = @Exchange(value = "springTopic", type = "topic"),
                    key = "com.alex.*"
            )
    })
    public void receive2(String message) {
        System.out.println("Topic Consumer2: " + message);
    }

}
