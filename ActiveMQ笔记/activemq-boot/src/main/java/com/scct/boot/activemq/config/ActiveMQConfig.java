package com.scct.boot.activemq.config;


import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jms.Queue;
import javax.jms.Topic;


/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/3/17
 */
@Configuration
public class ActiveMQConfig {

    @Value("${queue.name}")
    private String queueName;
    @Value("${topic.name}")
    private String topicName;

    @Bean
    public Queue activeQueue(){
        return  new ActiveMQQueue(queueName);
    }


    @Bean
    public Topic activeTopic(){
        return  new ActiveMQTopic(queueName);
    }

}
