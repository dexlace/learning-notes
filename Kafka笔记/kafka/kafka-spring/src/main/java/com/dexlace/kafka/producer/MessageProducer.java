package com.dexlace.kafka.producer;

import com.dexlace.kafka.entity.Person;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/6/3
 */
@Slf4j
@RestController
@RequestMapping("/producer")
public class MessageProducer {


    @Resource(name="stringKafkaTemplate")
    private KafkaTemplate<String, String> stringKafkaTemplate;

    @Resource(name="personKafkaTemplate")
    private KafkaTemplate<String, Person> personKafkaTemplate;





    @RequestMapping(value = "/sendTopic",method = RequestMethod.GET)
    public String testSend1(
            String topic) throws InterruptedException, ExecutionException, TimeoutException {

//        kafkaTemplate.send(topic, "hello-world-1");
//        kafkaTemplate.send(topic, "key-hello-2", "hello-world-2");
//
//        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, 1, "key-hello-3", "hello-world-3");
//        // 设置一个回调函数  非阻塞
//        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
//            @Override
//            public void onFailure(Throwable throwable) {
//                log.error("fail to send key-hello-3");
//            }
//
//            @Override
//            public void onSuccess(SendResult<String, String> stringStringSendResult) {
//                log.info("send success key-hello-3");
//
//            }
//        });
//
//
//        ListenableFuture<SendResult<String, String>> future2 = kafkaTemplate.send(topic, 1, "key-hello-4", "hello-world-4");
//        //可以阻塞进程，用get
//        future2.get(10, TimeUnit.SECONDS);
//
//
//        kafkaTemplate.send(new ProducerRecord<>(topic, "hello-record-1"));
//        kafkaTemplate.send(new ProducerRecord<>(topic, "key-record-2", "hello-record-2"));
//        kafkaTemplate.send(new ProducerRecord<>(topic, 1, "key-record-3", "hello-record-3"));


        //下面也是没有topic，所以是默认的topic，也需要绑定
//        ListenableFuture<SendResult<String, String>> send = kafkaTemplate.send(MessageBuilder.withPayload("Hello,World")
//                .setHeader("key-header","header-value")
//                .build());


        // kafkaTemplate.sendDefault() 需要kafkaTemplate被设定到某个topic上

        for(int i=0;i<100;i++){

            stringKafkaTemplate.send(new ProducerRecord<>(topic, i%3, "record-"+i, "bye-record-"+i));
        }

        return topic;

    }


    @RequestMapping(value = "/sendPersonTopic",method = RequestMethod.GET)
    public String testSend2(String topic){

        Person person = new Person();
        for (int i = 0; i < 100; i++) {
            log.info("sending Person Object:{}",i);
            person.setId(i);
            person.setName("personSerialization_" + i);
            person.setAge(i%2==0?18:100);
            personKafkaTemplate.send(new ProducerRecord<>("spring-boot-fourth", "client" + i, person));
        }


        return topic;
    }



}
