package com.atguigu.springcloud.controller;

import com.atguigu.springcloud.entities.CommonResult;
import com.atguigu.springcloud.entities.Payment;
import com.atguigu.springcloud.service.PaymentFeignService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/3/6
 */
//使用起来就相当于是普通的service。
    @RestController
public class CustomerFeignController {
    @Resource
    private PaymentFeignService paymentFeignService;

    @GetMapping("/customer/feign/payment/{id}")
    public CommonResult<Payment> getPaymentById(@PathVariable("id") Long id){
        return paymentFeignService.getPaymentById(id);
    }

    @GetMapping(value = "/customer/payment/feign/timeout")
    public String paymentFeignTimeout() {
        // openfeign-ribbon 客户端一般默认等待一秒钟
        return paymentFeignService.paymentFeignTimeout();
    }
}
