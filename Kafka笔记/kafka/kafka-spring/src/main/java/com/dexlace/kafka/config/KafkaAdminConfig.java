package com.dexlace.kafka.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/6/3
 */
@Configuration
public class KafkaAdminConfig {

//    For more advanced features, such as assigning partitions to replicas, you can use the AdminClient directly:
    // 比如kafka-learn中的习得
    @Bean
    public KafkaAdmin admin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
                "192.168.205.120:9092,192.168.205.121:9092,192.168.205.122:9092");

        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic topic1() {
        return new NewTopic("spring-boot-first", 2, (short) 2);
    }

    @Bean
    public NewTopic topic2() {
        return new NewTopic("spring-boot-fourth", 3, (short) 3);
    }
}
