# Kafka笔记

tips:

```bash
yum install -y lrzsz
# 省去了ftp工具的操作
rz   # 打开windows的文件选择窗口，选择好文件，点击确定则可上传
sz 文件名 #打开windows资源管理器，可以选择保存位置保存到windows
```

## 一、Kafka概述

Kafka 是一个分布式的基于==【发布/订阅模式】==的消息队列（Message Queue），主要应用于大数据实时处理领域。

### 1.1 消息队列

MQ:消息队列，不再解释。

<img src="Kafka%E7%AC%94%E8%AE%B0.assets/image-20210403152021715.png" alt="image-20210403152021715" style="zoom: 50%;" />

> <font color=red>消息队列的好处</font>

1. ==解耦：==

允许你独立的扩展或修改两边的处理过程，只要确保它们遵守同样的接口约束。

2. ==冗余：==

消息队列把数据进行持久化直到它们已经被完全处理，通过这一方式规避了数据丢失风险。许多消息队列所采用的"插入-获取-删除"范式

中，在把一个消息从队列中删除之前，需要你的处理系统明确的指出该消息已经被处理完毕，从而确保你的数据被安全的保存直到你使用

完毕。

3. ==扩展性：==

因为消息队列解耦了你的处理过程，所以增大消息入队和处理的频率是很容易的，只要另外增加处理过程即可。

4. ==灵活性 & 峰值处理能力：==

在访问量剧增的情况下，应用仍然需要继续发挥作用，但是这样的突发流量并不常见。如果为以能处理这类峰值访问为标准来投入资源随

时待命无疑是巨大的浪费。使用消息队列能够使关键组件顶住突发的访问压力，而不会因为突发的超负荷的请求而完全崩溃。

5. ==可恢复性：==

系统的一部分组件失效时，不会影响到整个系统。消息队列降低了进程间的耦合度，所以即使一个处理消息的进程挂掉，加入队列中的消

息仍然可以在系统恢复后被处理。

6. ==顺序保证：==

在大多使用场景下，数据处理的顺序都很重要。大部分消息队列本来就是排序的，并且能保证数据会按照特定的顺序来处理。（Kafka保

证一个Partition内的消息的有序性）

7. ==缓冲：==

有助于控制和优化数据流经过系统的速度，解决生产消息和消费消息的处理速度不一致的情况。

8. ==异步通信：==

很多时候，用户不想也不需要立即处理消息。消息队列提供了异步处理机制，允许用户把一个消息放入队列，但并不立即处理它。想向队

列中放入多少消息就放多少，然后在需要的时候再去处理它们。

> ==<font color=red>消息队列的两种模式</font>==

消息队列分为

- <font color=red>点对点模式</font>

> 点对点模式（一对一，==消费者主动拉取数据，消息收到后消息清除==）

点对点模型通常是一个==基于拉取或者轮询的消息传送模型==，这种模型从队列中请求信息，而不是将消息推送到客户端。这个模型的特

点是发送到队列的消息==被一个且只有一个接收者接收处理==，即使有多个消息监听者也是如此。

> 每条消息由一个生产者生产，且只被一个消费者消费（即使该队列有多个消费者）。

<img src="Kafka%E7%AC%94%E8%AE%B0.assets/image-20210403153152556.png" alt="image-20210403153152556" style="zoom:50%;" />

- <font color=red>发布/订阅模式</font>（一对多，数据生产后，推送给所有订阅者，==消费者消费数据之后不会清除消息==）

> 发布订阅模型则是一个==基于推送的消息传送模型==。

> 发布订阅模型可以有==多种不同的订阅者==，==临时订阅者==只在主动监听主题时才接收消息

> 而==持久订阅者则监听主题的所有消息，即使当前订阅者不可用，处于离线状态。==

> 所有==订阅了该主题的消费者都能收到同样的消息==

<img src="Kafka%E7%AC%94%E8%AE%B0.assets/image-20210403153738975.png" alt="image-20210403153738975" style="zoom:50%;" />

### 1.2 kafka基础架构

在流式计算中，Kafka一般用来缓存数据，Storm通过消费Kafka的数据进行计算。

