package com.dexlace.kafka.config;

import com.dexlace.kafka.entity.Person;
import com.dexlace.kafka.serialization.PersonCustomDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.*;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/6/3
 */

@Slf4j
@Configuration
@EnableKafka
public class KafkaConsumerConfig {

//    public KafkaMessageListenerContainer(ConsumerFactory<K, V> consumerFactory,
//                                         ContainerProperties containerProperties)
//
//    public KafkaMessageListenerContainer(ConsumerFactory<K, V> consumerFactory,
//                                         ContainerProperties containerProperties,
//                                         TopicPartitionInitialOffset... topicPartitions)


//    public ConcurrentMessageListenerContainer(ConsumerFactory<K, V> consumerFactory,
//                                          ContainerProperties containerProperties)


    /**
     * 配置了一个单个的消费者
     * @return
     */
//    @Bean
//    KafkaMessageListenerContainer<String, String>
//    kafkaListenerContainer() {
//        // 监听多个主题
//        ContainerProperties containerProps = new ContainerProperties("spring-boot-first", "spring-boot-second");
//        // 监听主题和分区
//        ContainerProperties containerProps2 = new ContainerProperties(new TopicPartitionInitialOffset("spring-boot-first", 1));
//        // 监听符合正则表达式的主题
//        ContainerProperties containerProps3 = new ContainerProperties(Pattern.compile("spring*"));
//
//
//        // 默认是批次提交  当每一批poll()的数据被消费者监听器（ListenerConsumer）处理之后提交
////        containerProps3.setAckMode(AbstractMessageListenerContainer.AckMode.BATCH);
//
////        //  当每一条记录被消费者监听器（ListenerConsumer）处理之后提交
////        containerProps3.setAckMode(AbstractMessageListenerContainer.AckMode.RECORD);
////        // 当每一批poll()的数据被消费者监听器（ListenerConsumer）处理之后，距离上次提交时间大于TIME时提交
////        containerProps3.setAckMode(AbstractMessageListenerContainer.AckMode.TIME);
////        // 当每一批poll()的数据被消费者监听器（ListenerConsumer）处理之后，被处理record数量大于等于COUNT时提交
////        containerProps3.setAckMode(AbstractMessageListenerContainer.AckMode.COUNT);
////        // TIME |　COUNT　有一个条件满足时提交
////        containerProps3.setAckMode(AbstractMessageListenerContainer.AckMode.COUNT_TIME);
////        // 当每一批poll()的数据被消费者监听器（ListenerConsumer）处理之后, 手动调用Acknowledgment.acknowledge()后提交
////        containerProps3.setAckMode(AbstractMessageListenerContainer.AckMode.MANUAL);
//        // 手动调用Acknowledgment.acknowledge()后立即提交
//        containerProps3.setAckMode(AbstractMessageListenerContainer.AckMode.MANUAL_IMMEDIATE);
//      containerProps.setMessageListener(new MessageListener<String,String>() {
//        @Override
//        public void onMessage(ConsumerRecord<String, String > record) {
//            log.info(" topic:{},partition:{},offset:{},key:{}, value:{}",record.topic(),record.partition(),record.offset(),record.key(),record.value());
//        }
//    });
//
//        KafkaMessageListenerContainer<String, String> container= new KafkaMessageListenerContainer<>(consumerFactory(),
//                containerProps3);
//
//        return container;
//
//    }


  // 新建一个异常处理器，用@Bean注入
    @Bean
    public ConsumerAwareListenerErrorHandler consumerAwareErrorHandler() {
        return (message, exception, consumer) -> {
         log.info("消费异常：{}",message.getPayload());
         return null;
        };
    }




