package com.scct.activemq.spring;

import org.apache.xbean.spring.context.ClassPathXmlApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/3/16
 */
@Service
public class SpringMQ_Consumer {
    @Autowired
    private JmsTemplate jmsTemplate;

    public static void main(String[] args) {
        ApplicationContext ctx=new ClassPathXmlApplicationContext("applicationContext.xml");

       SpringMQ_Consumer  consumer =(SpringMQ_Consumer ) ctx.getBean("springMQ_Consumer");

        String retValue = (String) consumer.jmsTemplate.receiveAndConvert();


        System.out.println("消费者收到的消息：" + retValue);
    }
}
