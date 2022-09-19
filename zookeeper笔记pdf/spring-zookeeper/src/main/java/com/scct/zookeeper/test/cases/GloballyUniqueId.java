package com.scct.zookeeper.test.cases;

import org.apache.zookeeper.*;

import java.util.concurrent.CountDownLatch;

/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/3/2
 */
public class GloballyUniqueId implements Watcher {
    // zk的连接串
    String IP = "127.0.0.1:2181";
    // 计数器对象
    CountDownLatch countDownLatch = new CountDownLatch(1);
    // 连接对象
    ZooKeeper zooKeeper;

    // 用户生成序号的节点
    String defaultPath = "/uniqueId";

    @Override
    public void process(WatchedEvent event) {
        try {
            // 捕获事件状态
            if (event.getType() == Event.EventType.None) {
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    System.out.println("连接成功");
                    countDownLatch.countDown();
                } else if (event.getState() == Event.KeeperState.Disconnected) {
                    System.out.println("连接断开!");
                } else if (event.getState() == Event.KeeperState.Expired) {
                    System.out.println("连接超时!");
                    // 超时后服务器端已经将连接释放，需要重新连接服务器端
                    zooKeeper = new ZooKeeper("127.0.0.1:2181", 6000, new GloballyUniqueId());
                } else if (event.getState() == Event.KeeperState.AuthFailed) {
                    System.out.println("验证失败!");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    // 构造方法
    public GloballyUniqueId() {
        try {
            //打开连接
            zooKeeper = new ZooKeeper(IP, 5000, this);
            // 阻塞线程，等待连接的创建成功
            countDownLatch.await();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // 生成id的方法
    public String getUniqueId() {
        String path = "";
        try {
            // 创建临时有序节点
            path = zooKeeper.create(defaultPath, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
//        System.err.println("path: "+path);
        // /uniqueId0000000001 截取id号
        return path.substring(9);
    }

    public static void main(String[] args) {
        GloballyUniqueId globallyUniqueId = new GloballyUniqueId();
        for (int i = 1; i <= 100; i++) {
            String id = globallyUniqueId.getUniqueId();
            System.err.println(Long.parseLong(id));
        }
    }

}
