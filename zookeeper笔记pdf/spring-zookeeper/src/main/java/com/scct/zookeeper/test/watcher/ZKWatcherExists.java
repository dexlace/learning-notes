package com.scct.zookeeper.test.watcher;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/3/2
 */
public class ZKWatcherExists {
    // 计数器对象
    static CountDownLatch countDownLatch = new CountDownLatch(1);
    //连接对象
    static ZooKeeper zooKeeper;

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        zooKeeper = new ZooKeeper("192.168.205.100", 5000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("连接对象的参数!");
                // 连接成功
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    countDownLatch.countDown();
                }
                System.out.println("path=" + event.getPath());
                System.out.println("eventType=" + event.getType());
            }
    });
        countDownLatch.await();


//        //下面是检查节点是否存在的
//        // 第一个参数：节点路径
//        // 第二个参数：是否使用连接对象中的watcher
        Stat exists = zooKeeper.exists("/watcher1", true);
        Thread.sleep(5000);
        System.out.println("是否存在"+exists.getCzxid()+" "+exists.getCtime());

//        Stat stat= zooKeeper.exists("/watcher1", new Watcher() {
//            @Override
//            public void process(WatchedEvent event) {
//                System.out.println("自定义watcher");
//                System.out.println("path=" + event.getPath());
//                System.out.println("eventType=" + event.getType());
//            }
//        });
//
//        System.out.println(stat.getVersion()+" "+stat.getCtime()+" "+stat.getCzxid());

    }



}
