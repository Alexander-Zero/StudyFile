package com.alex.zero.rabbitmq.workqueue;

import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author Alexander Zero
 * @version 1.0.0
 * @date 2020/12/15
 */
@Component
public class WorkConsumer {

    @RabbitListener(queuesToDeclare = @Queue(value = "springWork"))
    public void receive1(String message) {
        System.out.println("work consumer1: " + message);
    }

    @RabbitListener(queuesToDeclare = @Queue(value = "springWork"))
    public void receive2(String message) {
        System.out.println("work consumer2: " + message);
    }

}