- Apache Kafka是一个==开源消息系统，由Scala写成==。是由Apache软件基金会开发的一个开源消息系统项目。（是基于scala开发的）

- Kafka最初是由LinkedIn公司开发，并于2011年初开源。2012年10月从Apache Incubator毕业。该项目的目标是为处理实时数据提供一个统一、高通量、低等待的平台。

- Kafka是一个==分布式消息队列==。Kafka对消息保存时根据==Topic主题进行归类==，发送消息者称为Producer，消息接受者称为Consumer，此外kafka集群有多个kafka实例组成，==<font color=red>每个实例(server)称为**broker**。每台kafka服务器称为broker</font>==

​        消息队列保存在一个一个的Topic主题中。类似于一个水池子。

- 无论是kafka集群，还是consumer都==依赖于zookeeper集群==保存一些meta信息，来保证系统可用性。

<img src="Kafka%E7%AC%94%E8%AE%B0.assets/image-20210403175410618.png" alt="image-20210403175410618" style="zoom:80%;" />

> <font color=red>Producer </font>**：**消息生产者，就是==向 kafka broker 发消息==的客户端；

> <font color=red>Consumer </font>**：**消息消费者，向 kafka broker ==取消息==的客户端；

> **==<font color=red>Consumer Group（CG）</font>==**：**消费者组**，由多个 consumer 组成。消费者组内==每个消费者负责消费<font color=red>不同分区</font>数据，一个分区只能由一个组内消费者消费==；消费者组之间互不影响。所有的消费者都属于某个消费者组，即==**消费者组是逻辑上的一个订阅者**==。 

> <font color=red>Broker </font>**：**一台 kafka 服务器就是一个 broker。一个集群由多个 broker 组成。一个 broker可以容纳多个 topic。 

> <font color=red>Topic</font> **：**可以理解为一个队列，**生产者和消费者面向的都是一个** **topic**； 

> **<font color=red>Partition</font>**：为了实现扩展性，一个非常大的 topic 可以分布到多个 broker（即服务器）上，一个topic可以分为多个partition，每个 
>
> partition 是一个有序的队列；我总是把分区与副本给误解，副本那么容易理解我还和分区搞混，其实分区就是topic的一部分

> **<font color=red>Replica</font>**：副本，为保证集群中的某个节点发生故障时，该节点上的 partition 数据不丢失，且 kafka 仍然能够继续工作，kafka 提供
>
> 了副本机制，一个 topic 的每个分区都有若干个副本，

> leader：每个分区多个副本的“主”，==生产者发送数据的对象，以及消费者消费数据的对象都是 leader==，只有一个leader。 

> follower：每个分区多个副本中的“从”，实时从 leader 中同步数据，保持和 leader 数据的同步。leader 发生故障时，某个 follower 
>
> 会成为新的 leader。 

### 1.3 Zookeeper与Kafka的关系

#### 1.3.1 broker注册

Broker是==分布式部署并且相互之间相互独立==，但是需要有一个==注册系统==能够将整个集群中的Broker管理起来，此时就使用到了

Zookeeper。

> ==<font color=red>注册节点:/brokers/ids</font>==
>
> 每个Broker在启动时，都会在Zookeeper上进行注册，在==/brokers/ids==下创建属于自己的节点，如下共==创建了3个节点==，在之后
>
> 的配置文件==server.properties==中可以找到对应的broker id信息

<img src="Kafka%E7%AC%94%E8%AE%B0.assets/image-20210403161511574.png" alt="image-20210403161511574" style="zoom:80%;" />

> Kafka使用了==全局唯一的数字==来指代每个Broker服务器，不同的Broker必须使用不同的Broker ID进行注册，创建完节点后，每个
>
> Broker就===会将自己的IP地址和端口信息记录到该节点中==去。其中，Broker创建的节点类型是==临时节点==，一旦Broker宕机，则对应的
>
> 临时节点也会被自动删除。
>
> 如下图所示为3个broker id节点的信息

<img src="Kafka%E7%AC%94%E8%AE%B0.assets/image-20210403163356699.png" alt="image-20210403163356699" style="zoom:67%;" />

#### 1.3.2  Topic注册

