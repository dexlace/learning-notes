package com.scct.zookeeper.test.cases.config.center;

import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ConfigCenterTest {

    ZooKeeper zk;

    @Before
    public void connect() {
        zk = ZkConnection.getZK();
        System.out.println("connection finish...");
    }

    @After
    public void close() {
        try {
            zk.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testContent() {
        NodeWatcher nodeWatcher = new NodeWatcher(zk);
        MyConf myConf = new MyConf();
        // 设置读取的变量
        nodeWatcher.setMyConf(myConf);

        //1. 开始创建的时候没有节点，应该进行阻塞
        nodeWatcher.aWait();
        System.err.println("节点已创建...");

        // 下面的while循环是核心
        while (true) {
            nodeWatcher.aWait();
            System.err.println(myConf);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
