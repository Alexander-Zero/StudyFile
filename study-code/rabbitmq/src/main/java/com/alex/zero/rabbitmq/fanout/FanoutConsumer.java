package com.alex.zero.rabbitmq.fanout;

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
public class FanoutConsumer {


    @RabbitListener(bindings = {
            @QueueBinding(value = @Queue,
                    exchange = @Exchange(value = "springFanout", type = "fanout"))
    })
    public void receive1(String message) {
        System.out.println("fanout Consumer1: " + message);
    }

    @RabbitListener(bindings = {
            @QueueBinding(value = @Queue,
                    exchange = @Exchange(value = "springFanout", type = "fanout"))
    })
    public void receive2(String message) {
        System.out.println("fanout Consumer2: " + message);
    }


    @RabbitListener(bindings = {
            @QueueBinding(value = @Queue,
                    exchange = @Exchange(value = "springFanout", type = "fanout"))
    })
    public void receive3(String message) {
        System.out.println("fanout Consumer3: " + message);
    }

}