在Kafka中，同一个Topic的消息会被分成==多个分区==并将其分布在==多个Broker==上，这些分区信息及与Broker的对应关系也都是Zookeeper

在维护，由专门的节点来记录，如：

> ==<font color=red>注册的节点：/brokers/topics</font>==
>
> Kafka中每个Topic都会以==/brokers/topics/[topic]==的形式被记录，如/brokers/topics/login和/brokers/topics/search

![image-20210403165939794](Kafka%E7%AC%94%E8%AE%B0.assets/image-20210403165939794.png)

#### 1.3.3 生产者负载均衡

由于同一个Topic消息会被分区并将其分布在多个Broker上，因此，生产者需要将消息合理地发送到这些分布式的Broker上，那么如何实

现生产者的负载均衡，Kafka支持传统的四层负载均衡，也支持Zookeeper方式实现负载均衡。

- ==四层负载均衡==，根据生产者的IP地址和端口来为其确定一个相关联的Broker。通常，一个生产者只会对应单个Broker，然后该生产者

产生的消息都发往该Broker。这种方式逻辑简单，每个生产者不需要同其他系统建立额外的TCP连接，只需要和Broker维护单个TCP连接

即可。但是，其无法做到真正的负载均衡，因为实际系统中的每个生产者产生的消息量及每个Broker的消息存储量都是不一样的，如果有

些生产者产生的消息远多于其他生产者的话，那么会导致不同的Broker接收到的消息总数差异巨大，同时，生产者也无法实时感知到

Broker的新增和删除。

- 使用==Zookeeper进行负载均衡==，由于每个Broker启动时，都会完成Broker注册过程，生产者会通过该节点的变化来动态地感知到

Broker服务器列表的变更，这样就可以实现动态的负载均衡机制。

#### 1.3.4 消费者负载均衡

与生产者类似，Kafka中的消费者同样需要进行负载均衡来实现多个消费者合理地从对应的Broker服务器上接收消息，==每个消费者组包==

==含若干消费者==，每条消息都只会发送给分组中的一个消费者，不同的消费者分组消费自己特定的Topic下面的消息，互不干扰。

## 二、Kafka集群部署和命令行操作

### 2.1 集群安装

> 下载解压，具体自然不用多说，kafka_2.11-0.11.0.3.tgz，前面是scalar版本，后面是kafka版本。
>
> 三台虚拟机的ip分别为192.168.205.100；192.168.205.101；192.168.205.102
>
> ==注意：需要更改主机名，和主机名与ip的映射==
>
> ```bash
> # 修改主机名
> 在文件中修改/etc/hostname
> # 主机名与ip的映射
> 192.168.205.100  kafka100
> 192.168.205.101  kafka101
> 192.168.205.102  kafka102
> ```
>
> 注意我们使用自己安装的zookeeper，不使用自带的，所以自己要安装好
>
> 本文解压目录在/home/doglast/software/kafka
>
> 1. 在解压目录下mkdir logs
> 2. 在解压目录下修改config/server.properties
>
> ```properties
> ###################################################
> #broker的全局唯一编号，不能重复
> broker.id=0
> ###################################################
> 
> #删除topic功能使能
> delete.topic.enable=true
> 
> #处理网络请求的线程数量
> num.network.threads=3
> 
> #用来处理磁盘IO的现成数量
> num.io.threads=8
> 
> #发送套接字的缓冲区大小
> socket.send.buffer.bytes=102400
> 
> #接收套接字的缓冲区大小
> socket.receive.buffer.bytes=102400
> 
> #请求套接字的缓冲区大小
> socket.request.max.bytes=104857600
> ################################################################################
> ##############################################################################
> #kkafka持久化消息的目录，可以用逗号分隔设置多个  #最好写成data。他里面的00000.log是存放的数据
> log.dirs=/home/doglast/software/kafka/logs
> #############################################################################
> ############################################################################
> #topic在当前broker上的分区个数
> num.partitions=1
> 
> #用来恢复和清理data下数据的线程数量
> num.recovery.threads.per.data.dir=1
> 
> #segment文件保留的最长时间，超时将被删除
> log.retention.hours=168
> ######################################################################################
> #配置连接Zookeeper集群地址，指定了zk的集群地址
> zookeeper.connect=kafka100:2181,kafka101:2181,kafka102:2181
> #####################################################################################
> 
> ```
>
> 注意==broker.id不能重复==，分别在三台虚拟机上配置为0，1，2，<font color=red>除此之外，其他都一样</font>
>
> 这里使能了删除topic的功能，即==delete.topic.enable=true==
>
> 其次注意kafka运行日志的位置，这里为==log.dirs=/home/doglast/software/kafka/logs==
>
> 最后是zk的集群位置==zookeeper.connect=kafka100:2181,kafka101:2181,kafka102:2181==
>
> 3. 配置一下环境变量，我习惯~/.bash_profile，自然不用多说

