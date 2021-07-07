package com.security.distributed.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/4/18
 */
@SpringBootApplication
@EnableDiscoveryClient
public class OrderServerMain {
    public static void main(String[] args) {
        SpringApplication.run(OrderServerMain.class,args);
    }
}
