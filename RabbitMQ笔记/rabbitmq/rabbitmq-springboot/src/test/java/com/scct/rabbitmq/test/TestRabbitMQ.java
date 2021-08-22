package com.scct.rabbitmq.test;

import com.scct.rabbitmq.RabbitmqSpringbootApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/3/19
 */
@SpringBootTest(classes = RabbitmqSpringbootApplication.class)
@RunWith(SpringRunner.class)
public class TestRabbitMQ {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testHello(){

        rabbitTemplate.convertAndSend("springboot-rabbitmq","hello-springboot-rabbitmq");

    }

    //work
    @Test
    public void testWork(){
        for (int i = 0; i < 10; i++) {
            rabbitTemplate.convertAndSend("work","work模型"+i);
        }
    }


    // fanout广播
    @Test
    public void testFanout() throws InterruptedException {
        rabbitTemplate.convertAndSend("logs","","这是日志广播");
    }

    // 直接路由模式
    @Test
    public void testDirectRoute() {
        rabbitTemplate.convertAndSend("directRoute", "error", "error 的日志信息");
    }


    //topic
    @Test
    public void testTopic(){
        rabbitTemplate.convertAndSend("topics","user.save.findAll","user.save.findAll 的消息");
    }
}
