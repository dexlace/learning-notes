package com.scct.boot.activemq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/3/17
 */
@SpringBootApplication
@EnableJms
@EnableScheduling
public class BootActiveMQMain {
    public static void main(String[] args) {
        SpringApplication.run(BootActiveMQMain.class,args);
    }
}
