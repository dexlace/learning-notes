package com.atguigu.springcloud.service.impl;

import com.atguigu.springcloud.service.OrderHystrixService;
import org.springframework.stereotype.Component;

/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/3/7
 */
@Component
public class OrderHystrixFallbackService implements OrderHystrixService {
    @Override
    public String paymentInfo_OK(Integer id) {
        return "------OrderPaymentFallbackService-paymentInfo_OK fall back";
    }

    @Override
    public String paymentInfo_Timeout(Integer id) {
        return "------OrderPaymentFallbackService-paymentInfo_Timeout fall back";
    }
}
