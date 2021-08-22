package com.scct.rabbitmq.helloworld;

import com.rabbitmq.client.*;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/3/18
 */
public class Customer {

    public static void main(String[] args) throws IOException, TimeoutException {
        //一、创建连接工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.205.107");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("ems");
        connectionFactory.setPassword("123");
        connectionFactory.setVirtualHost("/ems");


        // 二、获取连接对象
        Connection connection = connectionFactory.newConnection();


        //三、创建通道
        Channel channel = connection.createChannel();
        //参数1：队列名称
        // 参数2: 是否持久化
        // 参数3:是否独占队列
        // 参数4:是否自动删除
        // 参数5:其他属性
        channel.queueDeclare("hello", false, false, false, null);

        // 四、消费消息
        // 参数1：消费哪个队列的消息  队列名称
        // 参数2：开始消费的自动确认机制
        // 参数3：消费时的回调接口
        channel.basicConsume("hello", true, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("new String(body)" + new String(body));
            }
        });

        // 如果channel和connection不关闭  则会一直监听队列
        //channel.close();
        //connection.close();
    }
}



