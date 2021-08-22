package com.scct.rabbitmq.workqueue;

import com.rabbitmq.client.*;
import com.scct.rabbitmq.utils.RabbitMQUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/3/19
 */
public class SlowCustomer {
    public static void main(String[] args) throws IOException, TimeoutException {

        Connection connection= RabbitMQUtils.getConnection();

        Channel channel=connection.createChannel();


        channel.queueDeclare("work",true,false,false,null);

        channel.basicQos(1);//一次只接受一条未确认的消息
        channel.basicConsume("work",false,new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                try {
                    Thread.sleep(2000);   //处理消息比较慢 一秒处理一个消息
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("消费者2: "+new String(body));
                channel.basicAck(envelope.getDeliveryTag(),false);//手动确认消息
            }
        });
    }
}
