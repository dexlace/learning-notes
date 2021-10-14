package com.atguigu.springcloud.alibaba;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/3/31
 */
@SpringBootApplication
@EnableDiscoveryClient
public class SeataStorageMain2004 {
    public static void main(String[] args) {
        SpringApplication.run(SeataStorageMain2004.class,args);
    }
}
