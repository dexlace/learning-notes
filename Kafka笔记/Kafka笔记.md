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

> **<font color=red>Partition</font>**：为了实现扩展性，一个非常大的 topic 可以分布到多个 broker（即服务器）上，一个topic可以分为多个partition，每个 partition 是一个有序的队列；我总是把分区与副本给误解，副本那么容易理解我还和分区搞混，其实分区就是topic的一部分

> **<font color=red>Replica</font>**：副本，为保证集群中的某个节点发生故障时，该节点上的 partition 数据不丢失，且 kafka 仍然能够继续工作，kafka 提供了副本机制，一个 topic 的每个分区都有若干个副本，

> leader：每个分区多个副本的“主”，==生产者发送数据的对象，以及消费者消费数据的对象都是 leader==，只有一个leader。 

> follower：每个分区多个副本中的“从”，实时从 leader 中同步数据，保持和 leader 数据的同步。leader 发生故障时，某个 follower 
>
> 会成为新的 leader。 

### 1.3 Zookeeper与Kafka的关系

#### 1.3.1 broker注册

Broker是==分布式部署并且相互之间相互独立==，但是需要有一个==注册系统==能够将整个集群中的Broker管理起来，此时就使用到了Zookeeper。

> ==<font color=red>注册节点:/brokers/ids</font>==
>
> 每个Broker在启动时，都会在Zookeeper上进行注册，在==/brokers/ids==下创建属于自己的节点，如下共==创建了3个节点==，在之后
>
> 的配置文件==server.properties==中可以找到对应的broker id信息

<img src="Kafka%E7%AC%94%E8%AE%B0.assets/image-20210403161511574.png" alt="image-20210403161511574" style="zoom:80%;" />

> Kafka使用了==全局唯一的数字==来指代每个Broker服务器，不同的Broker必须使用不同的Broker ID进行注册，创建完节点后，每个Broker就===会将自己的IP地址和端口信息记录到该节点中==去。其中，Broker创建的节点类型是==临时节点==，一旦Broker宕机，则对应的临时节点也会被自动删除。
>
> 如下图所示为3个broker id节点的信息

<img src="Kafka%E7%AC%94%E8%AE%B0.assets/image-20210403163356699.png" alt="image-20210403163356699" style="zoom:67%;" />

#### 1.3.2  Topic注册

在Kafka中，同一个Topic的消息会被分成==多个分区==并将其分布在==多个Broker==上，这些分区信息及与Broker的对应关系也都是Zookeeper在维护，由专门的节点来记录，如：

> ==<font color=red>注册的节点：/brokers/topics</font>==
>
> Kafka中每个Topic都会以==/brokers/topics/[topic]==的形式被记录，如/brokers/topics/login和/brokers/topics/search

![image-20210403165939794](Kafka%E7%AC%94%E8%AE%B0.assets/image-20210403165939794.png)

#### 1.3.3 生产者负载均衡

由于同一个Topic消息会被分区并将其分布在多个Broker上，因此，生产者需要将消息合理地发送到这些分布式的Broker上，那么如何实现生产者的负载均衡，Kafka支持传统的四层负载均衡，也支持Zookeeper方式实现负载均衡。

- ==四层负载均衡==，==根据生产者的IP地址和端口来为其确定一个相关联的Broker==。通常，一个生产者只会对应单个Broker，然后该生产者产生的消息都发往该Broker。这种方式逻辑简单，每个生产者不需要同其他系统建立额外的TCP连接，只需要和Broker维护单个TCP连接即可。但是，其无法做到真正的负载均衡，因为实际系统中的每个生产者产生的消息量及每个Broker的消息存储量都是不一样的，如果有些生产者产生的消息远多于其他生产者的话，那么会导致不同的Broker接收到的消息总数差异巨大，同时，生产者也无法实时感知到Broker的新增和删除。

- 使用==Zookeeper进行负载均衡==，由于每个Broker启动时，都会完成Broker注册过程，生产者会通过该节点的变化来动态地感知到Broker服务器列表的变更，这样就可以实现动态的负载均衡机制。

#### 1.3.4 消费者负载均衡

与生产者类似，Kafka中的消费者同样需要进行负载均衡来实现多个消费者合理地从对应的Broker服务器上接收消息，==每个消费者组包====含若干消费者==，==每条消息都只会发送给分组中的一个消费者==，不同的消费者分组消费自己特定的Topic下面的消息，互不干扰。

#### 1.3.5 分区与消费者的关系
==消费组 (Consumer Group)：==
consumer group 下有多个 Consumer（消费者）。
对于每个消费者组 (Consumer Group)，Kafka都会为其分配一个==全局唯一的Group ID==，Group 内部的所有消费者共享该 ID。订阅的topic下的每个分区只能分配给某个 group 下的一个consumer(当然该分区还可以被分配给其他group)。同时，Kafka为每个消费者分配一个==Consumer ID==，通常采用=="Hostname:UUID"==形式表示。

在Kafka中，规定了==每个消息分区 只能被同组的**一个消费者**进行消费==，因此，需要在 Zookeeper 上记录 消息分区 与 Consumer 之间的关系，每个消费者一旦确定了对一个消息分区的消费权力，需要将其Consumer ID 写入到 Zookeeper 对应消息分区的临时节点上，例如：

`/consumers/[group_id]/owners/[topic]/[broker_id-partition_id]`

其中，[broker_id-partition_id]就是一个 消息分区 的标识，==节点内容==就是该 消息分区上消费者的==Consumer ID==。
#### 1.3.6 消息消费进度Offset 记录
在消费者对指定消息分区进行消息消费的过程中，需要定时地将分区消息的消费进度Offset记录到Zookeeper上，以便在该消费者进行重启或者其他消费者重新接管该消息分区的消息消费后，能够从之前的进度开始继续进行消息消费。Offset在Zookeeper中由一个专门节点进行记录，其节点路径为:

`/consumers/[group_id]/offsets/[topic]/[broker_id-partition_id]`

==节点内容就是Offset的值==。

#### 1.3.7 消费者注册

消费者服务器在初始化启动时加入消费者分组的步骤如下

