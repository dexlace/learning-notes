package com.scct.zookeeper.test;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/2/28
 */
public class MyZookeeperClient {
    public static void main(String[] args) {
        try {
            CountDownLatch countDownLatch=new CountDownLatch(1);
            ZooKeeper zooKeeper=new ZooKeeper("192.168.205.100", 1000, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if(watchedEvent.getState()==Event.KeeperState.SyncConnected){
                        System.out.println("连接创建成功");
                        // 只有这里减了1，那么才不会阻塞
                        countDownLatch.countDown();
                    }
                }
            });
            // await()方法会阻塞住线程，直到countDownlatch减为0
            countDownLatch.await();
            // 会话编号
            System.out.println(zooKeeper.getSessionId());
            zooKeeper.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    }