    /**
     * 消费者工厂  string反序列化方式
     *
     * @return
     */
    @Bean
    KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>>
    stringFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        // 设置一个消费工厂
        factory.setConsumerFactory(stringConsumerFactory());
        // 设置并发量，即有三个消费者，会创建3个KafkaMessageListenerContainer
        factory.setConcurrency(3);
        factory.getContainerProperties().setPollTimeout(3000);
        // 设置MANUAL_IMMEDIATE
        factory.getContainerProperties().setAckMode(AbstractMessageListenerContainer.AckMode.MANUAL_IMMEDIATE);
        // 每条记录都。。。则不要设置批量提交
//        factory.getContainerProperties().setAckMode(AbstractMessageListenerContainer.AckMode.RECORD);


        /**
         *
         *
         * 使用MessageListener接口实现时，当消费者拉取消息之后，消费完成会自动提交offset，即enable.auto.commit为true时，适合使用此接口
         * public interface MessageListener<K, V> {} 1
         *
         *     void onMessage(ConsumerRecord<K, V> data);
         *
         * }
         *
         * 使用AcknowledgeMessageListener时，当消费者消费一条消息之后，不会自动提交offset，需要手动ack，即enable.auto.commit为false时，适合使用此接口
         * public interface AcknowledgingMessageListener<K, V> {} 2
         *
         *     void onMessage(ConsumerRecord<K, V> data, Acknowledgment acknowledgment);
         *
         * }
         *
         * public interface BatchMessageListener<K, V> {} 3
         *
         *     void onMessage(List<ConsumerRecord<K, V>> data);
         *
         * }
         *
         * public interface BatchAcknowledgingMessageListener<K, V> {} 4
         *
         *     void onMessage(List<ConsumerRecord<K, V>> data, Acknowledgment acknowledgment);
         *
         * }
         *BatchMessageListener和BatchAcknowledgingMessageListener接口作用与上述两个接口大体类似，只是适合批量消费消息决定是否自动提交offset
         *
         * 由于业务较重，且offset自动提交时，出现消费异常或者消费失败的情况，消费者容易丢失消息，所以需要采用手动提交offset的方式，
         *
         * 因此，这里实现了AcknowledgeMessageListener接口。
         *
         *
         */


        // 一般对应手动提交  这里设置之后listener那里不需要acknowledgment.acknowledge();
        factory.getContainerProperties().setMessageListener((AcknowledgingMessageListener<String, String>) (data, acknowledgment) -> {

            //TODO 这里具体实现个人业务逻辑
            // 最后 调用acknowledgment的ack方法，提交offset
            acknowledgment.acknowledge();

        });

        // 设置重平衡
        factory.getContainerProperties().setConsumerRebalanceListener(new ConsumerAwareRebalanceListener() {
            @Override
            public void onPartitionsRevokedBeforeCommit(Consumer<?, ?> consumer, Collection<TopicPartition> partitions) {

            }

            @Override
            public void onPartitionsRevokedAfterCommit(Consumer<?, ?> consumer, Collection<TopicPartition> partitions) {

            }

            @Override
            public void onPartitionsAssigned(Consumer<?, ?> consumer, Collection<TopicPartition> partitions) {

            }
        });

        // 可以设置批量提交
        factory.setBatchListener(true);
        return factory;


    }


    /**
     * 消费者工厂  string反序列化方式
     *
     * @return
     */
    @Bean
    KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, Person>>
    personFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Person> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        // 设置一个消费工厂
        factory.setConsumerFactory(personConsumerFactory());
        // 设置并发量，即有三个消费者，会创建3个KafkaMessageListenerContainer
        factory.setConcurrency(3);
        factory.getContainerProperties().setPollTimeout(3000);
        // 设置MANUAL_IMMEDIATE
        factory.getContainerProperties().setAckMode(AbstractMessageListenerContainer.AckMode.MANUAL_IMMEDIATE);
        // 每条记录都。。。不要设置批量提交
