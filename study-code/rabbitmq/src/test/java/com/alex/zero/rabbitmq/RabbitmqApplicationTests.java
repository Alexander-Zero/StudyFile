package com.alex.zero.rabbitmq;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RabbitmqApplicationTests {

    @Autowired
    private RabbitTemplate rabbitTemplate;


//	@Test
//	void contextLoads() {
//	}


    @Test
    public void testHelloWorld() {
        rabbitTemplate.convertAndSend("springHello", "Spring Hello Message");
    }

    @Test
    public void testWorkQueue() {
        for (int i = 0; i < 20; i++) {
            rabbitTemplate.convertAndSend("springWork", i + ": Spring Work Queue Message");
        }
    }

    @Test
    public void testFanout() {
        rabbitTemplate.convertAndSend("springFanout", "", "Spring Fanout Message");
    }

    @Test
    public void testRouting() {
        rabbitTemplate.convertAndSend("springRouting", "warn", "Warn message by routing exchange");
        rabbitTemplate.convertAndSend("springRouting", "error", "Error message by routing exchange");
        rabbitTemplate.convertAndSend("springRouting", "info", "Info message by routing exchange");
    }

    @Test
    public void testTopic() {
        rabbitTemplate.convertAndSend("springTopic", "com.alex.zero.save.test", "com.alex.zero.save.test");
        rabbitTemplate.convertAndSend("springTopic", "com.alex.zero.save", "com.alex.zero.save");
        rabbitTemplate.convertAndSend("springTopic", "com.alex.zero", "com.alex.zero");
        rabbitTemplate.convertAndSend("springTopic", "com.alex", "com.alex");
        rabbitTemplate.convertAndSend("springTopic", "com", "com");
    }

}
