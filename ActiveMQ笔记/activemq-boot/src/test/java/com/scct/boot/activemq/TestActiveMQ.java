package com.scct.boot.activemq;

import com.scct.boot.activemq.service.QueueProduceService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.jms.JMSException;

/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/3/17
 */
@SpringBootTest(classes = BootActiveMQMain.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class TestActiveMQ {
    @Autowired
    private QueueProduceService queueProduceService;

    @Test
    public void testSend() throws JMSException {
        for (int i = 0; i < 10; i++) {
            queueProduceService.productMessage();
        }
    }
}
