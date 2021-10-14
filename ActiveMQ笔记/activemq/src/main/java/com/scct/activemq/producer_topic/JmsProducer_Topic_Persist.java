package com.scct.activemq.producer_topic;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/3/13
 */
public class JmsProducer_Topic_Persist {
    public static final String ACTIVEMQ_URL = "tcp://192.168.205.103:61616";
    public static final String TOPIC_NAME ="Topic-Persist";

    public static void main(String[] args) throws JMSException {

        // 1 创建连接工厂,按照给定的url地址，采用默认的用户名和密码
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        // 2. 通过连接工厂，获取连接connection,并启动访问
        Connection connection = activeMQConnectionFactory.createConnection();

        // 3.创建会话session
        // 第一个 参数事务
        // 第二个  签收，这里选择自动签收
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        // 4.创建目的地（具体是队列还是主题topic）
        Topic topic = session.createTopic(TOPIC_NAME);
        // 5.创建消息的生产者
        MessageProducer producer = session.createProducer(topic);
        producer.setDeliveryMode(DeliveryMode.PERSISTENT);

        connection.start();
        // 6 通过使用messageProducer生产6条消息发送到MQ的队列里面
        for (int i =1; i <=6; ++i) {
            // 7 创建消息
            TextMessage textMessage = session.createTextMessage("topic msg---" + i);
            // 8.通过messageProducer发送给mq
            producer.send(textMessage);
        }
        //9 关闭资源
        producer.close();
        session.close();
        connection.close();

        System.out.println("****TOPIC消息发布到MQ完成");


    }


}