**注册到消费者分组**。每个消费者服务器启动时，都会到Zookeeper的指定节点下==创建一个属于自己的消费者节点==，例如`/consumers/[group_id]/ids/[consumer_id]`，完成节点创建后，消费者就会==将自己订阅的Topic信息写入该临时节点==。

**对消费者分组中的消费者的变化注册监听**。每个 消费者 都需要关注所属 消费者分组 中其他消费者服务器的变化情况，即对`/consumers/[group_id]/ids`节点注册子节点变化的Watcher监听，==一旦发现消费者新增或减少，就触发消费者的负载均衡==。

**对Broker服务器变化注册监听。**消费者需要对`/broker/ids/[0-N]`中的节点进行监听，如果发现Broker服务器列表发生变化，那么就根据具体情况来决定是否需要进行消费者负载均衡。

**进行消费者负载均衡。**为了让同一个Topic下不同分区的消息尽量均衡地被多个 消费者 消费而进行 消费者 与 消息 分区分配的过程，通常，对于一个消费者分组，如果组内的消费者服务器发生变更或Broker服务器发生变更，会发出消费者负载均衡。

<img src="Kafka%E7%AC%94%E8%AE%B0.assets/image-20210526224319352.png" alt="image-20210526224319352" style="zoom:80%;" />

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

```bash
# 用消费者控制平台去 hadoop102这台zookeeper里 去消费 主题为first的主题
bin/kafka-console-consumer.sh --zookeeper hadoop102:2181 --from-beginning --topic first


#查看所有正在连接的Consumer信息
bin/kafka-consumer-groups.sh --zookeeper localhost:2181 --list

bin/kafka-consumer-groups.sh --new-consumer --bootstrap-server localhost:9092 --list

#查看单个Consumer信息
bin/kafka-consumer-groups.sh --zookeeper localhost:2181 --describe --group BrowseConsumer

bin/kafka-consumer-groups.sh --new-consumer --bootstrap-server localhost:9092 --describe --group BrowseConsumer

#重头开始消费某个Topic
bin/kafka-console-consumer.sh --zookeeper localhost:2181 --from-beginning --topic xxx

bin/kafka-console-consumer.sh --new-consumer --bootstrap-server localhost:9092 --from-beginning --topic xxx

```



### 2.3 数据和日志分离

注意上面的logs文件夹其实==可以在kafka启动后在kafka目录下自动生成==，我们指定==log.dirs=/home/doglast/software/kafka/logs==其实会把数据混进日志里，只能是说这个配置项取得名字实在不好，==log.dirs其实是数据目录==，为了区分数据和众多log文件，将==log.dirs另外指定==比如指定为==kafka目录下的data文件夹（需要自己新建）==

<img src="Kafka%E7%AC%94%E8%AE%B0.assets/image-20210404000901210.png" alt="image-20210404000901210" style="zoom:80%;" />

注意为了演示以上效果，要把zookeeper中有关kafka的信息清除，即==删除zk的data下除myid文件的数据==；此外还要==删除kafka/logs的所有文件==。最后重启kafka，创建一个主题，会发现，数据和日志分离了。

<img src="Kafka%E7%AC%94%E8%AE%B0.assets/image-20210404001518003.png" alt="image-20210404001518003" style="zoom:80%;" />

<img src="Kafka%E7%AC%94%E8%AE%B0.assets/image-20210404001542039.png" alt="image-20210404001542039" style="zoom:80%;" />

## 三、Kafka架构深入

### 3.1 Kafka的工作流程及文件存储机制

#### 3.1.1 工作流程

<img src="Kafka%E7%AC%94%E8%AE%B0.assets/image-20210404225208657.png" alt="image-20210404225208657" style="zoom: 67%;" />

Kafka 中消息是以 **topic** 进行分类的，生产者生产消息，消费者消费消息，都是面向 topic的。

==topic 是逻辑上的概念，而 partition 是物理上的概念==，每个 partition 对应于一个 log 文件，该 log 文件中存储的就是 producer 生产的数据。Producer 生产的数据会被==不断追加到该log 文件末端，且每条数据都有自己的 offset==。消费者组中的每个消费者，都会实时记录自己消费到了哪个 offset，以便出错恢复时，从上次的位置继续消费。

#### 3.1.2 文件存储机制

<img src="Kafka%E7%AC%94%E8%AE%B0.assets/image-20210404225641628.png" alt="image-20210404225641628" style="zoom:67%;" />

由于生产者生产的消息会不断追加到 log 文件末尾，为防止 log 文件过大导致数据定位效率低下，Kafka 采取了==**分片**==和==**索引**==机制，将每个 partition 分为多个 segment。每个 segment对应两个文件——==“.index”文件==和==“.log”文件==。这些文件位于一个文件夹下，该文件夹的命名规则为：==topic 名称+分区序号==。例如，first 这个 topic 有三个分区，则其对应的文件夹为 ==first-0,first-1,first-2==index 和 log 文件以==<font color=red>当前 segment 的第一条消息的 offset 命名</font>==。下图为 index 文件和 log文件的结构示意图。

<img src="Kafka%E7%AC%94%E8%AE%B0.assets/image-20210404233014832.png" alt="image-20210404233014832" style="zoom: 67%;" />

“.index”文件存储大量的索引信息，“.log”文件存储大量的数据，索引文件中的元数据指向对应数据文件中==message 的物理偏移地址==。

### 3.2 Kafka生产者

#### 3.2.1 分区策略

##### 1）分区原因

==**方便在集群中扩展**==，每个 Partition 可以通过调整以适应它所在的机器，而一个 topic又可以有多个 Partition 组成，因此整个集群就可以

适应任意大小的数据了；

==**可以提高并发**==，因为可以以 Partition 为单位读写了。

##### 2）分区原则

我们需要将 producer 发送的数据封装成一个 **<font color=red>ProducerRecord</font>** 对象。

<img src="Kafka%E7%AC%94%E8%AE%B0.assets/image-20210404234354442.png" alt="image-20210404234354442" style="zoom:80%;" />

- ==指明 partition== 的情况下，直接将指明的值直接作为 partiton 值；

- ==没有指明 partition 值但有 key 的情况下==，将 key 的 hash 值与 topic 的 partition 数进行==取余得到 partition 值==；

