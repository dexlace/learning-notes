package com.dexlace.kafka.producer.partiton;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

import java.util.Map;

/**
 * @Author: xiaogongbing
 * @Description: 这里对主题设置分区器，根据key来设置key的形式是client数字的结合
 */
public class CustomPartition implements Partitioner {
    @Override
    // 返回要放入的paritition
    public int partition(String topic,  // 主题
                         Object key,  // 给定的key
                         byte[] keyBytes,  // key序列化后的值
                         Object value,  // 要放入的值
                         byte[] valueBytes, // 序列化后的值
                         Cluster cluster) // 当前集群
    {

//        System.out.println(key);
        String keyStr = key + "";  // 转化成String
        String keyInt = keyStr.substring(6);
//        System.out.println("keyStr : " + keyStr + "keyInt : " + keyInt);

        int i = Integer.parseInt(keyInt);

        return i%3;
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}
