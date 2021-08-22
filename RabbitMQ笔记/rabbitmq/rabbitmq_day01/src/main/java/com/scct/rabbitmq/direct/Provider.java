package com.scct.rabbitmq.direct;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.scct.rabbitmq.utils.RabbitMQUtils;


import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Provider {
    public static void main(String[] args) throws IOException, TimeoutException {
        //获取连接对象
        Connection connection=RabbitMQUtils.getConnection();
        //获取连接通道对象
        Channel channel=connection.createChannel();

        //步骤一：通过通道声明交换机  参数1:交换机名称  参数2:direct  路由模式
        channel.exchangeDeclare("logs_direct","direct");
        //步骤二：发送消息
        String routingKey="error";
        channel.basicPublish("logs_direct",routingKey,null,("这是direct模型发布的基于route key: ["+routingKey+"] 发送的消息").getBytes());

        //关闭资源
        RabbitMQUtils.closeConnectionAndChanel(channel,connection);

    }
}