- 既没有 partition 值又没有 key 值的情况下，第一次调用时==随机生成一个整数==（后面每次调用在这个整数上自增），将==这个值与 topic 可用的 partition 总数取余==得到 partition 值，也就是常说的 round-robin 算法。

#### 3.2.2 数据可靠性保证

> topic收到producer发送的数据后，需要向producer发送ack。但==前提是follower与leader同步完成==，leader再发送ack，以保证在
>
> leader挂掉之后，能在follower中选举出新的leader

<img src="Kafka%E7%AC%94%E8%AE%B0.assets/image-20210405000532533.png" alt="image-20210405000532533" style="zoom:67%;" />

##### ==1) 副本同步策略==

| 方案                        | 优点                                                         | 缺点                                                      |
| --------------------------- | ------------------------------------------------------------ | --------------------------------------------------------- |
| 半数以上完成同步，就发送ack | 延迟低                                                       | 选举新leader时，容忍==n台节点的故障==，==需要2n+1个副本== |
| 全部完成同步，才发送ack     | 选举新的 leader 时，==容忍 n 台节点的故障，需要 n+1 个副本== | 延迟高                                                    |

Kafka 选择了第二种方案，原因如下：

- 同样为了容忍 n 台节点的故障，第一种方案需要 2n+1 个副本，而第二种方案只需要 n+1个副本，而 Kafka 的每个分区都有大量的数据，第一种方案会造成大量数据的冗余。

- 虽然第二种方案的网络延迟会比较高，但网络延迟对 Kafka 的影响较小。

##### 2) ==ISR==（in-sync replica set）

> 如果leader收到数据，所有的follower都开始同步数据，但是==有一个follower迟迟不能与leader同步，那么leader就要一直等下去，==
>
> 直到它完成同步，才能发送ack。

Leader 维护了一个<font color=red>动态的 in-sync replica set (ISR)，意为和 leader 保持同步的 follower 集合</font>。当 ISR 中的 follower 完成数据的同步之后，leader 就会给 follower 发送 ack。如果 <font color=red>follower长时间 未 向 leader 同 步 数 据 ， 则 该 follower 将 被 踢 出 ISR ， 该 时 间 阈 值 </font><font color=red>由**replica.lag.time.max.ms** 参数设定</font>。Leader 发生故障之后，就会从 ISR 中选举新的 leader。 

##### 3) ==ACK应答机制==

> 对于某些不太重要的数据，对数据的可靠性要求不是很高，能够容忍数据的少量丢失，所以==没必要等 ISR 中的 follower 全部接收成功。==
>
> 所以 Kafka 为用户提供了三种可靠性级别，用户根据对可靠性和延迟的要求进行权衡，选择以下的配置

acks:

- 0: ==producer不等待broker的ack==，这一操作提供一个最低的延迟，==broker一接收到还没有写入磁盘就已经返回==，当broker故障时有可能丢失数据

- 1：==producer 等待 broker 的 ack==，partition 的 ==<font color=red>leader 落盘成功后返回 ack</font>==，如果在 follower同步成功之前 leader 故障，那么将会丢失数据

- -1（all）：==producer 等待 broker 的 ack==，partition 的 ==<font color=red>leader 和 follower 全部落盘成功后才返回 ack</font>==。但是如果在 follower 同步完成后，broker 发送 ack 之前，leader 发生故障，那么会造成**<font color=red>数据重复</font>**

##### 4) 故障处理细节

> ==LEO(Log End Offset)==: 每个副本的<font color=red>最后一个offeset</font>
>
> ==HW==:所有的副本中<font color=red>最小的LEO</font>

<img src="Kafka%E7%AC%94%E8%AE%B0.assets/image-20210405165731460.png" alt="image-20210405165731460" style="zoom:80%;" />

- <font color=red>follower故障</font>

follower 发生故障后会被临时踢出 ISR，待该 follower 恢复后，follower 会读取本地磁盘记录的上次的 HW，并将==log 文件高于 HW 的部====分截取掉==，<font color=red>从 HW 开始向 leader 进行同步</font>。等该 <font color=red>**follower** **的** **LEO** **大于等于该** **Partition** **的** **HW**</font>，即 follower 追上 leader 之后，就可以新加入 ISR 了。

- <font color=red>leader故障</font>

leader 发生故障之后，会从 ISR 中选出一个新的 leader，之后，为保证多个副本之间的数据一致性，其余的 follower 会先将==各自的====log 文件高于 HW 的部分截掉==，然后从新的 leader同步数据。

注意：这<font color=red>只能保证副本之间的数据一致性</font>，并不能保证数据不丢失或者不重复

####  3.2.3 Exactly Once语义

将服务器的 ACK 级别设置为==-1==，可以保证 ==<font color=red>Producer 到 Server 之间不会丢失数据</font>==，即 ==At Least Once== 语义。

相对的，将服务器 ACK 级别设置为 ==0==，可以保证生产者每条消息==<font color=red>只会被发送一次</font>==，即 ==At Most Once 语义==。

At Least Once 可以保证数据不丢失，但是==不能保证数据不重复==；

相对的，At Least Once可以保证数据不重复，但是==不能保证数据不丢失==。

但是，对于一些非常重要的信息，比如说交易数据，下游数据消费者要求数据==既不重复也不丢失==，即 ==Exactly Once 语义==。在 0.11 版本以前的 Kafka，对此是无能为力的，只能保证数据不丢失，再在下游消费者对数据做全局去重。对于多个下游应用的情况，每个都需要单独做全局去重，这就对性能造成了很大影响。

0.11 版本的 Kafka，引入了一项重大特性：==幂等性==。所谓的幂等性就是==指 Producer 不论向 Server 发送多少次重复数据，<font color=red>Server 端都</font>====<font color=red>只会持久化一条</font>==。幂等性结合 At Least Once 语义，就构成了 Kafka 的 Exactly Once 语义。即：

​                                                            <font color=red> At Least Once + 幂等性=Exactly Once</font>

要启用幂等性，只需要将 Producer 的参数中 ==<font color=red>enable.idompotence 设置为 true 即可</font>==。Kafka的幂等性实现其实就是==将原来下游需要做====的去重放在了数据上游==。开启幂等性的 Producer 在初始化的时候会被分配一个PID，发往同一 Partition 的消息会附带 Sequence Number。而Broker 端会对==<PID, Partition, SeqNumber>做缓存==，当具有相同主键的消息提交时，Broker 只会持久化一条。但是 PID 重启就会变化，同时不同的 Partition 也具有不同主键，所以幂等性无法保证跨分区跨会话的 Exactly Once。

