package com.scct.activemq.consumer_topic;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.io.IOException;

/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/3/13
 */
public class JmsConsumer_Topic_Persist {

    public static final String ACTIVEMQ_URL = "tcp://192.168.205.103:61616";
    public static final String TOPIC_NAME = "Topic-Persist";

    public static void main(String[] args) throws JMSException, IOException {
        System.out.println("*****lisi");
        // 1 创建连接工厂,按照给定的url地址，采用默认的用户名和密码
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        // 2. 通过连接工厂，获取连接connection,并启动访问
        Connection connection = activeMQConnectionFactory.createConnection();
        // 设置连接的client ID
        connection.setClientID("lisi");
        // 3.创建会话session
        // 第一个 参数事务
        // 第二个  签收，这里选择自动签收
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        // 4.创建目的地（具体是队列还是主题topic）
        Topic topic = session.createTopic(TOPIC_NAME);

        // 消息的订阅者
        TopicSubscriber topicSubscriber = session.createDurableSubscriber(topic, "remark ...");
        connection.start();


        Message message = topicSubscriber.receive();
        while (null != message) {
            TextMessage textMessage=(TextMessage) message;
            System.out.println("******收到的持久化topic" + textMessage.getText());
//            message = topicSubscriber.receive(1000);
            message = topicSubscriber.receive();

        }
        /*1 一定要先运行一次消费者，等于向MQ注册，类似订阅了这个主题
            2. 然后再运行生产者发送消息，此时
            3. 无论消费者是否在线，都会接收到，不在线的话，下次连接时，会把没有收到的消息都接收下来
        *
        * */
        session.close();
        connection.close();
    }
}
