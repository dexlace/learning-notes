package com.scct.zookeeper.test.cases.lock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/3/3
 */
public class MyLock {
    // ZK的连接地址
    String IP = "192.168.205.100:2181,192.168.205.101:2181,192.168.205.102:2181";
    // 计数器对象
    CountDownLatch countDownLatch = new CountDownLatch(1);

    // zookeeper配置信息
    ZooKeeper zooKeeper;

    private static final String LOCK_ROOT_PATH = "/Locks";
    private static final String LOCK_NODE_NAME = "Lock_";
    // 完整的临时有序节点路径
    private String lockPath;


    //监视器
    Watcher watcher = new Watcher() {
        @Override
        public void process(WatchedEvent watchedEvent) {
            if(watchedEvent.getType()==Event.EventType.NodeDeleted){
                synchronized (this){
                    notifyAll();
                }
            }

        }
    };

    public MyLock() {
        try {
            zooKeeper = new ZooKeeper(IP, 5000, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (event.getType() == Event.EventType.None) {
                        if (event.getState() == Event.KeeperState.SyncConnected) {
                            System.out.println("连接成功");
                            countDownLatch.countDown();
                        }
                    }
                }
            });
            countDownLatch.await();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public  void acquireLock() throws  Exception{
        // 创建锁节点
        createLock();
        // 尝试获取锁
        attemptLock();
    }

    private void createLock() throws Exception {
        // 其实随时在动态创建节点
        //判断Locks父节点是否存在，不存在就创建
        Stat stat = zooKeeper.exists(LOCK_ROOT_PATH, false);
        if (stat == null) {
            zooKeeper.create(LOCK_ROOT_PATH, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        // 创建了临时有序节点
        lockPath = zooKeeper.create(LOCK_ROOT_PATH + "/" + LOCK_NODE_NAME, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
        System.out.println("节点创建成功" + lockPath);
    }

    private void attemptLock() throws Exception {
        // 获取Locks节点下的所有节点
        List<String> list = zooKeeper.getChildren(LOCK_ROOT_PATH, false);
        //对子节点进行排序
        Collections.sort(list);

        // 因为随时访问之后会删除节点，所以要看当前的节点是否在最前面
        int index = list.indexOf(lockPath.substring(LOCK_ROOT_PATH.length() + 1));
        if (index == 0) {
            System.out.println("获取锁成功！");
            return;
        } else {
            // 上一个节点的路径,这里只有名称，不是完整路径
            String path = list.get(index - 1);
            Stat stat = zooKeeper.exists(LOCK_ROOT_PATH + "/" + path, watcher);
            if (null == stat) {
                // 怕的是上一个节点在上述的查找过程中被删除了，所以尝试再次尝试获取锁
                attemptLock();
            } else {
                // 如果没删除，那么就监视上一个节点
                synchronized (watcher) {
                    watcher.wait();
                }
                attemptLock();
            }
        }
    }

    //释放锁,完整路径，注意注意  节点的之在这里很不重要
    public void releaseLock() throws Exception {
        // 删除临时有序节点
        zooKeeper.delete(this.lockPath, -1);
        zooKeeper.close();
        System.out.println("锁已经释放:" + this.lockPath);
    }


}