### 3.3 Kafka消费者

#### 3.3.1 消费方式

<font color=red>consumer 采用 pull（拉）模式从 broker 中读取数据。</font>

push（推）模式很难适应消费速率不同的消费者，因为消息发送速率是由 broker 决定的。它的目标是尽可能以最快速度传递消息，但是这样很容易造成 consumer 来不及处理消息，典型的表现就是拒绝服务以及网络拥塞。

而<font color=red> pull 模式则可以根据 </font><font color=red>consumer 的消费能力以适当的速率消费消息。</font><font color=red>pull 模式不足之处是，如果 kafka 没有数据，消费者可能会陷入循环中，一直返回空数据</font>。针对这一点，Kafka 的消费者在消费数据时会传入==一个时长参数 timeout==，如果当前没有数据可供消费，consumer 会等待一段时间之后再返回，这段时长即为 timeout

#### 3.3.2 分区分配策略

一个 consumer group 中有多个 consumer，一个 topic 有多个 partition，所以必然会涉及到 partition 的分配问题，即==确定哪个 partition 由哪个 consumer 来消费==。

 Kafka 有两种分配策略，一是 ==RoundRobin==，一是==Range==。 

RoundRobin：

把所有的partition和consumer列出来，==然后轮询consumer和partition==，尽可能的让==把partition均匀的分配给consumer==

Range：

假如有10个分区，3个消费者，把分区按照序号排列0，1，2，3，4，5，6，7，8，9；消费者为C1,C2,C3，那么用分区数除以消费者数来决定每个Consumer消费几个Partition，==除不尽的前面几个消费者将会多消费一个==
最后分配结果如下

C1：0，1，2，3
C2：4，5，6
C3：7，8，9

如果有11个分区将会是：

C1：0，1，2，3
C2：4，5，6，7
C3：8，9，10

## 四、AdminClient API

![image-20210526225147780](Kafka%E7%AC%94%E8%AE%B0.assets/image-20210526225147780.png)

```JAVA
package com.dexlace.kafka.admin;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.internals.Topic;

import java.sql.SQLOutput;
import java.util.*;

/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/5/26
 */
public class AdminSample {


    public final static String TOPIC_NAME="myfirst";

    public static void main(String[] args) throws Exception {
//        System.out.println("adminClient:"+AdminSample.adminClient());
//        createTopic();
        topicList();
//        delTopics();
        describeTopics();
        describeConfig();
    }



    public static void createTopic(){
        AdminClient adminClient=adminClient();
        // 主题名，分区数，副本集数
        NewTopic newTopic=new NewTopic(TOPIC_NAME,3,(short)3);
        CreateTopicsResult topics = adminClient.createTopics(Arrays.asList(newTopic));
        System.out.println("topics: " + topics);
    }


    public static AdminClient adminClient() {
        Properties properties = new Properties();
        properties.setProperty(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.205.100:9092,192.168.205.101:9092,192.168.205.102:9092");
        AdminClient adminClient = AdminClient.create(properties);

        return adminClient;
    }

    /**
     * 获取topic列表
     */
    public static void topicList() throws Exception{
        AdminClient adminClient = adminClient();
        // 是否查看internal选项
        ListTopicsOptions options = new ListTopicsOptions();
        options.listInternal(true);
//        ListTopicsResult listTopicsResult = adminClient.listTopics();
        ListTopicsResult listTopicsResult = adminClient.listTopics(options);
        Set<String> names = listTopicsResult.names().get();

        names.stream().forEach(System.out::println);

    }

    /*
     删除Topic
    */
    public static void delTopics() throws Exception {
        AdminClient adminClient = adminClient();
        DeleteTopicsResult deleteTopicsResult = adminClient.deleteTopics(Arrays.asList(TOPIC_NAME));
        deleteTopicsResult.all().get();
    }



    /*
       描述Topic

       name ：myfirst ,
       desc: (name=myfirst,
       internal=false,
       partitions=
       (partition=0, leader=kafka101:9092 (id: 1 rack: null),replicas=kafka101:9092 (id: 1 rack: null), kafka102:9092 (id: 2 rack: null), isr=kafka101:9092 (id: 1 rack: null), kafka102:9092 (id: 2 rack: null)),
       (partition=1, leader=kafka102:9092 (id: 2 rack: null), replicas=kafka102:9092 (id: 2 rack: null), kafka100:9092 (id: 0 rack: null), isr=kafka102:9092 (id: 2 rack: null), kafka100:9092 (id: 0 rack: null)),
       (partition=2, leader=kafka100:9092 (id: 0 rack: null), replicas=kafka100:9092 (id: 0 rack: null), kafka102:9092 (id: 2 rack: null), isr=kafka100:9092 (id: 0 rack: null), kafka102:9092 (id: 2 rack: null)))


    */
    public static void describeTopics() throws Exception {
        AdminClient adminClient = adminClient();
        DescribeTopicsResult describeTopicsResult = adminClient.describeTopics(Arrays.asList(TOPIC_NAME));
        Map<String, TopicDescription> stringTopicDescriptionMap = describeTopicsResult.all().get();
        Set<Map.Entry<String, TopicDescription>> entries = stringTopicDescriptionMap.entrySet();
        entries.stream().forEach((entry)->{
            System.out.println("name ："+entry.getKey()+" , desc: "+ entry.getValue());
        });
    }


    /**
     * 配置信息的描述
     * @throws Exception
     */
    public static void describeConfig() throws Exception{
        AdminClient adminClient = adminClient();
        // TODO 这里做一个预留，集群时会讲到
//        ConfigResource configResource = new ConfigResource(ConfigResource.Type.BROKER, TOPIC_NAME);

        ConfigResource configResource = new ConfigResource(ConfigResource.Type.TOPIC, TOPIC_NAME);
        DescribeConfigsResult describeConfigsResult = adminClient.describeConfigs(Arrays.asList(configResource));
        Map<ConfigResource, Config> configResourceConfigMap = describeConfigsResult.all().get();
        configResourceConfigMap.entrySet().stream().forEach((entry)->{
            System.out.println("configResource : "+entry.getKey()+" , Config : "+entry.getValue());
        });
    }


}

```

