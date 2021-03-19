package com.scct.zookeeper.test.cases.lock;

/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/3/3
 */
public class TicketSeller {
    private void sell() {
        System.out.println("售票开始啦");
        //线程随机休眠数毫秒，模拟现实中的费时操作
        int sleepMills = 5000;
        try {

            // 代表复杂的逻辑执行了一段时间
            Thread.sleep(sleepMills);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("售票结束");
    }

    public void sellTicketWithLock() throws Exception {
        MyLock lock = new MyLock();
        // 获取锁
        lock.acquireLock();
        sell();
//         释放锁
        lock.releaseLock();
    }

    public static void main(String[] args) throws Exception {
//        for (int i = 0; i < 10; i++) {
//            Thread thread=new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        new TicketSeller().sellTicketWithLock();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            },"线程："+i);
//            thread.start();
//
//        }
//    }
        TicketSeller ticketSeller = new TicketSeller();
        for (int i = 0; i < 10; i++) {
            ticketSeller.sellTicketWithLock();
        }
    }

}
