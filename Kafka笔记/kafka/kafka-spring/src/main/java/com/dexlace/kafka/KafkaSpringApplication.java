package com.dexlace.kafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;

/**
 * 非常关键  排除kafka的自动加载  不然只能有一个消费容器工厂
 */
@SpringBootApplication(exclude = {KafkaAutoConfiguration.class})
public class KafkaSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaSpringApplication.class, args);
    }

}
