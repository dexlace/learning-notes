package com.scct.rabbitmq.helloworld;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/3/18
 */
public class Provider {

    @Test
    public void testSendMessage() throws IOException, TimeoutException {
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
        channel.queueDeclare("hello",false,false,false,null);

        // 发布消息
        // 参数一：交换机名称
        // 参数二：队列名称
        // 参数三：传递消息的额外设置
        //  参数四：消息的具体内容
        channel.basicPublish("","hello", null,"hello rabbitmq".getBytes());
        channel.close();
        connection.close();
    }
}
