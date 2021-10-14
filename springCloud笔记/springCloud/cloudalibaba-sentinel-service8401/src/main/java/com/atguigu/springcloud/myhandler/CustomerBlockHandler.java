package com.atguigu.springcloud.myhandler;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.atguigu.springcloud.entities.CommonResult;

/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/3/29
 */
public class CustomerBlockHandler {

    public static CommonResult handleException(BlockException exception){

        return new CommonResult(200,"客户自定义的限流处理信息......CustomerBlock");
    }

    public static CommonResult handleException2(BlockException exception){

        return new CommonResult(200,"客户自定义的限流处理信息2......CustomerBlock");
    }
}