## 五、Producer API

- 生产者要发送消息的==属性封装到Properties中==，将Properties传到KafkaProducer构造器里，创建一个生产者
- 发送的消息封装成ProducerRecord对象，包含==topic、分区、key、value==。分区和key可不指定，由kafka自行确定目标分区
- KafkaProducer调用KafkaProducer的send()方法发送到zookeeper
- ==消费者将要订阅的主题封装在Properties对象中==，传入KafkaConsumer构造器中，创建一个消费者
- KafkaConsumer调用==poll()从zookeeper拉取消费消息==
- ==发送时将数据序列化，消费时将数据反序列化==

producer使用一个线程（用户主线程，也就是用户启动producer的线程）将待发送的消息封装成一ProducerRecord实例，然后将其序列化后发送给partition，再由partition确定目标分区后一同发送到位于producer程序中的一块内存缓冲区中。而producer的另一个线程（I/O发送线程，也称Sender线程）则负责实时地将缓冲区中提取出准备就绪的消息封装进一个批次（batch），统一发送给对应的broker。

Kafka 的 Producer 发送消息采用的是异步发送的方式。在消息发送的过程中，涉及到了两个线程——main 线程和 Sender 线程，以及一个线程共享变量——RecordAccumulator。

==main 线程将消息发送给 RecordAccumulator==， ==Sender 线程==不断从 RecordAccumulator 中拉取消息发送到 Kafka 

broker相关参数：

- ==batch.size： 只有数据积累到 batch.size 之后， sender 才会发送数据。==
- ==linger.ms： 如果数据迟迟未达到 batch.size， sender 等待 linger.time 之后就会发送数据。==

### 5.1 ==异步发送，不带回调函数==

```java
 /*        一、Producer异步发送演示
   KafkaProducer：需要创建一个生产者对象，用来发送数据
   ProducerConfig：获取所需的一系列配置参数
   ProducerRecord：每条数据都要封装成一个 ProducerRecord 对象
    */
    public static void producerSend() {
        Properties properties = new Properties();
        /**
         * kafka 集群，broker-list配置：bootstrap.servers
         如果kafka集群中机器数很多，那么只需指定部分broker即可，
         不需要列出所有的机器。
         因为不管指定几台机器，
         producer都会通过该参数找到并发现集群中所有broker。
         为该参数指定多态机器只是为了故障转移使用。
         这样即使某一台broker挂掉了，producer重启后依然可以通过该参数指定的其他broker连入kafka集群
         */
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.205.100:9092,192.168.205.101:9092,192.168.205.102:9092");
        /**
         * 确认机制
         */
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        /**
         * 重试次数
         */
        properties.put(ProducerConfig.RETRIES_CONFIG, "0");
        /**
         * 批次大小
         */
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, "16384");
        /**
         * 等待时间：1ms
         */
        properties.put(ProducerConfig.LINGER_MS_CONFIG, "1");
        /**
         * 缓冲区大小
         */
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, "33554432");

        /**
         * 键的序列化
         */
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        /**
         * value的序列化
         */
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        // Producer的主对象
        Producer<String, String> producer = new KafkaProducer<>(properties);

        // 消息对象 - ProducerRecoder
        for (int i = 0; i < 10; i++) {
            ProducerRecord<String, String> record =
                    new ProducerRecord<>(TOPIC_NAME, "key-" + i, "value-" + i);

            producer.send(record);
        }

        // 所有的通道打开都需要关闭
        producer.close();
    }

```

### 5.2 ==异步发送，带回调函数==

```java
 /*
        二、Producer异步发送带回调函数
   */
    public static void producerSendWithCallback() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.205.100:9092,192.168.205.101:9092,192.168.205.102:9092");
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        properties.put(ProducerConfig.RETRIES_CONFIG, "0");
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, "16384");
        properties.put(ProducerConfig.LINGER_MS_CONFIG, "1");
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, "33554432");

        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        // Producer的主对象
        Producer<String, String> producer = new KafkaProducer<>(properties);

        // 消息对象 - ProducerRecoder
        for (int i = 0; i < 10; i++) {
            ProducerRecord<String, String> record =
                    new ProducerRecord<>(TOPIC_NAME, "key-" + i, "value-" + i);

            producer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    if(null==e){
                        System.out.println(
                                "partition : " + recordMetadata.partition() + " , offset : " + recordMetadata.offset());
                    }
                   
                }
            });
        }

        // 所有的通道打开都需要关闭
        producer.close();
    }
```

### 5.3 同步发送（伪异步发送）

```java
 /*  三、
           所谓的同步发送：其实没有同步发送
           Producer异步阻塞发送
        */
    public static void producerSyncSend() throws ExecutionException, InterruptedException {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.205.100:9092,192.168.205.101:9092,192.168.205.102:9092");
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        properties.put(ProducerConfig.RETRIES_CONFIG, "0");
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, "16384");
        properties.put(ProducerConfig.LINGER_MS_CONFIG, "1");
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, "33554432");

        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        // Producer的主对象
        Producer<String, String> producer = new KafkaProducer<>(properties);

        // 消息对象 - ProducerRecoder
        for (int i = 0; i < 10; i++) {
            String key = "key-" + i;
            ProducerRecord<String, String> record =
                    new ProducerRecord<>(TOPIC_NAME, key, "value-" + i);

            //由于 send 方法返回的是一个 Future 对象，根据 Futrue 对象的特点，我们也可以实现同步发送的效果，只需在调用 Future 对象的 get 方发即可。
            // get方法会一直等待下去直至broker将发送结果返回给producer程序。
            // 返回的时候要不返回发送结果，要么抛出异常由producer自行处理。
            // 如果成功，get将返回对应的RecordMetadata实例（包含了已发送消息的所有元数据消息），包含了topic、分区、位移
            Future<RecordMetadata> send = producer.send(record);
            RecordMetadata recordMetadata = send.get();
            System.out.println(key + "partition : " + recordMetadata.partition() + " , offset : " + recordMetadata.offset());
        }

        // 所有的通道打开都需要关闭
        producer.close();
    }
```