> ==启动集群，依次启动三个节点的kafka==

``` bash
[kafka目录]$ kafka-server-start.sh -daemon config/server.properties #-daemon表示后台启动 启动后可以用jps查看
```

> ==关闭集群==

```bash
[kafka目录]$ kafka-server-stop.sh stop 
```

### 2.2 kafka命令行操作

#### 2.2.1 topic操作

> <font color=red>创建topic</font>

```bash
[doglast@kafka100 kafka] kafka-topics.sh --zookeeper kafka100:2181 --create --replication-factor 2 --partitions 2 --topic first
```

> <font color=red>查看某个topic的详情</font>

```bash
[doglast@kafka100 kafka]kafka-topics.sh --zookeeper kafka100:2181 --describe --topic first
```

<img src="Kafka%E7%AC%94%E8%AE%B0.assets/image-20210403230855505.png" alt="image-20210403230855505" style="zoom:80%;" />

在上述的logs文件夹下会出现==first-0，first-1==之类的文件，表示的是first主题的==第0个文件==和==第1个文件==,其分布位置如图所示

> <font color=red>查看当前服务器中的所有topic</font>

```bash
[doglast@kafka100 kafka]kafka-topics.sh --zookeeper kafka100:2181 --list
```

> <font color=red>删除topic</font>

```bash
[doglast@kafka100 kafka]kafka-topics.sh --zookeeper kafka100:2181 --delete --topic first
```

> <font color=red>修改分区数</font>
>
> 注意分区数不能大于broker的数量

```bash
[doglast@kafka100 kafka]kafka-topics.sh --zookeeper kafka100:2181 --alter --topic first --partitions 3
```

#### 2.2.2 消息生产和消费

> ==消息生产==

```bash
[doglast@kafka100 kafka]kafka-console-producer.sh --broker-list kafka100:9092  --topic first 
>hello word
>god dead
```

> ==消息消费==
>
> 以下命令会在kafka102上消费如上的消息

```bash
[doglast@kafka100 kafka]kafka-console-consumer.sh --bootstrap-server kafka102:9092 --topic first 
```

### 2.3 数据和日志分离

注意上面的logs文件夹其实==可以在kafka启动后在kafka目录下自动生成==，我们指定==log.dirs=/home/doglast/software/kafka/logs==其实会

把数据混进日志里，只能是说这个配置项取得名字实在不好，==log.dirs其实是数据目录==，为了区分数据和众多log文件，将==log.dirs另外指定==

比如指定为==kafka目录下的data文件夹（需要自己新建）==

<img src="Kafka%E7%AC%94%E8%AE%B0.assets/image-20210404000901210.png" alt="image-20210404000901210" style="zoom:80%;" />

注意为了演示以上效果，要把zookeeper中有关kafka的信息清除，即==删除zk的data下除myid文件的数据==；此外还要==删除kafka/logs的所有文件==。最后重启kafka，创建一个主题，会发现，数据和日志分离了。

<img src="Kafka%E7%AC%94%E8%AE%B0.assets/image-20210404001518003.png" alt="image-20210404001518003" style="zoom:80%;" />

<img src="Kafka%E7%AC%94%E8%AE%B0.assets/image-20210404001542039.png" alt="image-20210404001542039" style="zoom:80%;" />

## 三、Kafka架构深入

### 3.1 Kafka的工作流程及文件存储机制

### 3.2 Kafka生产者

#### 3.2.1 分区策略

#### 3.2.2 数据可靠性保证

### 3.3 Kafka消费者



