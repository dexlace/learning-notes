# 《Java并发编程的艺术》解读

## 一、大厂面试真题

- （百度 阿里）请描述synchronized和reentrantlock的底层实现及重入的底层原理
- （百度 阿里）请描述锁的四种状态和升级过程
- （百度）CAS的ABA问题如何解决
- （百度）请谈一下AQS,为什么AQS的底层是CAS+volatile
- （美团 阿里）请谈一下你对volatile的理解
- （美团 阿里）volatile的可见性与禁止指令重排是如何实现的
- （美团 ）CAS是什么
- （美团 顺丰）请描述一下对象的创建过程
- （美团 顺丰）对象在内存中的内存布局
- （美团 ）DCL单例为什么要加volatile
- （顺丰）Object o=new Object()在内存中占了多少字节
- （京东）聊聊你对as-if-serial和happens-before语义的理解
- （京东 阿里）你了解ThreadLocal吗？你知道ThreadLocal如何解决内存泄漏问题吗？
- （阿里）描述一下锁的分类以及JDK中的应用

## 二、第四章：Java并发编程基础

#### 1. 线程优先级

线程优先级不能作为程序正确性的以来，因为操作系统完全可以不用理会Java线程对于优先级的设定

```java
public class Priority {
    private static volatile boolean notStart = true;
    private static volatile boolean notEnd   = true;

    public static void main(String[] args) throws Exception {
        List<Job> jobs = new ArrayList<Job>();
        // 开了10个线程，前5个优先级最小，后5个优先级最大
        for (int i = 0; i < 10; i++) {
            int priority = i < 5 ? Thread.MIN_PRIORITY : Thread.MAX_PRIORITY;
            Job job = new Job(priority);
            jobs.add(job);
            Thread thread = new Thread(job, "Thread:" + i);
            thread.setPriority(priority);
            thread.start();
        }
        notStart = false;
        Thread.currentThread().setPriority(8);
        System.out.println("done.");
        TimeUnit.SECONDS.sleep(10);
        notEnd = false;

        for (Job job : jobs) {
            System.out.println("Job Priority : " + job.priority + ", Count : " + job.jobCount);
        }
    }

    static class Job implements Runnable {
        private int  priority;
        private long jobCount;

        public Job(int priority) {
            this.priority = priority;
        }

        public void run() {
            while (notStart) {
                //在这等一会儿，等待大家去争抢cpu资源
                Thread.yield();
            }
            while (notEnd) {
                Thread.yield();
                jobCount++;
            }
        }
    }
}

```

##### 解释yield()方法

| 静态方法 | 解释                                                         |
| -------- | ------------------------------------------------------------ |
| yield()  | ==<font color=red>暂停当前执行的线程对象，并执行其他线程。</font>==这个暂停是会放弃CPU资源的，并且放弃CPU的时间不确定，==<font color=red>有可能刚放弃，就获得CPU资源了</font>==，也有可能放弃好一会儿，才会被CPU执行。好像等于==Thread.Sleep(0)== |

```java
package basic;

public class YieldThread extends Thread {
    public YieldThread(String name) {
        super(name);
    }

    @Override
    public void run() {
        for (int i = 1; i <= 40; i++) {
            System.out.println(Thread.currentThread().getName() + "------" + i);
            if (i == 20) {
                Thread.yield();
            }
        }
    }


    public static void main(String[] args) {
            // 启动三个线程
            new YieldThread("李白").start();
            new YieldThread("香炉").start();
        }
}
```

**情况1**：香炉先运行到==输出第20个数==，此时香炉执行了yield()方法，也即==线程让步==，于是乎==李白抢到了CPU的执行权==。==开始执行李白这条线程==。

<img src="C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20200809103137902.png" alt="image-20200809103137902" style="zoom:50%;" />

**情况2**：李白先运行到输出第20个数，此时李白执行了yield()方法，但是==李白在让步了以后又抢到了CPU的执行权==，所以李白继续使用并往下输出了21！（这个情况试了好多次才出来！）

<img src="C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20200809103405659.png" alt="image-20200809103405659" style="zoom:50%;" />

#### 2. 线程的状态

