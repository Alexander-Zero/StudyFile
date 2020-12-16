package com.alex.zero.rabbitmq.routing;

import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.security.Key;

/**
 * @author Alexander Zero
 * @version 1.0.0
 * @date 2020/12/15
 */
@Component
public class RoutingConsumer {

    @RabbitListener(bindings = {
            @QueueBinding(value = @Queue,
                    exchange = @Exchange(value = "springRouting", type = "direct"),
                    key = {"error", "warn"})
    })
    public void receiveWarnAndErrorMsg(String message) {
        System.out.println("warn and error consumer: " + message);
    }


    @RabbitListener(bindings = {
            @QueueBinding(value = @Queue,
                    exchange = @Exchange(value = "springRouting", type = "direct"),
                    key = {"info"}
            )
    })
    public void receiveErrorMsg(String message) {
        System.out.println("info consumer: " + message);
    }

}
