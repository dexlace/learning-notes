package com.scct.zookeeper.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.concurrent.CountDownLatch;

/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/2/28
 */
@Slf4j
public class MyZkWatcher implements Watcher {

    //异步锁
    private CountDownLatch cdl;

    //标记
    private String mark;

    public MyZkWatcher(CountDownLatch cdl,String mark) {
        this.cdl = cdl;
        this.mark = mark;
    }

    //监听事件处理方法
    @Override
    public void process(WatchedEvent event) {
        log.info(mark+" watcher监听事件：{}",event);
        cdl.countDown();
    }


}
