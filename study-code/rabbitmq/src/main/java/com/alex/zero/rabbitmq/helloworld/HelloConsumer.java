package com.alex.zero.rabbitmq.helloworld;

import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author Alexander Zero
 * @version 1.0.0
 * @date 2020/12/15
 * 点对点模式
 */

@Component
@RabbitListener(queuesToDeclare = @Queue(value = "springHello"))
public class HelloConsumer {

    @RabbitHandler
    public void receive1(String message) {
        System.out.println("Hello Spring Consumer: " + message);
    }


}
