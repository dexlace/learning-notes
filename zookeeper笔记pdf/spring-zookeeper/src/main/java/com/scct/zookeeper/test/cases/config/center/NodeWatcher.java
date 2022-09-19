package com.scct.zookeeper.test.cases.config.center;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

// 处理节点监听
public class NodeWatcher implements Watcher, AsyncCallback.StatCallback, AsyncCallback.DataCallback {

    private ZooKeeper zk;

    private MyConf myConf;

    private CountDownLatch cdl = new CountDownLatch(1);

    public NodeWatcher(ZooKeeper zk) {
        this.zk = zk;
    }

    public void setMyConf(MyConf myConf) {
        this.myConf = myConf;
    }

    // 给节点绑定一个事件
    @Override
    public void process(WatchedEvent watchedEvent) {
        switch (watchedEvent.getType()) {

            case None:
                break;
            case NodeCreated:
                zk.getData("/testConf", this, this, "version-1");
                System.err.println("/testConf节点创建......");
                cdl.countDown();
                break;
            case NodeDeleted:
                myConf.setConf("");
                cdl = new CountDownLatch(1);
                System.err.println("/testConf节点被删除......");
                break;
            case NodeDataChanged:
                zk.getData("/testConf", this, this, "version-1");
                System.err.println("/testConf节点被修改......");
                break;
            case NodeChildrenChanged:
                break;
            case DataWatchRemoved:
                break;
            case ChildWatchRemoved:
                break;

        }

    }

    // exists 的回调
    @Override
    public void processResult(int i, String s, Object o, Stat stat) {
        // 如果stat不为空，说明有节点，可以直接取数据
        if (stat != null) {
            // 这里第二个参数还要传watcher，为了持续绑定事件
            zk.getData("/testConf", this, this, "version-1");
            // 存在且计数器没有变为0  则需要减去1 被创建时也需要减去1
            if(cdl.getCount()==1){
                cdl.countDown();
            }
        }


    }

    // getData的回调
    @Override
    public void processResult(int i, String s, Object o, byte[] bytes, Stat stat) {
        if(bytes==null){
            myConf.setConf("Node of no value");
        }else{
            myConf.setConf(new String(bytes));
        }

    }


    // 主要调用exists方法
    public void aWait() {
        try {
            System.err.println("await start ...");
            zk.exists("/testConf", this, this, "version-0");
            // 如果不存在会一直阻塞
            cdl.await();
            System.err.println("await finish ...");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}


