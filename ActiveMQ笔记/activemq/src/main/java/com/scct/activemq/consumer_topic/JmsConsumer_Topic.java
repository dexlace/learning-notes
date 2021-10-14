package com.scct.activemq.consumer_topic;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.io.IOException;

/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/3/13
 */
public class JmsConsumer_Topic {

    public static final String ACTIVEMQ_URL = "tcp://192.168.205.103:61616";
    public static final String TOPIC_NAME = "topic01";

    public static void main(String[] args) throws JMSException, IOException {
        System.out.println("我是2号消费者");
        // 1 创建连接工厂,按照给定的url地址，采用默认的用户名和密码
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        // 2. 通过连接工厂，获取连接connection,并启动访问
        Connection connection = activeMQConnectionFactory.createConnection();
        connection.start();
        // 3.创建会话session
        // 第一个 参数事务
        // 第二个  签收，这里选择自动签收
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        // 4.创建目的地（具体是队列还是主题topic）
        Topic topic = session.createTopic(TOPIC_NAME );
        // 5.创建消息的生产者

        MessageConsumer consumer = session.createConsumer(topic);
        // 6 通过使用messageConsumer监听并消费消息
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                if (null != message && message instanceof TextMessage) {
                    TextMessage textMessage=(TextMessage) message;
                    try {
                        System.out.println("消费者接收到Topic消息："+textMessage.getText());
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        System.in.read();
        consumer.close();
        session.close();
        connection.close();
    }
}
