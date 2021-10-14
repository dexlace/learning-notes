package com.scct.boot.activemq.service;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.TextMessage;

/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/3/17
 */
@Service
public class QueueConsumerService {
    /**
     * 监听接收的方法，监听的目的地名称为${queue.name}配置
     */
    @JmsListener(destination = "${queue.name}")
    public void receive(TextMessage textMessage) throws JMSException {
        String text = textMessage.getText();
        System.out.println("***消费者收到的消息:    " + text);
    }

}
