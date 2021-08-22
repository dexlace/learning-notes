package com.scct.rabbitmq.utils;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/3/18
 */
public class RabbitMQUtils {

    private static ConnectionFactory connectionFactory;

    static {
        //一、创建连接工厂
        connectionFactory = new ConnectionFactory();
    }

    public static Connection getConnection() throws IOException, TimeoutException {
        try {
            connectionFactory.setHost("192.168.205.107");
            connectionFactory.setPort(5672);
            connectionFactory.setUsername("ems");
            connectionFactory.setPassword("123");
            connectionFactory.setVirtualHost("/ems");


            // 二、获取连接对象
            return connectionFactory.newConnection();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;


    }


    public static void closeConnectionAndChanel(Channel channel, Connection connection) {

        try {
            if (channel != null)
                channel.close();
            if (connection != null)
                connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
