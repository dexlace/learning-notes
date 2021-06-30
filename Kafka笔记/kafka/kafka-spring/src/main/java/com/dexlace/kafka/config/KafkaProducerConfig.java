package com.dexlace.kafka.config;

import com.dexlace.kafka.entity.Person;
import com.dexlace.kafka.producer.partiton.CustomPartition;
import com.dexlace.kafka.serialization.PersonCustomSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: xiaogongbing
 * @Description: 构造KafkaTemplate
 * @Date: 2021/6/3
 */
@Configuration
public class KafkaProducerConfig {



    @Bean
    public KafkaTemplate<String, String> stringKafkaTemplate() {
        return new KafkaTemplate<String, String>(stringProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, Person> personKafkaTemplate() {
        return new KafkaTemplate<String, Person>(personProducerFactory());
    }


    @Bean
    public ProducerFactory<String, String> stringProducerFactory() {
        return new DefaultKafkaProducerFactory<>(stringConfigs());
    }

    @Bean
    public ProducerFactory<String, Person> personProducerFactory() {
        return new DefaultKafkaProducerFactory<>(personConfigs());
    }




    @Bean
    public Map<String, Object> stringConfigs() {
        Map<String, Object> props =commonConfigs();
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return props;
    }

    @Bean
    public Map<String, Object> personConfigs() {
        Map<String, Object> props =commonConfigs();
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, PersonCustomSerializer.class);
        // 自定义分区器
        props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, CustomPartition.class);
        return props;
    }






    private Map<String, Object> commonConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.205.120:9092,192.168.205.121:9092,192.168.205.122:9092");
        /**
         * 确认机制
         */
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        /**
         * 重试次数
         */
        props.put(ProducerConfig.RETRIES_CONFIG, "0");
        /**
         * 批次大小
         */
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, "16384");
        /**
         * 等待时间：1000ms
         */
        props.put(ProducerConfig.LINGER_MS_CONFIG, "1000");
        /**
         * 缓冲区大小
         */
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, "33554432");
        // See https://kafka.apache.org/documentation/#producerconfigs for more properties
        return props;
    }
}