### 5.4 异步发送，带回调函数和分区负载均衡

```java
 /*
       四、
        Producer异步发送带回调函数和Partition负载均衡
     */
    public static void producerSendWithCallbackAndPartition() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.205.100:9092,192.168.205.101:9092,192.168.205.102:9092");
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        properties.put(ProducerConfig.RETRIES_CONFIG, "0");
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, "16384");
        properties.put(ProducerConfig.LINGER_MS_CONFIG, "1");
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, "33554432");

        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, "com.dexlace.kafka.producer.SamplePartition");

        // Producer的主对象
        Producer<String, String> producer = new KafkaProducer<>(properties);

        // 消息对象 - ProducerRecoder
        for (int i = 0; i < 10; i++) {
            ProducerRecord<String, String> record =
                    new ProducerRecord<>(TOPIC_NAME, "key-" + i, "value-" + i);

            producer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    System.out.println(
                            "partition : " + recordMetadata.partition() + " , offset : " + recordMetadata.offset());
                }
            });
        }

        // 所有的通道打开都需要关闭
        producer.close();
    }
```

```java
package com.dexlace.kafka.producer.partition;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

import java.util.Map;

public class SamplePartition implements Partitioner {
    @Override
    // 返回要放入的paritition
    public int partition(String topic,  // 主题
                         Object key,  // 给定的key
                         byte[] keyBytes,  // key序列化后的值
                         Object value,  // 要放入的值
                         byte[] valueBytes, // 序列化后的值
                         Cluster cluster) // 当前集群
    {

        /*
            key-1
            key-2
            key-3
         */
        String keyStr = key + "";
        String keyInt = keyStr.substring(4);
        System.out.println("keyStr : " + keyStr + "keyInt : " + keyInt);

        int i = Integer.parseInt(keyInt);

        return i % 2;
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}

```

## 六、Consumer API

==Consumer 消费数据时的可靠性是很容易保证的，因为数据在 Kafka 中是持久化的，故不用担心数据丢失问题。==

由于 consumer 在消费过程中可能会出现断电宕机等故障，consumer 恢复后，需要从故障前的位置的继续消费，所以 consumer 需要实时记录自己消费到了哪个 offset，以便故障恢复后继续消费。

所以 ==offset 的维护是 Consumer 消费数据是必须考虑的问题。==

### 6.1 自动提交offset

```JAVA
 /*
           工作里这种用法，有，但是不推荐
        */
    private static void helloworld(){
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "192.168.205.100:9092,192.168.205.101:9092,192.168.205.102:9092");
        props.setProperty("group.id", "test");
        props.setProperty("enable.auto.commit", "true");
        props.setProperty("auto.commit.interval.ms", "1000");
        props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String,String> consumer = new KafkaConsumer(props);
        // 消费订阅哪一个Topic或者几个Topic
        consumer.subscribe(Arrays.asList(TOPIC_NAME));
        while (true) {
            //需要在其他线程中调用consumer.wakeup()触发consumer的关闭。
            //虽然consumer是线程不安全的，但其他用户调用这个函数是安全的
            // 读取数据，读取超时时间为100ms 
            //但是拉取到的数据可能处理失败，所以这里容易出问题，
            // 重新启动时因为有offset我们就不能重新处理数据了，所以我们后面改用手动提交
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records)
                System.out.printf("patition = %d , offset = %d, key = %s, value = %s%n",
                        record.partition(),record.offset(), record.key(), record.value());
        }
    }

```

传递给poll()方法的超时设定参数用于控制consumer等待消息的==最大阻塞时间==。由于某些原因，==broker端有时候无法立即满足consumer端的获取请求==（比如consumer要求至少一次获取1MB的数据，但broker端无法立即全部给出），那么此时consumer端就会阻塞以等待数据不断累积被满足consumer需求。如果用户不想让consumer一直处于阻塞状态，则==需要给定一个超时时间==。因此poll方法返回满足一下任一条件时即可返回

- ==获取了足够多的可用数据==

- ==等待时间超过指定的超时设置==

consumer是==单线程的设计理念==，因此consumer运行在它专属的线程中。新版本的java consumer不是线程安全的，如果没有显示地同步锁保护机制，kafka会排除KafkaConsumer is not safe for multi-threaded access异常，这代表同一个kafkaconsumer实例用在了多个线程中，这是不允许的。

上面的是==while(isRunning)来判断是否退出消费循环结束consumer应用==。具体的做法是让isRunning边控几位volatile型

### 6.2 手动提交offset

手动提交 offset 的方法有两种：分别是 ==commitSync（同步提交）==和 ==commitAsync（异步提交）==。两者的相同点是，都会将**本次** **poll** **的一批数据最高的偏移量提交**；不同点是，==commitSync 阻塞当前线程，一直到提交成功==，并且会自动失败重试（由不可控因素导致，也会出现提交失败）；而 ==commitAsync 则没有失败重试机制，故有可能提交败。==

```java
/*
       手动提交offset
       因为消费到数据后一般有业务逻辑，如果耗时消费了，而已经批量消费了，非常不合理，所以手动提交
    */
    private static void commitedOffset() {
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "kafka100:9092,kafka101:9092,kafka102:9092");
        props.setProperty("group.id", "test");
        props.setProperty("enable.auto.commit", "false");//不自动提交

        props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer(props);
        // 消费订阅哪一个Topic或者几个Topic
        consumer.subscribe(Arrays.asList(TOPIC_NAME));
        while (true) {
            System.out.println("怎么会消费不到");
            ConsumerRecords<String, String> records = consumer.poll(10000);
            for (ConsumerRecord<String, String> record : records) {
                // 想把数据保存到数据库，成功就成功，不成功...
                // TODO record 2 db
                System.out.printf("patition = %d , offset = %d, key = %s, value = %s%n",
                        record.partition(), record.offset(), record.key(), record.value());
                // 如果失败，则回滚， 不要提交offset
            }

            // 如果成功，手动通知offset提交 也是批量 提交所有的
            //同步提交，当前线程会阻塞直到 offset 提交成功
            consumer.commitAsync();
            
            //异步提交
  //          consumer.commitAsync(new OffsetCommitCallback() {
  //                  @Override
  //                    public void onComplete(Map<TopicPartition, 
  //                                     OffsetAndMetadata> offsets, Exception exception) {
  //                         if (exception != null) {
  //                                  System.err.println("Commit failed for" + offsets);
  //                         }
  //                    }
  //                  });
            
        }
    }

```

