package com.scct.boot.activemq.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Queue;
import java.util.UUID;

/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/3/17
 */
@Service
public class QueueProduceService {
    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;
    @Autowired
    private Queue queue;

    public void productMessage() throws JMSException {
        // 生产者生产并发送消息，此方法是send方法的加强版
        jmsMessagingTemplate.convertAndSend(queue, "生产者发送消息的消息为：" + UUID.randomUUID());
        System.out.println("I am sending ......");
    }

//    @Scheduled(fixedDelay = 3000)
//    public void productMsgScheduled(){
//        jmsMessagingTemplate.convertAndSend(queue,"****scheduled: "+ UUID.randomUUID());
//        System.out.println("produceMsgScheduled send ok");
//    }

}

