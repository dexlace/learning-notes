package com.atguigu.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/3/23
 */
@SpringBootApplication
@EnableDiscoveryClient
public class CustomerOrderMain80 {
    public static void main(String[] args) {
        SpringApplication.run(CustomerOrderMain80.class,args);
    }
}