| 状态                    |                             解释                             |
| ----------------------- | :----------------------------------------------------------: |
| 初始(NEW)               |       新创建了一个线程对象，但还没有调用start()方法。        |
| 运行(RUNNABLE)          | Java线程中将<font color=red>就绪（ready）</font>和<font color=red>运行中（running）</font>两种状态笼统的称为“运行”。线程对象创建后，==其他线程(比如main线程）调用了该对象的start()方法==。该状态的==线程位于可运行线程池中==，等待被线程调度选中，获取CPU的使用权，此时处于==就绪状态（ready）==。就绪状态的线程在获得CPU时间片后变为运行中状态（running）。 |
| 阻塞(BLOCKED)           |                    表示线程==阻塞于锁==。                    |
| 等待(WAITING)           | 进入该状态的线程==需要等待其他线程做出一些特定动作（<font color=red>通知或中断</font>）==。 |
| 超时等待(TIMED_WAITING) |     该状态不同于WAITING，它可以在指定的时间后自行返回。      |
| 终止(TERMINATED)        |                   表示该线程已经执行完毕。                   |

```java
public class ThreadState {

    private static Lock lock = new ReentrantLock();

    public static void main(String[] args) {
        new Thread(new TimeWaiting(), "TimeWaitingThread").start();
        new Thread(new Waiting(), "WaitingThread").start();
        // 使用两个Blocked线程，一个获取锁成功，另一个被阻塞
        new Thread(new Blocked(), "BlockedThread-1").start();
        new Thread(new Blocked(), "BlockedThread-2").start();
        new Thread(new Sync(), "SyncThread-1").start();
        new Thread(new Sync(), "SyncThread-2").start();
    }

    /**
     * 该线程不断的进行睡眠
     */
    static class TimeWaiting implements Runnable {
        @Override
        public void run() {
            while (true) {
                SleepUtils.second(100);
            }
        }
    }

    /**
     * 该线程在Waiting.class实例上等待
     */
    static class Waiting implements Runnable {
        @Override
        public void run() {
            while (true) {
                synchronized (Waiting.class) {
                    try {
                        Waiting.class.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 该线程在Blocked.class实例上加锁后，不会释放该锁
     */
    static class Blocked implements Runnable {
        public void run() {
            synchronized (Blocked.class) {
                while (true) {
                    SleepUtils.second(100);
                }
            }
        }
    }

    static class Sync implements Runnable {

        @Override
        public void run() {
            lock.lock();
            try {
                SleepUtils.second(100);
            } finally {
                lock.unlock();
            }

        }

    }
}
```

注意==阻塞在java.concurrent包中的Lock接口的线程状态却是等待状态==，因为java.concurrent包中的Lock接口对于阻塞的实现均使用了LockSupport类中的相关方法

#### 3. 理解中断

| 方法                                                         |                             解释                             |
| ------------------------------------------------------------ | :----------------------------------------------------------: |
| public <font color=red>static</font> boolean **interrupted** | 注意是**静态方法**，测试**当前线程**==<font color=red>是否已经中断</font>，如果当前线程已经被中断，则返回true，<font color=red>并且复位即将其复位为未中断状态</font>==。换句话说，==<font color=red>如果连续两次调用该方法，则第二次调用将返回 false</font>==（在第一次调用已清除了其中断状态之后，且第二次调用检验完中断状态前，当前线程再次中断的情况除外）。<font color=red>线程内部复位使用</font> |
| public boolean **isInterrupted**()                           | 测试**线程是否已经中断**。线程的<font color=red>中断状态不受该方法的影响</font>。 |
| public void **interrupt**()                                  |                          中断线程。                          |

中断可以理解为线程的==一个标识位属性==，它标识一个运行中的线程==是否被其他线程进行了中断操作==。其他线程通过调用该线程的 ==interrupt() 方法对其进行中断操作==。其实就是其他线程对该线程==打了个招呼，要求其中断==。

记忆：以英语的语义来区别，还有就是<font color=red>记住一个复位操作interrupted，它其实是用于线程内部的</font>

注意在Java API中可以看到，许多抛出 InterruptedException 的方法，（其实线程已经终结了，因为遇到了异常）==如Thread.sleep( long mills) 方法）这些方法在抛出InterruptedException 异常之前，JVM会将中断标识位清除，然后抛出InterruptedException，此时调用isInterrupted()仍会返回false。==

#### 4. 安全地中断线程

通过以上的==中断操作==或者在==线程内部设置中断标识变量==

```java
 private static class Runner implements Runnable {
        private long             i;

        private volatile boolean on = true;

        @Override
        public void run() {
            while (on && !Thread.currentThread().isInterrupted()) {
                i++;
            }
            System.out.println("Count i = " + i);
        }

        public void cancel() {
            on = false;
        }
    }
```

#### 5. 线程间通信

##### 5.1 volatile和synchronized关键字（==共享变量==）

虽然==对象以及成员变量分配的内存是在共享内存中的==，但是每个执行的线程还是可以==拥有一份拷贝==，最终==修改的值还是得写入内存中，才会被其他线程可见==，因此，程序执行过程中，一个线程看到的变量不一定是最新的。

**Volatile修饰变量**，就是告知程序==任何对变量的访问都需要**从共享内存中**获取==，而==对它的改变==必须同步==**刷新回内存**==，能够保证所有线程对变量的访问的可见性。

**关键字synchronized**可以==修饰方法==或者以==同步块==的形式来进行使用，它主要==确保多个线程在同一个时刻，只能有一个线程处于方法或者同步块中==，它保证了==线程对变量访问的可见性和排他性==。

对于**同步块的实现**使用了==**monitorenter和monitorexit**==指令，而**同步方法**则是依靠方法修饰符上的==**ACC_SYNCHRONIZED**==来完成的。无论采用哪种方式，其本质是对一个对象的==**监视器（monitor）**==进行获取，而这个获取过程是==**排他的**==，也就是同一时刻只能有==一个线程获取到由synchronized所保护对象的监视器==。

<img src="Java%E5%B9%B6%E5%8F%91%E7%BC%96%E7%A8%8B%E7%9A%84%E8%89%BA%E6%9C%AF%E8%A7%A3%E8%AF%BB.assets/image-20210111105104492.png" alt="image-20210111105104492" style="zoom:80%;" />

##### 5.2 ==等待/通知机制==（**重点**）

等待/通知机制就是==一个【线程A】等待，一个【线程B】通知（线程A可以不用再等待了）==。

在Java语言中，实现等待通知机制主要是用：`wait()/notify()`方法实现。下面详细介绍一下这两个方法：

- `wait()`方法 ：wait()方法是==**使当前执行代码的线程进行等待**==，wait()方法是==Object类的方法==，该方法用来将当前线程置入“**欲执行队列**”中，并且在wait()所在的代码处停止执行，直到接到通知或被中断为止。在调用`wait()`方法之前，线程必须获得==**该对象的对象级别锁**==，即==只能在同步方法或者同步块中调用wait()方法。在执行wait()方法后，当前线程释放锁==。
- `notify()`方法：方法notify()**也要在同步方法或同步块中调用，即在调用前，线程也必须获得该对象的对象级别锁。**【但是如果使用了notify()，那么执行这个notify()的代码的线程会是什么状态？】

这两个方法都需要同`synchronized`关键字联用。

```java
public class WaitNotify {
    static boolean flag = true;
    static Object  lock = new Object();

    public static void main(String[] args) throws Exception {
        Thread waitThread = new Thread(new Wait(), "WaitThread");
        waitThread.start();
        TimeUnit.SECONDS.sleep(1);

        Thread notifyThread = new Thread(new Notify(), "NotifyThread");
        notifyThread.start();
    }

    static class Wait implements Runnable {
        public void run() {
            // 加锁，拥有lock的Monitor
            synchronized (lock) {
                // 当条件不满足时，继续wait，同时释放了lock的锁
                while (flag) {
                    try {
                        System.out.println(Thread.currentThread() + " flag is true. wait @ "
                                           + new SimpleDateFormat("HH:mm:ss").format(new Date()));
                        lock.wait();
                    } catch (InterruptedException e) {
                    }
                }
                // 条件满足时，完成工作
                System.out.println(Thread.currentThread() + " flag is false. running @ "
                                   + new SimpleDateFormat("HH:mm:ss").format(new Date()));
            }
        }
    }

    static class Notify implements Runnable {
        public void run() {
            // 加锁，拥有lock的Monitor
            synchronized (lock) {
                // 获取lock的锁，然后进行通知，通知时不会释放lock的锁，
                // 直到当前线程释放了lock后，WaitThread才能从wait方法中返回
                System.out.println(Thread.currentThread() + " hold lock. notify @ " + new SimpleDateFormat("HH:mm:ss").format(new Date()));
                lock.notifyAll();
                flag = false;
                SleepUtils.second(5);
            }
            // 再次加锁 这段是为了说明调用notify方法后不会立即释放lock锁，而只有等待当前线程执行完或者其他方法使其释放锁
            synchronized (lock) {
                System.out.println(Thread.currentThread() + " hold lock again. sleep @ "
                                   + new SimpleDateFormat("HH:mm:ss").format(new Date()));
                SleepUtils.second(5);
            }
        }
    }
}


```

总结==等待/通知经典范式==：

等待方遵循如下原则：

```java
// 获取对象的锁
// 条件不满足，则调用对象的wait方法，被通知后仍然要检查条件
// 条件满足后则执行对应的逻辑
synchronized(对象){
    while(条件不满足){
        对象.wait();
    }
    // 满足条件时候的逻辑
}


```

通知方遵循如下原则：

```java
// 获取对象的锁
// 改变条件
// 通知所有等待在对象上的线程
synchronized(对象){
    改变条件
       对象.notify();
}
```

##### 5.3 管道输入/输出流（了解即可）

##### 5.4 Thread.join():线程插队

```java
public class Join {
    public static void main(String[] args) throws Exception {
        Thread previous = Thread.currentThread();
        for (int i = 0; i < 10; i++) {
            // 每个线程拥有前一个线程的引用，需要等待前一个线程终止，才能从等待中返回
            Thread thread = new Thread(new Domino(previous), String.valueOf(i));
            thread.start();
            previous = thread;
        }

        TimeUnit.SECONDS.sleep(5);
        System.out.println(Thread.currentThread().getName() + " terminate.");
    }

    static class Domino implements Runnable {
        private Thread thread;

        public Domino(Thread thread) {
            this.thread = thread;
        }

        public void run() {
            try {
                // 有一个线程插队，只能等他终止之后自己的逻辑才可以继续执行
                thread.join();
            } catch (InterruptedException e) {
            }
            // 自己的逻辑
            System.out.println(Thread.currentThread().getName() + " terminate.");
        }
    }
}
```

<img src="Java%E5%B9%B6%E5%8F%91%E7%BC%96%E7%A8%8B%E7%9A%84%E8%89%BA%E6%9C%AF%E8%A7%A3%E8%AF%BB.assets/image-20210111151959967.png" alt="image-20210111151959967" style="zoom:80%;" />

##### 5.5 ThreadLocal的使用（重点）

## 三、 第五章：Java中的锁

#### 1. Lock接口

Java SE 5之后，JUC包中新增了==Lock接口（以及相关的实现类）==用来实现锁的功能

==适合Lock的场景：==

先获取锁A，然后在获取锁B，当锁B获得后，释放锁A同时获取锁C，当锁C获取后，在释放B同时获取锁D。

特点：

| 特性                   | 描述                                                         |
| ---------------------- | ------------------------------------------------------------ |
| 需要显式的获取和释放锁 |                                                              |
| ==尝试非阻塞的获取锁== | 当前线程尝试获取锁，如果如果这一时刻==锁没有被其它的线程获取到==，则==成功获取==并持有锁 |
| ==能被中断的获取锁==   | 获取到锁的线程==能够响应中断==，当前获取到锁的线程被中断时，中断异常将会被抛出，同时锁会被释放 |
| ==超时获取锁==         | 在指定的截止时间之前获取锁，如果截止时间到了仍旧无法获取锁，则返回 |

```java

Lock lock=new ReentrantLock();
lock.lock();
try{
  //   
}finally{
    lock.unlock();
}
```

Lock基本Api

| 方法名称                                                     | 描述                                                         |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| void lock()                                                  | 获取锁，调用该方法线程将会获取锁，当获取锁后，从该方法返回   |
| void ==lockInterruptibly()== throws InterruptedException     | 可中断获取锁，和lock方法的不同之处在于==该方法会响应中断，即在锁的获取中可以中断当前线程== |
| boolean ==tryLock()==                                        | ==尝试非阻塞的获取锁==，调用该方法==<font color=red>立即返回</font>==，如果能够获取在返回true，否则返回false |
| boolean tryLock(long time，TimeUnit unit) throws InterruptedException | 超时获取锁，当前线程在以下3中情况下回返回1、当前线程在超时时间内获取了锁2、当前线程在超时时间内被中断3、超时时间结束，返回false |
| void unlock()                                                | 释放锁                                                       |
| Condition newCondition()                                     | **获取等待通知组件，该组件和当前锁绑定，当前线程只有获取了锁，**才能调用该组件的wait()方法。调用后，当前线程将释放锁 |

nb    