> ==手动提交offset，并且手动控制partition一==

```java
 /*
        手动提交offset,并且手动控制partition
     */
    private static void commitedOffsetWithPartition() {
        Properties props = new Properties();
        props.setProperty("bootstrap.servers",  "kafka100:9092,kafka101:9092,kafka102:9092");
        props.setProperty("group.id", "test");
        props.setProperty("enable.auto.commit", "false");


        props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer(props);
        // 消费订阅哪一个Topic或者几个Topic
        consumer.subscribe(Arrays.asList(TOPIC_NAME));
        while (true) {

            System.out.println("=============partition - "+ " start================");


            ConsumerRecords<String, String> records = consumer.poll(10000);
            // 每个partition单独处理
            for(TopicPartition partition : records.partitions()){
                System.out.println("=============partition - "+ partition +" start================");
                // 获取每个partition的数据
                List<ConsumerRecord<String, String>> pRecord = records.records(partition);
                for (ConsumerRecord<String, String> record : pRecord) {
                    System.out.printf("patition = %d , offset = %d, key = %s, value = %s%n",
                            record.partition(), record.offset(), record.key(), record.value());

                }
                // 得到分区的最后一次获取的offset
                long lastOffset = pRecord.get(pRecord.size() -1).offset();
                // 单个partition中的offset，并且进行提交
                Map<TopicPartition, OffsetAndMetadata> offset = new HashMap<>();
                // 注意这里提交的时下次消费的起始位置，所以+1
                offset.put(partition,new OffsetAndMetadata(lastOffset+1));
                // 提交offset 单独提交
                consumer.commitSync(offset);
                System.out.println("=============partition - "+ partition +" end================");
            }

            System.out.println("=============partition - "+ " stop================");
        }
    }
```

> 手动提交offset,并且手动控制partition二

```java
 /*
       手动提交offset,并且手动控制partition,更高级
    */
    private static void commitedOffsetWithPartition2() {
        Properties props = new Properties();
        props.setProperty("bootstrap.servers",  "kafka100:9092,kafka101:9092,kafka102:9092");
        props.setProperty("group.id", "test");
        props.setProperty("enable.auto.commit", "false");
        props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer(props);

        // jiangzh-topic - 0,1两个partition
        TopicPartition p0 = new TopicPartition(TOPIC_NAME, 0);
        TopicPartition p1 = new TopicPartition(TOPIC_NAME, 1);

        // 消费订阅哪一个Topic或者几个Topic
//        consumer.subscribe(Arrays.asList(TOPIC_NAME));

        // 消费订阅某个Topic的某个分区
        consumer.assign(Arrays.asList(p0,p1));

        while (true) {
            System.out.println("judy is here...");
            ConsumerRecords<String, String> records = consumer.poll(100);
            // 每个partition单独处理
            for(TopicPartition partition : records.partitions()){
                List<ConsumerRecord<String, String>> pRecord = records.records(partition);
                for (ConsumerRecord<String, String> record : pRecord) {
                    System.out.printf("patition = %d , offset = %d, key = %s, value = %s%n",
                            record.partition(), record.offset(), record.key(), record.value());

                }
                long lastOffset = pRecord.get(pRecord.size() -1).offset();
                // 单个partition中的offset，并且进行提交
                Map<TopicPartition, OffsetAndMetadata> offset = new HashMap<>();
                offset.put(partition,new OffsetAndMetadata(lastOffset+1));
                // 提交offset
                consumer.commitSync(offset);
                System.out.println("=============partition - "+ partition +" end================");
            }
            System.out.println("judy is gone...");
        }
    }
```

### 6.3 消费者多线程

> 每个线程创建一个消费者的经典模式

```java
public class ConsumerThreadSample {
    private final static String TOPIC_NAME="judy";

    /*
        这种类型是经典模式，每一个线程单独创建一个KafkaConsumer，用于保证线程安全
     */
    public static void main(String[] args) throws InterruptedException {
        KafkaConsumerRunner r1 = new KafkaConsumerRunner();
        Thread t1 = new Thread(r1);

        t1.start();

        Thread.sleep(15000);

        r1.shutdown();
    }

    public static class KafkaConsumerRunner implements Runnable{
        private final AtomicBoolean closed = new AtomicBoolean(false);
        private final KafkaConsumer consumer;

        public KafkaConsumerRunner() {
            // 配置消费者
            Properties props = new Properties();
            props.put("bootstrap.servers",  "kafka100:9092,kafka101:9092,kafka102:9092");
            props.put("group.id", "thread-test");
            props.put("enable.auto.commit", "false");
            props.put("auto.commit.interval.ms", "1000");
            props.put("session.timeout.ms", "30000");
            props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

            consumer = new KafkaConsumer<>(props);

            TopicPartition p0 = new TopicPartition(TOPIC_NAME, 0);
            TopicPartition p1 = new TopicPartition(TOPIC_NAME, 1);

            consumer.assign(Arrays.asList(p0,p1));
        }


        public void run() {
            try {
                while(!closed.get()) {
                    //处理消息
                    ConsumerRecords<String, String> records = consumer.poll(10000);

                    for (TopicPartition partition : records.partitions()) {
                        List<ConsumerRecord<String, String>> pRecord = records.records(partition);
                        // 处理每个分区的消息
                        for (ConsumerRecord<String, String> record : pRecord) {
                            System.out.printf("patition = %d , offset = %d, key = %s, value = %s%n",
                                    record.partition(),record.offset(), record.key(), record.value());
                        }

                        // 返回去告诉kafka新的offset
                        long lastOffset = pRecord.get(pRecord.size() - 1).offset();
                        // 注意加1
                        consumer.commitSync(Collections.singletonMap(partition, new OffsetAndMetadata(lastOffset + 1)));
                    }

                }
            }catch(WakeupException e) {
                if(!closed.get()) {
                    throw e;
                }
            }finally {
                consumer.close();
            }
        }

        // 关闭  把开关变量close关掉即可
        public void shutdown() {
            closed.set(true);
            consumer.wakeup();
        }
    }

}

```

