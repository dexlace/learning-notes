package com.atguigu.springcloud.alibaba.service.impl;


import com.atguigu.springcloud.alibaba.dao.StorageDao;
import com.atguigu.springcloud.alibaba.service.StorageService;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class StorageServiceImpl implements StorageService {

    @Resource
    private StorageDao storageDao;

    @Override
    public void decrease(Long productId, Integer count) {
        try {
          Thread.sleep(5000);
            storageDao.decrease(productId,count);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
