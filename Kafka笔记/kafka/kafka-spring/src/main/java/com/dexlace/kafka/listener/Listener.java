package com.dexlace.kafka.listener;

import com.dexlace.kafka.entity.Person;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/6/3
 */


@Slf4j
@Component
public class Listener  {


    // if you are using the same listener in multiple containers (or in a ConcurrentMessageListenerContainer)
    // you should store the callback in a ThreadLocal or some other structure keyed by the listener Thread.

//    @KafkaListener(topics = {"spring-boot-first"})
//    @KafkaListener(topicPattern = "spring*")
    // 注解可以多种设置，只要设置就是了clientIdPrefix会在配置了很多个listener的基础时添加名字前缀
//    @KafkaListener(topicPartitions = {
////            @TopicPartition(topic = "spring-boot-first",partitions = {"0","1","2"}),
////            @TopicPartition(topic = "spring-boot-second",partitions = {"0"},
////            partitionOffsets = @PartitionOffset(partition = "1",initialOffset = "100"))
////    },clientIdPrefix = "myClientId")
////    public void listen1(String data){
////
////    }


//    @KafkaListener(topicPartitions = {
//            @TopicPartition(topic = "spring-boot-first", partitions = {"0", "1", "2"})},
//            clientIdPrefix = "myClientId")
//    public void listen2(String data, Acknowledgment acknowledgment) {
//        acknowledgment.acknowledge();
//    }
//
//
//    @KafkaListener(id = "hhh",topics = {"spring-boot-first"})
//    public void listen3(
//
//                        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
//                        @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long ts,
//                        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
//                        @Header(KafkaHeaders.OFFSET) int offset,
//                        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
//                        @Payload String value
//
//
//    ) {
//        // 批量提交时很乱
//        // 适合模式：自动提交，且为record模式
//        log.info(" topic:{},timeStap:{},partition:{},offset:{},key:{}, value:{}",topic, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(ts)),partition,offset,key,value);
//
//    }


/******************************************用对应的value的类型接收就行*******************************************************/
//    @KafkaListener(id="listen1",topics = {"spring-boot-first"})
//    public void listen1(String data) {
//        log.info("listen data hello:"+data);
//
//    }
//
//    @KafkaListener(id="listen11",topicPartitions ={@TopicPartition(topic = "spring-boot-first", partitions = { "1"})} )
//    public void listen11(String data) {
//        log.info("listen data spring:"+data);
//
//    }
//
//
//    @KafkaListener(id="listen21",topicPartitions ={@TopicPartition(topic = "spring-boot-first", partitions = { "0"})} )
//    public void listen21(String data) {
//        log.info("listen data spring21:"+data);
//
//    }

    /**************************************************ConsumerRecord**********************************************************************/

//    @KafkaListener(id = "record1", topics = "spring-boot-first")
    @KafkaListener(id = "record1", topicPartitions ={@TopicPartition(topic = "spring-boot-third", partitions = { "0"})}
      ,containerFactory = "stringFactory")
    public void listenRecord(List<ConsumerRecord<String, String>> list) {
        for(ConsumerRecord<String, String> record:list){
            log.info(" topic:{},partition:{},offset:{},key:{}, value:{}",record.topic(),record.partition(),record.offset(),record.key(),record.value());

        }

    }



    @KafkaListener(id = "record2", topicPartitions ={@TopicPartition(topic = "spring-boot-third", partitions = { "1"})} ,containerFactory = "stringFactory")
    public void listenRecord2(List<ConsumerRecord<String, String>> list,Acknowledgment ack) {
        for(ConsumerRecord<String, String> record:list){
            log.info(" topic:{},partition:{},offset:{},key:{}, value:{}",record.topic(),record.partition(),record.offset(),record.key(),record.value());
//            ack.acknowledge();
        }
        ack.acknowledge();

    }






//    /**
//     * 从2.0版本之后id可以覆盖已经设置的groupId
//     * @param list
//     */
//    @KafkaListener(id = "listenMessage1",topics = {"spring-boot-first"})
//    public void listen4(List<Message<?>> list) {
//
//    }
//
//    @KafkaListener(id = "message2", topicPartitions ={@TopicPartition(topic = "spring-boot-first", partitions = { "0"})} )
//    public void listenMessage2(List<Message<?>> list, Acknowledgment ack) {
//
//        for (Message<?> message:list){
//            log.info(" message header:{}, message load:{}",message.getHeaders().size(),message.getPayload());
//            ack.acknowledge();
//        }
//
//    }
//
//
//    @KafkaListener(id="listen6",topics = {"spring-boot-second"})
//    public void listen6(List<Message<?>> list) {
//
//    }
//
//    @KafkaListener(id="listen7",topics = {"spring-boot-second"})
//    public void listen7(List<Message<?>> list, Acknowledgment ack) {
//
//    }




//    @KafkaListener(id="listen8",topics = {"spring-boot-second"})
////    @SendTo("spring-boot-first")
//    public void listen8(List<Message<?>> list, Acknowledgment ack) {
//
//    }



    /***********************************Person类的序列化和反序列化测试,使用消费异常处理器********************************/
    @KafkaListener(id = "personRecord",
            topicPartitions ={@TopicPartition(topic = "spring-boot-fourth", partitions = { "0"})},
            containerFactory = "personFactory",errorHandler = "consumerAwareErrorHandler"
            )
    public void listenPerson(List<ConsumerRecord<String, Person>> list) {

        log.info("size:{}",list.size());
        for(ConsumerRecord<String, Person> record:list){
            log.info(" topic:{},partition:{},offset:{},key:{}, value:{}",record.topic(),record.partition(),record.offset(),record.key(),record.value());

        }

    }





}
