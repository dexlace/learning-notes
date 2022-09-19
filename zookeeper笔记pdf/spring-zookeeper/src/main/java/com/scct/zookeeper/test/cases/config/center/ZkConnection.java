package com.scct.zookeeper.test.cases.config.center;

import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;


// 封装连接
public class ZkConnection {

    private static ZooKeeper zk;

    private static String connectString = "127.0.0.1:2181";

    private static CountDownLatch cdl = new CountDownLatch(1);

    public static ZooKeeper getZK() {

        try {
            zk = new ZooKeeper(connectString, 3000, new ConnectionWatcher(cdl));
            cdl.await(); // 同步阻塞，维护zk的连接创建成功
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 1. 为了保证返回的zk一定建立连接，使用CountDownLatch
        return zk;
    }
}
