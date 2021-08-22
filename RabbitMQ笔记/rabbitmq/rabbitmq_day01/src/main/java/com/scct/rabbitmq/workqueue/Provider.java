package com.scct.rabbitmq.workqueue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.scct.rabbitmq.utils.RabbitMQUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/3/19
 */
public class Provider {
    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection= RabbitMQUtils.getConnection();

        Channel channel=connection.createChannel();

        channel.queueDeclare("work",true,false,false,null);

        for (int i=0;i<10;i++){
            channel.basicPublish("", "work", null, (i+"====>:我是消息").getBytes());
        }

        RabbitMQUtils.closeConnectionAndChanel(channel,connection);

    }
}
