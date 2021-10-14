package com.atguigu.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/3/9
 */
@SpringBootApplication
@EnableEurekaClient
public class GatewayMain9527
{
    public static void main(String[] args) {
        SpringApplication.run(GatewayMain9527.class,args);
    }
}