> 一个消费者多个执行线程

```java
package com.dexlace.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.*;

public class ConsumerRecordThreadSample {
    private final static String TOPIC_NAME = "judy";

    public static void main(String[] args) throws InterruptedException {

        String brokerList = "kafka100:9092,kafka101:9092,kafka102:9092";
        String groupId = "test";
        int workerNum = 5;

        CunsumerExecutor consumers = new CunsumerExecutor(brokerList, groupId, TOPIC_NAME);
        consumers.execute(workerNum);

        Thread.sleep(1000000);

        consumers.shutdown();

    }

    // Consumer处理执行器
    public static class CunsumerExecutor{
        /**
         * 只有一个消费者，执行很多事情
         */
        private final KafkaConsumer<String, String> consumer;
        /**
         * 线程池，一般由Executors工具类去创建对应的执行单元
         */
        private ExecutorService executors;

        /**
         * 初始化一个消费者
         * @param brokerList kafka服务器的broker list
         * @param groupId 消费者组组名
         * @param topic topic名称
         */
        public CunsumerExecutor(String brokerList, String groupId, String topic) {
            Properties props = new Properties();
            props.put("bootstrap.servers", brokerList);
            props.put("group.id", groupId);
            props.put("enable.auto.commit", "true");
            props.put("auto.commit.interval.ms", "1000");
            props.put("session.timeout.ms", "30000");
            props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            consumer = new KafkaConsumer<>(props);
            consumer.subscribe(Arrays.asList(topic));
        }

        /**
         * 执行的逻辑
         * @param workerNum 线程数
         */
        public void execute(int workerNum) {
            /**
             * 核心线程数等于最大线程数，线程空闲时间为0，使用数组阻塞队列，拒绝策略为只用调用者所在线程来运行任务
             */
            executors = new ThreadPoolExecutor(workerNum, workerNum, 0L, TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<>(1000), new ThreadPoolExecutor.CallerRunsPolicy());

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(200);
                for (final ConsumerRecord record : records) {
                    // 使用线程池不断的提交任务
                    executors.submit(new ConsumerRecordWorker(record));
                }
            }
        }

        public void shutdown() {
            if (consumer != null) {
                consumer.close();
            }
            if (executors != null) {
                executors.shutdown();
            }
            try {
                if (!executors.awaitTermination(10, TimeUnit.SECONDS)) {
                    System.out.println("Timeout.... Ignore for this case");
                }
            } catch (InterruptedException ignored) {
                System.out.println("Other thread interrupted this shutdown, ignore for this case.");
                Thread.currentThread().interrupt();
            }
        }


    }

    // 需要执行的逻辑
    // 一个worker对应一个record
    public static class ConsumerRecordWorker implements Runnable {

        private ConsumerRecord<String, String> record;

        public ConsumerRecordWorker(ConsumerRecord record) {
            this.record = record;
        }

        @Override
        public void run() {
            // 假如说数据入库操作
            System.out.println("Thread - "+ Thread.currentThread().getName());
            System.err.printf("patition = %d , offset = %d, key = %s, value = %s%n",
                    record.partition(), record.offset(), record.key(), record.value());
        }

    }
}

```

### 6.4 消费者组

```bash

[kafka101 bin目录]kafka-console-consumer.sh --bootstrap-server kafka100:9092 --topic first --consumer.config config/consumer.properties
[kafka102 bin目录]kafka-console-consumer.sh --bootstrap-server kafka100:9092 --topic first --consumer.config config/consumer.properties
```

在kafka101、kafka102上修改kafka/config/consumer.properties配置文件中的==group.id属性为固定组名==。不指定的话，==虽然那几个成员还是属于一个组的（三台机器上的消费者组名都是一样的）==（也可以体会出==默认是基于消息队列模式的==），这样就有两个组，==kafka100属于单独一个组，kafka101和kafka102属于同一个组==

可以验证上面的同一个消费者组的消费者消费不同的消息。

#### 6.2.1 消费者组的意义

消费者组里某个消费者挂了==组内其他消费能接管partition==，这叫==重平衡==。

- ==高伸缩性==
- ==高容错性==

#### 6.2.2 消费者组再平衡

再均衡是指，==对于一个消费者组，分区的所属从一个消费者转移到另一个消费者的行为==。他为消费者组剧本里==高可用性==和==伸缩性==提供了保障，使得我们==既可以方便又安全地删除组内的消费者==或者==往消费者组里添加消费者==。不过再均衡发生期间，消费者是无法拉取信息的。

==消费者消费分区：==

- （1）指定了patition情况下，则直接使用；
- （2）==未指定patition但指定key情况下==，将==key的hash值与topic的partition数进行取余==得到partition值；
- （3）==patition和key都未指定情况下==，第一次调用时==随便生成一个整数==（后面每次调用在这个整数上自增），将这个值与topic可用的partition总数取余得到partition值。即使用round-robin算法轮询选出一个patition。

==再平衡触发条件：==

- ==当一个 consumer 加入组时==，读取的是原本由其他 consumer 读取的分区。
- ==当一个 consumer 离开组时==（被关闭或发生崩溃），原本由它读取的分区将由组里的其他 consumer 来读取。
- ==当 Topic 发生变化时，比如添加了新的分区==，会发生分区重分配。

## 七、Kafka面试题类型

### 7.1 Kafka常见应用场景/与其他中间件的异同点

概念：==分布式流处理平台==

特性一：基于发布订阅及Topic支持，能提供和消息队列相同的特性

特性二：吞吐量高==**但不保证消息有序**==，只保证==分区内的消息有序==，可以==消费历史内容==

 应用场景：

==日志收集或流式系统==

==消息系统==，对消息的有序性不是很在意，==比如订单系统==

==用户活动跟踪或运营指标监控，可以做推荐==

### 7.2 Kafka吞吐量为什么大/为什么快

 ==日志顺序读写==和快速检索

- Kafka的日志是以Partition为单位进行保存
- 日志目录格式为Topic名称+数字
- 日志分段：分为N个大小相等的segment，每个segment中消息数量不一定相等，==每一个partition只支持顺序读写==

Partition机制

批量发送接收及数据压缩机制

通过sendfile实现零拷贝原则