//        factory.getContainerProperties().setAckMode(AbstractMessageListenerContainer.AckMode.RECORD);


        /**
         *
         *
         * 使用MessageListener接口实现时，当消费者拉取消息之后，消费完成会自动提交offset，即enable.auto.commit为true时，适合使用此接口
         * public interface MessageListener<K, V> {} 1
         *
         *     void onMessage(ConsumerRecord<K, V> data);
         *
         * }
         *
         * 使用AcknowledgeMessageListener时，当消费者消费一条消息之后，不会自动提交offset，需要手动ack，即enable.auto.commit为false时，适合使用此接口
         * public interface AcknowledgingMessageListener<K, V> {} 2
         *
         *     void onMessage(ConsumerRecord<K, V> data, Acknowledgment acknowledgment);
         *
         * }
         *
         * public interface BatchMessageListener<K, V> {} 3
         *
         *     void onMessage(List<ConsumerRecord<K, V>> data);
         *
         * }
         *
         * public interface BatchAcknowledgingMessageListener<K, V> {} 4
         *
         *     void onMessage(List<ConsumerRecord<K, V>> data, Acknowledgment acknowledgment);
         *
         * }
         *BatchMessageListener和BatchAcknowledgingMessageListener接口作用与上述两个接口大体类似，只是适合批量消费消息决定是否自动提交offset
         *
         * 由于业务较重，且offset自动提交时，出现消费异常或者消费失败的情况，消费者容易丢失消息，所以需要采用手动提交offset的方式，
         *
         * 因此，这里实现了AcknowledgeMessageListener接口。
         *
         *
         */


        // 一般对应手动提交  这里设置之后listener那里不需要acknowledgment.acknowledge();
        factory.getContainerProperties().setMessageListener((AcknowledgingMessageListener<String, Person>) (data, acknowledgment) -> {

            //TODO 这里具体实现个人业务逻辑
            // 最后 调用acknowledgment的ack方法，提交offset
            acknowledgment.acknowledge();

        });

        // 设置重平衡
        factory.getContainerProperties().setConsumerRebalanceListener(new ConsumerAwareRebalanceListener() {
            @Override
            public void onPartitionsRevokedBeforeCommit(Consumer<?, ?> consumer, Collection<TopicPartition> partitions) {

            }

            @Override
            public void onPartitionsRevokedAfterCommit(Consumer<?, ?> consumer, Collection<TopicPartition> partitions) {

            }

            @Override
            public void onPartitionsAssigned(Consumer<?, ?> consumer, Collection<TopicPartition> partitions) {

            }
        });


        // 设置过滤器
        factory.setRecordFilterStrategy(consumerRecord -> {
            // 100岁的不过滤
            if(consumerRecord.value().getAge()==100){
                return false;
            }
            // 其他岁的过滤
            return true;
        });
        // 可以设置批量提交offset
        factory.setBatchListener(true);
        return factory;


    }



    /**
     * value是string的反序列化方式
     *
     * @return
     */
    @Bean
    public ConsumerFactory<String, String> stringConsumerFactory() {
        return new DefaultKafkaConsumerFactory<String, String>(stringConsumerConfigs());
    }


    /**
     * value是person的反序列化方式
     *
     * @return
     */
    @Bean
    public ConsumerFactory<String, Person> personConsumerFactory() {
        return new DefaultKafkaConsumerFactory<String, Person>(personConsumerConfigs());
    }


    /**
     * value是string的反序列化方式
     *
     * @return
     */
    @Bean
    public Map<String, Object> stringConsumerConfigs() {
        Map<String, Object> props = consumerCommonConfigs();
//        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
//        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        // 这里表示从头消费，默认是latest
//        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest" );
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");


        return props;
    }



    /**
     * value是person的反序列化方式
     *
     * @return
     */
    @Bean
    public Map<String, Object> personConsumerConfigs() {
        Map<String, Object> props = consumerCommonConfigs();
//        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
//        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        // 这里表示从头消费，默认是latest
//        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest" );
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, PersonCustomDeserializer.class);


        return props;
    }







    private Map<String, Object> consumerCommonConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.205.120:9092,192.168.205.121:9092,192.168.205.122:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "spring-boot-consumer");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
//        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
//        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        // 这里表示从头消费，默认是latest
//        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest" );
        return props;
    }


}
