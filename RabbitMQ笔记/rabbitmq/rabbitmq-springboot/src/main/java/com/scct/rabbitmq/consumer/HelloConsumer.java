package com.scct.rabbitmq.consumer;

import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;



/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/3/19
 */
@Component
@RabbitListener(queuesToDeclare = @Queue("springboot-rabbitmq"))
public class HelloConsumer {

    @RabbitHandler
    public void recieve(String message){
        System.out.println("message= " + message);
    }



}
