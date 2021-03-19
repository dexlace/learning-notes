package com.scct.zookeeper.test.watcher;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/3/2
 */
public class ZKConnectionWatcher implements Watcher {
   // 计数器对象
    static CountDownLatch countDownLatch=new CountDownLatch(1);
    //连接对象
    static ZooKeeper zooKeeper;

    @Override
    public void process(WatchedEvent event) {
        try {
            // 事件类型
            if (event.getType() == Event.EventType.None) {
                // 客户端与服务器正常连接
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    System.out.println("连接创建成功!");
                    // countDownlatch减一，主线程继续进行
                    countDownLatch.countDown();
                    // 客户端与服务器断开连接
                } else if (event.getState() == Event.KeeperState.Disconnected) {
                    System.out.println("断开连接！");
                    // 会话session失效
                } else if (event.getState() == Event.KeeperState.Expired) {
                    System.out.println("会话超时!");
                    zooKeeper = new ZooKeeper("192.168.205.100:2181", 5000, new ZKConnectionWatcher());
                  // 身份认证失败
                } else if (event.getState() == Event.KeeperState.AuthFailed) {
                    System.out.println("认证失败！"); } }
        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    public static void main(String[] args) {
        try{
            zooKeeper=new ZooKeeper("192.168.205.100,192.168.205.101,192.168.205.102",5000,new ZKConnectionWatcher());
            // 阻塞线程等待连接的创建
            countDownLatch.await();
            // 会话id
            System.out.println(zooKeeper.getSessionId());

//            // 添加授权用户
//            zooKeeper.addAuthInfo("digest1","scut:1234561".getBytes());
//            byte [] bs=zooKeeper.getData("/gantamade",false,null);
//            System.out.println(new String(bs));
//            Thread.sleep(50000);
            zooKeeper.close();
            System.out.println("结束");
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }



}
