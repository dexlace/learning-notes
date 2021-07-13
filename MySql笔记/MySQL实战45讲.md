#  MySQL实战45讲

## 完全篇

### 1. 一条SQL查询语句如何执行

<img src="MySQL%E5%AE%9E%E6%88%9845%E8%AE%B2.assets/image-20210710194508102.png" alt="image-20210710194508102" style="zoom:67%;" />

<img src="MySQL%E5%AE%9E%E6%88%9845%E8%AE%B2.assets/image-20210710194315914.png" alt="image-20210710194315914" style="zoom:67%;" />

#### 连接器

长连接：指连接成功后，如果==客户端持续有请求，则一直使用同一个连接==

短连接：则是指每次执行完==很少的几次查询就断开连接==，下次查询再重新建立一个

尽量使用长连接，但是==使用长连接可能系统涨得特别快==

#### 查询缓存

<font color=red>      

</font>

大多数情况下==建议不要使用查询缓存==，因为弊大于利，只要<font color=red>对一个表有更新，那么查询缓存都会被清空 </font>

除非你的业务就是有一张静态表，很长时间才会更新一次。比如，一个系统配置表，那这张表上的查询才适合使用查询缓存

mysql可以“按需使用”，可以将参数<font color=red> query_cache_type </font>设置成 <font color=red>  DEMAND</font>，这样对于默认的 SQL 语句都不使用查询缓存

而对于你确定要使用查询缓存的语句，可以用 ==SQL_CACHE 显式指定==，像下面这个语句一样：

```sql

mysql> select SQL_CACHE * from T where ID=10；
```

需要注意的是，**MySQL 8.0 版本直接将查询缓存的整块功能删掉了**，也就是说 8.0 开始彻底没有这个功能了。

#### 分析器

如果没有命中查询缓存，就要开始真正执行语句了。

首先，MySQL 需要知道你要做什么，因此需要对 SQL 语句做解析。

分析器先会做==“词法分析”==。你输入的是由多个字符串和空格组成的一条 SQL 语句，MySQL 需要识别出里面的字符串分别是什么，代表什么。

MySQL 从你输入的"select"这个关键字识别出来，这是一个查询语句。它也要把字符串“T”识别成“表名 T”，把字符串“ID”识别成“列 ID”。

做完了这些识别以后，就要做==“语法分析”==。根据词法分析的结果，语法分析器会根据语法规则，判断你输入的这个 SQL 语句是否满足 MySQL 语法。

#### 优化器

优化器是<font color=red>      在表里面有多个索引的时候，决定使用哪个索引</font>；或者在一个语句<font color=red> 有多表关联（join）</font>的时候，决定各个表的连接顺序。比如你执行下面这样的语句，这个语句是执行两个表的 join：

```sql
 select * from t1 join t2 using(ID)  where t1.c=10 and t2.d=20;
```

- 既可以先从表 t1 里面取出 c=10 的记录的 ID 值，再根据 ID 值关联到表 t2，再判断 t2 里面 d 的值是否等于 20。
- 也可以先从表 t2 里面取出 d=20 的记录的 ID 值，再根据 ID 值关联到 t1，再判断 t1 里面 c 的值是否等于 10。

这两种执行方法的逻辑结果是一样的，但是执行的效率会有不同，而优化器的作用就是决定选择使用哪一个方案。

#### 执行器

执行语句，看是否有权限，有则继续执行

打开表的时候，执行器就会根据表的引擎定义，==去使用这个引擎提供的接口。==

一行一行取结果，在没有索引的情况下

有索引，则取满足条件的结果

你会在数据库的慢查询日志中看到一个 ==rows_examined== 的字段，表示这个语句==执行过程中扫描了多少行==。

这个值就是在执行器==每次调用引擎获取数据行的时候累加的==。

在有些场景下，执行器调用一次，在引擎内部则扫描了多行，因此引擎扫描行数跟 rows_examined 并不是完全相同的。

### 2.一条SQL更新语句如何执行

MySQL 可以恢复到<font color=red>      **半个月内任意一秒的状态**</font>

与查询流程不一样的是，更新流程还涉及两个重要的日志模块，它们正是我们今天要讨论的主角：==redo log（重做日志）==和==binlog（归档日志）==

```sql

mysql> update T set c=c+1 where ID=2;
```



#### redo log

如果每一次的更新操作都需要写进磁盘，然后磁盘要==找到对应的那条记录，然后再更新，整个过程 IO 成本、查找成本都很高==

<font color=red>**WAL（Write-Ahead Logging）技术**: 先写日志，再写磁盘</font>

具体来说，当有一条记录需要更新的时候，==InnoDB 引擎就会先把记录写到 redo log==（粉板）里面，==并更新内存==，这个时候更新就算完成了。同时，==InnoDB 引擎会在适当的时候，将这个操作记录更新到磁盘里面==，而这个更新往往是在系统比较空闲的时候做。

<font color=red>InnoDB 的 redo log 是==固定大小的==，比如可以配置为一组 4 个文件，每个文件的大小是 1GB，那么这块“粉板”总共就可以记录 4GB 的操作。从头开始写，写到末尾就又回到开头循环写</font>，如下面这个图所示。

<img src="MySQL%E5%AE%9E%E6%88%9845%E8%AE%B2.assets/image-20210710204547500.png" alt="image-20210710204547500" style="zoom:80%;" />

**write pos** 是当前记录的位置，一边写一边后移，写到第 3 号文件末尾后就回到 0 号文件开头

**checkpoint** 是当前要擦除的位置，也是往后推移并且循环的，擦除记录前要把记录更新到数据文件。

有了 redo log，==InnoDB 就可以保证即使数据库发生异常重启，之前提交的记录都不会丢失==，这个能力称为 crash-safe。

<font color=red>redo log 他记录的是数据库当前值，实际上是==**防止系统崩溃**==的，当系统崩溃重启时，可以通过redo log 来恢复崩溃之前的页面。</font>

#### Binlog

前面我们讲过，MySQL 整体来看，其实就有两块：<font color=red>一块是 Server 层，它主要做的是 MySQL 功能层面的事情；还有一块是引擎层，负责存储相关的具体事宜</font>。上面我们聊到的粉板<font color=red> redo log 是 InnoDB 引擎特有的日志</font>，而 ==**Server 层也有自己的日志，称为 binlog（归档日志）**==。

因为最开始 MySQL 里并没有 InnoDB 引擎。==MySQL 自带的引擎是 MyISAM==，但是 ==MyISAM 没有 crash-safe 的能力，binlog 日志只能用于归档==。而 InnoDB 是另一个公司以插件形式引入 MySQL 的，既然只依靠 binlog 是没有 crash-safe 能力的，所以 InnoDB 使用另外一套日志系统——也就是 <font color=red>redo log 来实现 crash-safe 能力。</font>

这两种日志有以下三点不同。

- <font color=red>redo log 是 InnoDB 引擎特有的；binlog 是 MySQL 的 Server 层实现的</font>，所有引擎都可以使用。

- redo log 是==<font color=red>物理日志</font>，记录的是“在某个数据页上做了什么修改”==；binlog 是==<font color=red>逻辑日志</font>，记录的是这个语句的原始逻辑，比如“给 ID=2 这一行的 c 字段加 1 ”。==

- redo log 是循环写的，空间固定会用完；==binlog 是可以追加写入的==。“追加写”是指 binlog 文件写到一定大小后会切换到下一个，==并不会覆盖以前的日志==。

```sql

mysql> update T set c=c+1 where ID=2;
```

<img src="MySQL%E5%AE%9E%E6%88%9845%E8%AE%B2.assets/image-20210710212401163.png" alt="image-20210710212401163" style="zoom:50%;" />

将 redo log 的写入拆成了两个步骤：<font color=red>prepare</font> 和 <font color=red>commit</font>，这就是"两阶段提交"。

为什么必须有“两阶段提交”呢？==这是为了让两份日志之间的逻辑一致==

<font color=red>binlog是做**备份**的，binlog 中**记录的是sql语句，或行的内容**。当想恢复到之前几小时，几天或者几个月的数据时，就需要依次提取当时的binlog，按照时间顺序重放的到需要的时刻。</font>

### 3. 事务隔离级别：为什么你改了我还看不见

谈到事务一般都是以下四点

- <font color=red>原子性（Atomicity）</font>
  原子性是指事务是一个不可分割的工作单位，事务中的操作==要么都发生，要么都不发生==。
- <font color=red>一致性（Consistency）</font>
  事务前后==数据的完整性必须保持一致==。
- <font color=red>隔离性（Isolation）</font>
  事务的隔离性是多个用户并发访问数据库时，数据库为每一个用户开启的事务，不能被其他事务的操作数据所干扰，==多个并发事务之间要相互隔离==。
- <font color=red>持久性（Durability）</font>
  持久性是指一个事务==一旦被提交，它对数据库中数据的改变就是永久性的，接下来即使数据库发生故障也不应该对其有任何影响==

这里主要讲<font color=red>**隔离性**</font>：

当数据库上有多个事务同时执行的时候，就可能出现==脏读（dirty read）==、==不可重复读（non-repeatable read）==、==幻读（phantom read）==的问题，为了解决这些问题，就有了“隔离级别”的概念。

- <font color=red>**脏读**</font>：==读到了没有提交的数据==
- <font color=red>**不可重复读**</font>：在**一个事务中**前后<font color=red>两次读取的结果并不致</font>，导致了不可重复读。如果读不到没有提交的数据，即如果一个查询的过程中读取的是若干事务实时提交的结果，尽管得到的数据不一样，但其实也没多大问题
- <font color=red>**幻读**</font>：一个事务用一样的 SQL 多次查询，结果每次查询都会发现==查到一些之前没看到过的数据==（<font color=red>多了一些数据或少了一些数据</font>）。注意，幻读特指的是你查询到了之前查询没看到过的数据

隔离级别

- <font color=red>**读未提交**</font>：一个事务还没提交时，它做的变更就能被别的事务看到。<font color=red>**什么问题都解决不了**</font>
- <font color=red>**读提交**</font>：一个事务提交之后，它做的变更才会被其他事务看到。<font color=red>**解决脏读**</font>
- <font color=red>**可重复读**</font>：一个事务执行过程中看到的数据，总是跟这个事务在启动时看到的数据是一致的。当然在可重复读隔离级别下，未提交变更对其他事务也是不可见的。<font color=red>**解决不可重复读**</font>
- <font color=red>**串行化**</font>：顾名思义是对于同一行记录，“写”会加“写锁”，“读”会加“读锁”。当出现读写锁冲突的时候，后访问的事务必须等前一个事务执行完成，才能继续执行。<font color=red>**解决幻读**</font>

可重复读怎么实现

**==MySQL默认的隔离级别是可重复读==**，即：事务A在读到一条数据之后，此时事务B对该数据进行了修改并提交，那么事务A再读该数据，读到的还是原来的内容

使用的的一种叫MVCC的控制方式 ，即==<font color=red>Mutil-Version Concurrency Control,多版本并发控制</font>==，类似于乐观锁的一种实现方式

在读取事务开始时，系统会==给当前读事务一个版本号，事务会读取版本号<=当前版本号的数据==
此时如果==其他写事务修改了这条数据，那么这条数据的版本号就会加1，从而比当前读事务的版本号高==，读事务自然而然的就读不到更新后的数据了

长事务意味着<font color=red>系统里面会存在很老的事务视图。由于这些事务随时可能访问数据库里面的任何数据，所以这个事务提交之前，数据库里面它可能用到的回滚记录都必须保留，这就会导致大量占用存储空间</font>。

**长事务有这些潜在风险**，我当然是建议你尽量避免。其实很多时候业务开发同学并不是有意使用长事务，通常是由于误用所致。

MySQL 的事务启动方式有以下几种：

- ==显式启动事务语句==， begin 或 start transaction。配套的提交语句是 commit，回滚语句是 rollback。
- ==set autocommit=0，这个命令会将这个线程的自动提交关掉==。意味着如果你只执行一个 select 语句，这个事务就启动了，而且并不会自动提交。这个事务持续存在直到你主动执行 commit 或 rollback 语句，或者断开连接。

因此，我会建议你总是使用 ==set autocommit=1, 通过显式语句的方式来启动事务==。

但是有的开发同学会纠结“多一次交互”的问题。对于一个需要频繁使用事务的业务，第二种方式每个事务在开始时都不需要主动执行一次 “begin”，减少了语句的交互次数。如果你也有这个顾虑，我建议你使用 commit work and chain 语法。

在 ==autocommit 为 1== 的情况下，用 ==begin 显式启动的事务==，如果执行 commit 则提交事务。如果执行 ==commit work and chain==，则是提交事务并自动启动下一个事务，这样也省去了再次执行 begin 语句的开销。同时带来的好处是从程序开发的角度明确地知道每个语句是否处于事务中。

### 4. 深入浅出索引（上）

#### InnoDB的索引模型

InnoDB 使用了 ==B+ 树索引模型==，所以数据都是存储在 B+ 树中的。每一个索引在 InnoDB 里面对应一棵 B+ 树。

```sql

mysql> create table T(
id int primary key, 
k int not null, 
name varchar(16),
index (k))engine=InnoDB;
```

表中 R1~R5 的 (ID,k) 值分别为 (100,1)、(200,2)、(300,3)、(500,5) 和 (600,6)，两棵树的示例示意图如下。

<img src="MySQL%E5%AE%9E%E6%88%9845%E8%AE%B2.assets/image-20210711002006324.png" alt="image-20210711002006324" style="zoom:80%;" />

==主键索引==的叶子节点存的是==整行数据==。

在 InnoDB 里，主键索引也被称为==聚簇索引（clustered index）==。

==非主键索引==的叶子节点内容是==主键的值==。

在 InnoDB 里，非主键索引也被称为==二级索引（secondary index==）。

##### 基于主键索引和普通索引的查询有什么区别

如果语句是 select * from T where ID=500，即主键查询方式，则只需要搜索 ID 这棵 B+ 树；

如果语句是 select * from T where k=5，即普通索引查询方式，则需要先搜索 k 索引树，得到 ID 的值为 500，==再到 ID 索引树搜索一次。这个过程称为回表。==

也就是说，<font color=red>基于非主键索引的查询需要多扫描一棵索引树</font>。因此，我们在应用中应该尽量使用主键查询。

##### 索引维护

B+ 树为了维护索引有序性，在插入新值的时候需要做必要的维护。

以上面这个图为例，如果插入新的行 ID 值为 700，则只需要在 R5 的记录后面插入一个新记录。

如果新插入的 ID 值为 400，就相对麻烦了，需要逻辑上挪动后面的数据，空出位置。

而更糟的情况是，如果 R5 所在的数据页已经满了，根据 B+ 树的算法，这时候需要申请一个新的数据页，然后挪动部分数据过去。这个过程称为==页分裂==。

在这种情况下，性能自然会受影响。除了性能外，页分裂操作还影响数据页的利用率。原本放在一个页的数据，现在分到两个页中，整体空间利用率降低大约 50%。

当然有分裂就有==合并==。当相邻两个页由于删除了数据，利用率很低之后，会将数据页做合并。合并的过程，可以认为是分裂过程的逆过程。

###### 自增主键

> 你可能在一些建表规范里面见到过类似的描述，要求建表语句里一定要有自增主键。当然事无绝对，我们来分析一下==哪些场景下应该使用自增主键，而哪些场景下不应该==。

自增主键是指自增列上定义的主键，在建表语句中一般是这么定义的：` NOT NULL PRIMARY KEY AUTO_INCREMENT`。

插入新记录的时候可以不指定 ID 的值，系统会获取当前 ID 最大值加 1 作为下一条记录的 ID 值。也就是说，自增主键的插入数据模式，正符合了我们前面提到的递增插入的场景。

每次插入一条新记录，都是追加操作，都不涉及到挪动其他记录，也==不会触发叶子节点的分裂==。

除了考虑性能外，我们还可以<font color=red>从存储空间的角度来看</font>。假设你的表中确实==有一个唯一字段==，比如字符串类型的==身份证号==，那==应该用身份证号做主键，还是用自增字段做主键呢==？

由于==每个非主键索引的叶子节点上都是主键的值==。如果用身份证号做主键，那么每个二级索引的叶子节点占用约 20 个字节，而如果用整型做主键，则只要 4 个字节，如果是长整型（bigint）则是 8 个字节。==显然，主键长度越小，普通索引的叶子节点就越小，普通索引占用的空间也就越小==。

**<font color=red>所以，从性能和存储空间方面考量，自增主键往往是更合理的选择。</font>**

有没有什么场景适合用业务字段直接做主键的呢？

还是有的。比如，有些业务的场景需求是这样的：

==只有一个索引；==

==该索引必须是唯一索引。==

你一定看出来了，**<font color=red>这就是典型的 KV 场景。</font>**

这时候我们就要优先考虑上一段提到的“尽量使用主键查询”原则，直接将这个索引设置为主键，可以避免每次查询需要搜索两棵树。

###### 为何以及如何重建索引

### 5. 深入浅出索引（下）

```sql

mysql> create table T (
ID int primary key,
k int NOT NULL DEFAULT 0, 
s varchar(16) NOT NULL DEFAULT '',
index k(k))
engine=InnoDB;

insert into T values(100,1, 'aa'),(200,2,'bb'),(300,3,'cc'),(500,5,'ee'),(600,6,'ff'),(700,7,'gg');
```

<img src="MySQL%E5%AE%9E%E6%88%9845%E8%AE%B2.assets/image-20210711104243085.png" alt="image-20210711104243085" style="zoom:67%;" />

如果我执行 `select * from T where k between 3 and 5`，需要执行几次树的搜索操作，会扫描多少行？

<img src="MySQL%E5%AE%9E%E6%88%9845%E8%AE%B2.assets/image-20210711104654832.png" alt="image-20210711104654832" style="zoom:67%;" />

在这个过程中，==回到主键索引树搜索的过程，我们称为回表==。可以看到，这个查询过程**读了 k 索引树的 3 条记录（步骤 1、3 和 5），回表了两次（步骤 2 和 4）。**

#### 覆盖索引

如果执行的语句是 `select ID from T where k between 3 and 5`，这时只需要查 ID 的值，**而 ID 的值已经在 k 索引树上了，因此可以直接提供查询结果，不需要回表**。也就是说，在这个查询里面，索引 k 已经“覆盖了”我们的查询需求，我们称为覆盖索引。

由于覆盖索引可以减少树的搜索次数，显著提升查询性能，所以使用覆盖索引是一个常用的性能优化手段。

```sql

CREATE TABLE `tuser` (
  `id` int(11) NOT NULL,
  `id_card` varchar(32) DEFAULT NULL,
  `name` varchar(32) DEFAULT NULL,
  `age` int(11) DEFAULT NULL,
  `ismale` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `id_card` (`id_card`),
  KEY `name_age` (`name`,`age`)
) ENGINE=InnoDB
```

如果现在有一个高频请求，==要根据市民的身份证号查询他的姓名==，这个==联合索引(将身份证号和名字建立联合索引)==就有意义了。它可以在这个高频请求上用到覆盖索引，不再需要回表查整行记录，减少语句的执行时间。

也就是==频繁作为查询条件的字段应该创建索引==

#### 最左前缀原则

我们用（name，age）这个联合索引来分析。

<img src="MySQL%E5%AE%9E%E6%88%9845%E8%AE%B2.assets/image-20210711113407365.png" alt="image-20210711113407365" style="zoom:67%;" />

当你的逻辑需求是查到所有名字是“张三”的人时，可以快速定位到 ID4，然后向后遍历得到所有需要的结果。

如果你要查的是所有名字第一个字是“张”的人，你的 SQL 语句的条件是"where name like ‘张 %’"。这时，你也能够用上这个索引，查找到第一个符合条件的记录是 ID3，然后向后遍历，直到不满足条件为止。

可以看到，不只是索引的全部定义，==只要满足最左前缀，就可以利用索引来加速检索==。这个最左前缀可以是联合索引的最左 N 个字段，也可以是字符串索引的最左 M 个字符。

#### 如何安排联合索引中索引的字段顺序

- 第一原则是，==如果通过调整顺序，可以少维护一个索引==，那么这个顺序往往就是需要优先考虑采用的。

那么，如果既有联合查询，又有基于 a、b 各自的查询呢？查询条件里面只有 b 的语句，是无法使用 (a,b) 这个联合索引的，这时候你不得不维护另外一个索引，也就是说你需要同时维护 (a,b)、(b) 这两个索引。

这时候，我们要考虑的原则就是==空间==了。比如上面这个市民表的情况，==name 字段是比 age 字段大的 ，那我就建议你创建一个（name,age) 的联合索引和一个 (age) 的单字段索引。==

#### 索引下推

最左前缀可以用于在索引中定位记录。这时，你可能要问，那些不符合最左前缀的部分，会怎么样呢？

如果现在有一个需求：检索出表中“名字第一个字是张，而且年龄是 10 岁的所有男孩”。那么，SQL 语句是这么写的：

```sql
select * from tuser where name like '张%' and age=10 and ismale=1;
```

用==前缀索引规则==，所以这个语句在搜索索引树的时候，只能用 “张”，找到第一个满足条件的记录 ID3

**然后呢**

<img src="MySQL%E5%AE%9E%E6%88%9845%E8%AE%B2.assets/image-20210711143800086.png" alt="image-20210711143800086" style="zoom:67%;" />

当然是判断其他条件是否满足。

在 MySQL 5.6 之前，只能从 ID3 开始一个个==回表==。==到主键索引上找出数据行，再对比字段值==。

而 MySQL 5.6 引入的==索引下推==优化（index condition pushdown)， 可以在索引遍历过程中，==对索引中包含的字段先做判断，直接过滤掉不满足条件的记录，减少回表次数==。

如下==没有索引下推优化==

<img src="MySQL%E5%AE%9E%E6%88%9845%E8%AE%B2.assets/image-20210711144300481.png" alt="image-20210711144300481" style="zoom:67%;" />

有==索引下推优化==：

<img src="MySQL%E5%AE%9E%E6%88%9845%E8%AE%B2.assets/image-20210711144327677.png" alt="image-20210711144327677" style="zoom:67%;" />

有索引下推优化的情况下，InnoDB 在 (name,age) ==索引内部就判断了 age 是否等于 10==，对于==不等于 10==的记录，==直接判断并跳过==。在我们的这个例子中，只需要对 ID4、ID5 这两条记录回表取数据判断，就只需要回表 2 次

### 6. 全局锁和表锁：给表加个字段怎么这么难

#### 全局锁

加全局锁命令

```bash
Flush tables with read lock 
```

加了之后，整个库处于只读状态，==数据更新语句==（数据的增删改）、==数据定义语句==（包括建表、修改表结构等）和==更新类事务的提交语句==。

全局锁的使用典型使用场景是：==**全库逻辑备份**==

但是让整库都只读，听上去就很危险：

- 如果你在主库上备份，那么在备份期间都不能执行更新，业务基本上就得停摆；

- 如果你在==从库上备份==，那么==备份期间从库不能执行主库同步过来的 binlog，会导致主从延迟==。看来加全局锁不太好。

但是细想一下，备份为什么要加锁呢？我们来看一下不加锁会有什么问题。

不用去看是否有啥问题，肯定有问题，很容易想

总结就是：<font color=red>不加锁的话，备份系统备份的得到的库不是一个逻辑时间点，这个视图是逻辑不一致的。</font>

如此，另一种解决方案是，可以在<font color=red>可重复读隔离级别下开启一个事务</font>

官方自带的逻辑备份工具是 ==mysqldump==。当 mysqldump 使用参数==–single-transaction== 的时候，导数据之前就==会启动一个事务，来确保拿到一致性视图==。而由于 MVCC 的支持，这个过程中数据是可以正常更新的。

你一定在疑惑，有了这个功能，为什么还需要 FTWRL 呢？<font color=red>一致性读是好，但前提是引擎要支持这个隔离级别</font>。比如，对于<font color=red> MyISAM 这种不支持事务的引擎</font>，如果备份过程中有更新，总是只能取到最新的数据，那么就破坏了备份的一致性。这时，我们就需要使用 FTWRL 命令了。

所以，==single-transaction 方法只适用于所有的表使用事务引擎的库==。如果有的表使用了不支持事务的引擎，那么备份就只能通过 FTWRL 方法。这往往是 ==DBA 要求业务开发人员使用 InnoDB 替代 MyISAM 的原因之一==。

<img src="MySQL%E5%AE%9E%E6%88%9845%E8%AE%B2.assets/image-20210712095312250.png" alt="image-20210712095312250" style="zoom:80%;" />

#### 表级锁(InnoDB不支持)

##### 表锁

MySQL 里面表级别的锁有两种：一种是**<font color=red>表锁</font>**，一种是**<font color=red>元数据锁（meta data lock，MDL)</font>**。

- **<font color=red>表共享读锁</font>**：顾名思义，所有表都能读，当前线程不释放该锁连写都不能；简而言之，==加锁期间所有线程只能读==

- **<font color=red>表独占写锁</font>**：顾名思义，加锁期间，==只有当前线程能读写，别的线程既不能读也不能写==

```sql
-- test表将会被锁住，另一个线程执行select * from test where id = 3;不会等待
-- 执行UPDATE test set name='peter' WHERE id = 4;将会一直等侍，直到test表解锁
LOCK table test READ;
-- 当一个线程获得对一个表的写锁后，只有持有锁的线程可以对表进行更新操作。
-- 其他线程的读、写操作都会等待，直到锁被释放为止。
-- test表将会被锁住，另一个线程执行select * from test where id = 3;将会一直等待，直到test表解锁
LOCK TABLE test WRITE; 
```

##### 元数据锁

另一类表级的锁是 ==MDL（metadata lock)==

元数据锁主要是==面向DML==和==DDL==之间的并发控制，如果对一张表做DML增删改查操作的同时，有一个线程在做DDL操作，不加控制的话，就会出现错误和异常。==元数据锁不需要我们显式的加，系统默认会加。==

###### 元数据锁的原理

当做DML操作时，会申请一个MDL==读锁==,因此你可以有多个线程同时对一张表增删改查。
当做==DDL==操作时，会申请一个MDL==写锁==
读锁之间不互斥，<font color=red>读写和写写之间都互斥</font>。

<font color=red>虽然 MDL 锁是系统默认会加的</font>,事务中的 MDL 锁，==在语句执行开始时申请，但是语句结束后并不会马上释放，而会等到整个事务提交后再释放==

> 备注：这里的实验环境是 MySQL 5.6。
>
> <img src="MySQL%E5%AE%9E%E6%88%9845%E8%AE%B2.assets/image-20210712105553861.png" alt="image-20210712105553861" style="zoom:80%;" />

==session A 先启动==，这时候会对表 t 加一个 ==MDL 读锁==。由于 session B 需要的==也是 MDL 读锁==，因此可以==正常执行。==

==session C 会被 blocked==，是因为 session A 的 ==MDL 读锁还没有释放==，而 session C 需要 MDL 写锁，因此只能被阻塞。

但是之后==所有要在表 t 上新申请 MDL 读锁的请求也会被 session C 阻塞==

<font color=red>等于这个表现在完全不可读写了。</font>

如果某个表上的==查询语句频繁==，而且客户端有==重试机制==，也就是说超时后会再起一个新 session 再请求的话，这个库的==线程很快就会爆满。==

###### 如何安全地加字段

- 首先解决长事务，事务不提交，就会一直占着MDL锁

> 在 MySQL 的 information_schema 库的 innodb_trx 表中，你可以查到当前执行中的事务。如果你要做 DDL 变更的表刚好有长事务在执行，要考虑先暂停 DDL，或者 kill 掉这个长事务。

- ==在 alter table 语句里面设定等待时间==，如果在这个指定的等待时间里面能够拿到 MDL 写锁最好，拿不到也不要阻塞后面的业务语句，先放弃

MariaDB 已经合并了 AliSQL 的这个功能，所以这两个开源分支目前都支持 ==DDL NOWAIT/WAIT n==这个语法。

```sql

ALTER TABLE tbl_name NOWAIT add column ...
ALTER TABLE tbl_name WAIT N add column ... 
```

### 7. 行锁功过：怎么减少行锁对性能的影响

MySQL 的行锁是在引擎层由各个引擎自己实现的。但并不是所有的引擎都支持行锁，比如 ==MyISAM 引擎就不支持行锁==。不支持行锁意味着并发控制只能使用==表锁==，对于这种引擎的表，==同一张表上任何时刻只能有一个更新在执行==，这就会影响到业务并发度。==InnoDB 是支持行锁的，这也是 MyISAM 被 InnoDB 替代的重要原因之一。==

行锁：顾名思义，针对数据表中==行记录的锁==。比如事务 A 更新了一行，而这时候事务 B 也要更新同一行，则必须等事务 A 的操作完成后才能进行更新

#### 两阶段锁协议

**两阶段锁协议**：分为<font color=red>加锁阶段</font>和<font color=red>解锁阶段</font>，==所有的 lock 操作都在 unlock 操作之后==

在下面的操作序列中，事务 B 的 update 语句执行时会是什么现象呢？假设字段 id 是表 t 的主键。

<img src="MySQL%E5%AE%9E%E6%88%9845%E8%AE%B2.assets/image-20210712115500990.png" alt="image-20210712115500990" style="zoom:67%;" />

可以验证一下：==实际上事务 B 的 update 语句会被阻塞，直到事务 A 执行 commit 之后，事务 B 才能继续执行。==

事务 A 持有的两个记录的行锁，都是在 commit 的时候才释放的。

在 InnoDB 事务中，行锁是在需要的时候才加上的，但并不是不需要了就立刻释放，而是<font color=red>要等到事务结束时才释放</font>

<font color=red>**如果你的事务中需要锁多行，要把最可能造成锁冲突，最可能影响并发度的锁尽量往后放**</font>

如以下情景：

> 顾客A在影院B购票，涉及的业务操作如下：
>
> 1. 从顾客 A 账户余额中扣除电影票价；
> 2. 给影院 B 的账户余额增加这张电影票价；
> 3. 记录一条交易日志。

涉及到两条update和一条insert操作，为了保证交易的原子性，我们需要把三个操作放到一个事务中。但是**<font color=red>如何安排三个操作在事务中的顺序</font>**呢？

很显然，**<font color=red>把操作2安排在最后</font>**，则影响影院账户余额的锁时间最少

#### 死锁和死锁检测

当并发系统中不同线程出现==循环资源依赖==，涉及的==线程都在等待别的线程释放资源时==，就会导致这几个线程都进入无限等待的状态，称为==死锁==

<img src="MySQL%E5%AE%9E%E6%88%9845%E8%AE%B2.assets/image-20210712134744016.png" alt="image-20210712134744016" style="zoom:67%;" />

死锁很简单，如上，不解释，怎么解决：

- 一种策略是，直接进入等待，直到超时。这个超时时间可以通过==参数 innodb_lock_wait_timeout 来设置==。
- 另一种策略是，发起死锁检测，发现死锁后，==主动回滚死锁链条中的某一个事务==，让其他事务得以继续执行。将参数 ==innodb_deadlock_detect 设置为 on==，表示开启这个逻辑。

 innodb_lock_wait_timeout不好设置，==<font color=red>一般使用主动死锁检测</font>==

但是==<font color=red>主动死锁检测需要消耗大量的CPU资源</font>==

在数据库服务端==<font color=red>做并发控制，如果你有中间件，可以考虑在中间件实现</font>==；如果你的团队有能修改 MySQL 源码的人，也可以做在 MySQL 里面。基本思路就是，对于相同行的更新，在进入引擎之前排队。这样在 InnoDB 内部就不会有大量的死锁检测工作了。

### 9. 普通索引和唯一索引，应该怎么选择

假设你在维护一个市民系统，每个人都有一个唯一的身份证号，而且业务代码已经保证了==不会写入两个重复的身份证号==。如果市民系统需要按照身份证号查姓名，就会执行类似这样的 SQL 语句：

```sql

select name from CUser where id_card = 'xxxxxxxyyyyyyzzzzz';
```

<img src="MySQL%E5%AE%9E%E6%88%9845%E8%AE%B2.assets/image-20210712145932318.png" alt="image-20210712145932318" style="zoom:80%;" />

##### 查询过程

`select id from T where k=5`

先是==通过 B+ 树从树根开始，按层搜索到叶子节点==，也就是图中右下角的这个数据页，然后可以认为==数据页内部通过二分法==来定位记录。

- 对于==普通索引==来说，查找到满足条件的第一个记录 (5,500) 后，==需要查找下一个记录==，直到碰到第一个不满足 k=5 条件的记录。

- 对于==唯一索引==来说，由于索引定义了唯一性，==查找到第一个满足条件的记录后，就会停止继续检索==。

<font color=red>那么，这个不同带来的性能差距会有多少呢？答案是，微乎其微。</font>

InnoDB 的数据是<font color=red>按数据页</font>为单位来读写的。也就是说，当需要读一条记录的时候，并不是将这个记录本身从磁盘读出来，而是<font color=red>以页为单位，将其整体读入内存</font>。在 InnoDB 中，<font color=red>每个数据页的大小默认是 16KB。</font>

因为引擎是按页读写的，所以说，==当找到 k=5 的记录的时候，它所在的数据页就都在内存里了。那么，对于普通索引来说，要多做的那一次“查找和判断下一条记录”的操作，就只需要一次指针寻找和一次计算==。

![image-20210712153544807](MySQL%E5%AE%9E%E6%88%9845%E8%AE%B2.assets/image-20210712153544807.png)

##### 更新过程

当需要更新一个数据页时，如果==数据页在内存中就直接更新==，而如果这个==数据页还没有在内存中==的话，<font color=red>在不影响数据一致性的前提下，InnoDB 会将这些更新操作缓存在 change buffer 中，这样就不需要从磁盘中读入这个数据页了</font>。在下次<font color=red>查询需要访问这个数据页的时候</font>，将数据页读入内存，然后==执行 change buffer 中与这个页有关的操作==。通过这种方式就能保证这个数据逻辑的正确性。

 change buffer，实际上它==是可以持久化的数据==。也就是说，change buffer 在内存中有拷贝，也会被写入到磁盘上。

将 change buffer 中的操作应用到原数据页，得到最新结果的过程称为 merge。除了访问这个数据页会触发 merge 外，系统有后台线程会定期 merge。在数据库正常关闭（shutdown）的过程中，也会执行 merge 操作。

###### 什么情况下使用change buffer

待续。。。。

结论是：

由于<font color=red>唯一索引用不上 change buffer 的优化机制</font>，因此如果业务可以接受，从性能角度出发我建议你<font color=red>优先考虑非唯一索引</font>。

### 10. MySql为什么有时候会选错索引

```sql

CREATE TABLE `t` (
  `id` int(11) NOT NULL,
  `a` int(11) DEFAULT NULL,
  `b` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `a` (`a`),
  KEY `b` (`b`)
) ENGINE=InnoDB；
```

。。。

### 11. "order by"是怎么工作的

```sql

CREATE TABLE `house` (
  `id` int(11) NOT NULL,
  `city` varchar(16) NOT NULL,
  `name` varchar(16) NOT NULL,
  `age` int(11) NOT NULL,
  `addr` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `city` (`city`)
) ENGINE=InnoDB;
```

假设你要查询城市是“杭州”的所有人名字，并且按照姓名排序返回前 1000 个人的姓名、年龄。

```sql

select city,name,age from t where city='杭州' order by name limit 1000  ;
```

#### 全字段排序

==Extra 这个字段中的“Using filesort”表示的就是需要排序==，<font color=red size=5>MySQL 会给每个线程分配一块内存用于排序，称为 sort_buffer。</font>

<img src="MySQL%E5%AE%9E%E6%88%9845%E8%AE%B2.assets/image-20210713154921327.png" alt="image-20210713154921327" style="zoom:80%;" />

<img src="MySQL%E5%AE%9E%E6%88%9845%E8%AE%B2.assets/image-20210713155256448.png" alt="image-20210713155256448" style="zoom: 67%;" />

从图中可以看到，满足 city='杭州’条件的行，是从 ID_X 到 ID_(X+N) 的这些记录。

通常情况下，这个语句执行流程如下所示 ：

- ==初始化 sort_buffer==，确定放入 name、city、age 这三个字段；

- <font color=red>从索引 city</font> 找到==第一个满足 city='杭州’条件的主键 id==，也就是图中的 ID_X；

- 到<font color=red size=5>主键 id 索引取出整行，取 name、city、age 三个字段的值，存入 sort_buffer 中</font><font color=blue size=5>这里只想说明，别以为索引天下无敌，非主键索引存的都是索引字段的值，你要其他值必须用到主键id去回表查询</font>；

- 从索引 city 取下一个记录的主键 id；

- 重复步骤 3、4 直到 city 的值不满足查询条件为止，对应的主键 id 也就是图中的 ID_Y；

- 对 sort_buffer 中的数据==按照字段 name 做快速排序==；

- 按照排序结果取前 1000 行返回给客户端。

<img src="MySQL%E5%AE%9E%E6%88%9845%E8%AE%B2.assets/image-20210713163045994.png" alt="image-20210713163045994" style="zoom:67%;" />

图中“按 name 排序”这个动作，可能==在内存中完成==，也可能需要==使用外部排序==，这取决于排序==所需的内存==和==参数 sort_buffer_size==。

sort_buffer_size，就是==MySQL 为排序开辟的内存（sort_buffer）的大小==。如果要排序的数据量小于 sort_buffer_size，排序就在内存中完成。但如果排序数据量太大，内存放不下，则不得不利用磁盘临时文件辅助排序。

#### rowid排序

全字段排序的缺点显而易见：<font color=red size=5>如果查询要返回的字段很多，那么 sort_buffer 里面要放的字段数太多，这样内存里能够同时放下的行数很少，要分成很多个临时文件，排序的性能会很差。</font>

==控制一个排序的参数如下==：我们修改为16

```sql

SET max_length_for_sort_data = 16;
```

是 MySQL 中专门控制用于排序的行数据的长度的一个参数。它的意思是，<font color=red>如果单行的长度超过这个值，MySQL 就认为单行太大，要换一个算法。</font>

`city、name、age` 这三个字段的定义总长度是<font color=red>36</font>，我把==max_length_for_sort_data 设置为 16==

<img src="MySQL%E5%AE%9E%E6%88%9845%E8%AE%B2.assets/image-20210713171505435.png" alt="image-20210713171505435" style="zoom:67%;" />

可见，排序只用了==name和主键id两列==

- <font color=red>初始化 sort_buffer，确定放入两个字段，即 name 和 id</font>；
- 从<font color=red>索引 city 找到第一个满足 city='杭州’条件的主键 id</font>，也就是图中的 ID_X；
- 到主键 id 索引取出整行，取 name、id 这两个字段，存入 sort_buffer 中；
- 从<font color=red>索引 city 取下一个记录的主键 id</font>；
- 重复步骤 3、4 直到不满足 city='杭州’条件为止，也就是图中的 ID_Y；
- <font color=red>对 sort_buffer 中的数据按照字段 name 进行排序</font>；
- 遍历排序结果，取前 1000 行，并按照 id 的值回到原表中取出 city、name 和 age 三个字段返回给客户端。

<img src="MySQL%E5%AE%9E%E6%88%9845%E8%AE%B2.assets/image-20210713172510118.png" alt="image-20210713172510118" style="zoom:67%;" />

###  23. MySQL如何保证数据不丢失

#### binlog的写入机制

<font color=red>事务执行过程中，先把日志写到 binlog cache，事务提交的时候，再把 binlog cache 写到 binlog 文件中。</font>

==一个事务的 binlog 是不能被拆开的，因此不论这个事务多大，也要确保一次性写入==。这就涉及到了 binlog cache 的保存问题。

<img src="MySQL%E5%AE%9E%E6%88%9845%E8%AE%B2.assets/image-20210713113601699.png" alt="image-20210713113601699" style="zoom:67%;" />

可以看到，==每个线程有自己 binlog cache，但是共用同一份 binlog 文件==。

<img src="MySQL%E5%AE%9E%E6%88%9845%E8%AE%B2.assets/image-20210713114213086.png" alt="image-20210713114213086" style="zoom:67%;" />



 ### 24. MySQL如何保证主备一致

#### MySQL主备的基本原理

<img src="MySQL%E5%AE%9E%E6%88%9845%E8%AE%B2.assets/image-20210713000321388.png" alt="image-20210713000321388" style="zoom:80%;" />

在状态 1 中，客户端的读写都直接访问节点 A，而节点 B 是 A 的备库，只是将 A 的更新都同步过来，到本地执行。这样可以保持节点 B 和 A 的数据是相同的。

当需要切换的时候，就切成状态 2。这时候客户端读写访问的都是节点 B，而节点 A 是 B 的备库。

在状态 1 中，虽然节点 B 没有被直接访问，但是我依然建议你把==节点 B（也就是备库）设置成只读（readonly）模式==。这样做，有以下几个考虑：

- 有时候一些运营类的查询语句会被放到备库上去查，设置为==只读可以防止误操作==；

- ==防止切换逻辑有 bug==，比如切换过程中出现双写，==造成主备不一致==；

- 可以用 ==readonly 状态，来判断节点的角色==。

==把备库设置成只读，怎么跟主库保持同步更新==

<font color=red>因为 readonly 设置对超级 (super) 权限用户是无效的，而用于同步更新的线程，就拥有超级权限。</font>

<img src="MySQL%E5%AE%9E%E6%88%9845%E8%AE%B2.assets/image-20210713115230968.png" alt="image-20210713115230968" style="zoom:67%;" />

备库 B 跟主库 A 之间维持了一个长连接。主库 A 内部有一个线程，专门用于服务备库 B 的这个长连接。一个事务日志同步的完整过程是这样的：

- 在备库 B 上通过 ==change master 命令(用于配置和改变slave服务器用于连接master服务器的参数)==，设置主库 A 的 IP、端口、用户名、密码，以及要从哪个位置开始请求 binlog，这个位置包含文件名和日志偏移量。

- 在备库 B 上执行 ==start slave 命令==，这时候备库会<font color=red >启动两个线程</font>，就是图中的 <font color=red >io_thread </font>和 <font color=red >sql_thread</font>。其中 io_thread 负责与主库建立连接。

- 主库 A 校验完用户名、密码后，开始<font color=red >按照备库 B 传过来的位置，从本地读取 binlog，发给 B</font>。

- <font color=red >备库 B 拿到 binlog 后，写到本地文件，称为中转日志（relay log）</font>。

- <font color=red>sql_thread 读取中转日志，解析出日志里的命令，并执行</font>。

#### Binlog的使用

##### 如何开启Binlog日志

```properties
[mysql]
default-character-set=utf8

[mysqld]
# 设置mysql客户端默认字符集
character-set-server=utf8
# 创建新表时将使用的默认存储引擎
default-storage-engine=INNODB
# 允许最大连接数
max_connections=200
# Binary Logging
log_bin=mysql-bin
binlog-format=ROW
server-id=1
```

binlog的安装目录的==data文件下则有mysql-bin.00001之类的binlog==

类似的还有如下具体配置logbin的存储路径

```properties
 # 是binlog日志的基本文件名，后面会追加标识来表示每一个文件
log_bin_basename=/var/lib/mysql/mysql-bin  
# 指定的是binlog文件的索引文件，这个文件管理了所有的binlog文件的目录
log_bin_index=/var/lib/mysql/mysql-bin.index
# 一种简单的配置，一个参数就可以搞定
# 这一个参数的作用和上面三个的作用是相同的，mysql会根据这个配置自动设置log_bin为on状态，自动设置log_bin_index文件为你指定的文件名后跟.index
log-bin=/var/lib/mysql/mysql-bin
```

注意，<font color=red>对于5.7以上的版本，记住一定要指定`server-id`,该参数是为了区别集群区别作用，不能重名</font>

<img src="MySQL%E5%AE%9E%E6%88%9845%E8%AE%B2.assets/image-20210713122854402.png" alt="image-20210713122854402" style="zoom:67%;" />

##### Binlog的三种格式对比

```sql
 CREATE TABLE `t` (
  `id` int(11) NOT NULL,
  `a` int(11) DEFAULT NULL,
  `t_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `a` (`a`),
  KEY `t_modified`(`t_modified`)
) ENGINE=InnoDB;

insert into t values(1,1,'2018-11-13');
insert into t values(2,2,'2018-11-12');
insert into t values(3,3,'2018-11-11');
insert into t values(4,4,'2018-11-10');
insert into t values(5,5,'2018-11-09');
```

如果要在表中删除一行数据，现在查看==delete语句的binlog statement格式是怎么记录的==

```sql

mysql> delete from t  /*comment*/  where a>=4 and t_modified<='2018-11-10' limit 1;
```

<img src="MySQL%E5%AE%9E%E6%88%9845%E8%AE%B2.assets/image-20210713132822761.png" alt="image-20210713132822761" style="zoom:80%;" />

- 第一行 SET @@SESSION.GTID_NEXT='ANONYMOUS'你可以先忽略

- 第二行是==一个 BEGIN，跟第四行的 commit 对应，表示中间是一个事务==；

- 第三行就是==真实执行的语句了==。可以看到，在真实执行的 delete 命令之前，还有一个“use ‘test’”命令。这条命令不是我们主动执行的，而是 MySQL 根据当前要操作的表所在的数据库，自行添加的。这样做可以保证日志传到备库去执行的时候，不论当前的工作线程在哪个库里，都能够正确地更新到 test 库的表 t。use 'test’命令之后的 delete 语句，就是我们输入的 SQL 原文了。可以看到，binlog“忠实”地记录了 SQL 命令，==甚至连注释也一并记录了==。

- 最后一行是一个 COMMIT。你可以看到里面写着==xid=61==

上面语句执行有一个warning，<font color=red>原因是当前 binlog 设置的是 statement 格式，并且语句中有 limit，所以这个命令可能是 unsafe 的</font>。

为什么这么说呢？这是<font color=red>因为 delete 带 limit，很可能会出现主备数据不一致的情况</font>。比如上面这个例子：

<img src="MySQL%E5%AE%9E%E6%88%9845%E8%AE%B2.assets/image-20210713135820268.png" alt="image-20210713135820268" style="zoom:80%;" />

<font color=red>由于 statement 格式下，记录到 binlog 里的是语句原文</font>，因此可能会出现这样一种情况：==在主库执行这条 SQL 语句的时候，用的是索引 a；而在备库执行这条 SQL 语句的时候，却使用了索引 t_modified==。因此，MySQL 认为这样写是有风险的。

<font color=red>当 binlog_format 使用 row 格式的时候，binlog 里面记录了真实删除行的主键 id，这样 binlog 传到备库去的时候，就肯定会删除 id=4 的行，不会有主备删除不同行的问题</font>。

<img src="MySQL%E5%AE%9E%E6%88%9845%E8%AE%B2.assets/image-20210713141438198.png" alt="image-20210713141438198" style="zoom:80%;" />

因此，如果你的线上 MySQL 设置的 binlog 格式是 statement 的话，那基本上就可以认为这是一个不合理的设置。你<font color=red size=5>至少应该把 binlog 的格式设置为 mixed。</font>

<font color=red size=5>现在越来越多的场景要求把 MySQL 的 binlog 格式设置成 row。这么做的理由有很多，最直接的一个是：恢复数据。</font>

<font color=red size=5>用 binlog 来恢复数据的标准做法是，用 mysqlbinlog 工具解析出来</font>，然后把解析结果整个发给 MySQL 执行。类似下面的命令：

```sql

mysqlbinlog master.000001  --start-position=2738 --stop-position=2973 | mysql -h127.0.0.1 -P13000 -u$user -p$pwd;
```

这个命令的意思是，==将 master.000001 文件里面从第 2738 字节到第 2973 字节中间这段内容解析出来==，放到 MySQL 去执行。

##### 双M结构及其循环复制问题

<img src="MySQL%E5%AE%9E%E6%88%9845%E8%AE%B2.assets/image-20210713150225670.png" alt="image-20210713150225670" style="zoom:80%;" />

==节点 A 和 B 之间总是互为主备关系==。这样在切换的时候就==不用再修改主备关系==

业务逻辑在节点 A 上更新了一条语句，然后再把生成的 binlog 发给节点 B，节点 B 执行完这条更新语句后也会生成 binlog；节点 A 同时是节点 B 的备库，相当于又把节点 B 新生成的 binlog 拿过来执行了一次，然后节点 A 和 B 间，会不断地循环执行这个更新语句，也就是==循环复制==

解决办法：<font color=red size=5>server id</font>:

- 从节点 A 更新的事务，binlog 里面记的都是 A 的 server id；
- 传到节点 B 执行一次以后，==节点 B 生成的 binlog 的 server id 也是 A 的 server id==；
- ==再传回给节点 A，A 判断到这个 server id 与自己的相同==，就不会再处理这个日志。所以，死循环在这里就断掉了。

### 25. MySQL是怎么保证高可用的

<img src="MySQL%E5%AE%9E%E6%88%9845%E8%AE%B2.assets/image-20210713173206576.png" alt="image-20210713173206576" style="zoom:80%;" />

#### 主备延迟及其来源

“同步延迟”：

- 主库 ==A 执行完成==一个事务，写入 binlog，我们把这个时刻记为 ==T1==;
- 之后传给备库 B，我们把备库== B 接收完==这个 binlog 的时刻记为 ==T2;==
- 备库 ==B 执行完成==这个事务，我们把这个时刻记为 ==T3==。

所谓主备延迟，就是同一个事务，在备库执行完成的时间和主库执行完成的时间之间的差值，也就是 ==T3-T1。==

可以在备库上执行 `show slave status` 命令,它的返回结果里面会显示 ==seconds_behind_master==，用于表示==当前备库延迟了多少秒==。

- ==每个事务的 binlog 里面都有一个时间字段==，用于记录主库上写入的时间；

- ==备库取出当前正在执行的事务的时间字段的值==，计算它与当前系统时间的差值，得到 seconds_behind_master。

<img src="MySQL%E5%AE%9E%E6%88%9845%E8%AE%B2.assets/image-20210713203812813.png" alt="image-20210713203812813" style="zoom:80%;" />

<font color=red size=5>来源</font>

- <font color=red size=5>非对称部署</font>:有些部署条件下，备库所在机器的性能要比主库所在的机器性能差
- <font color=red size=5>备库压力大</font>：备库可以提供一些读能力。或者一些运营后台需要的分析语句，不能影响正常业务，备库上的查询耗费了大量的 CPU 资源
- <img src="MySQL%E5%AE%9E%E6%88%9845%E8%AE%B2.assets/image-20210713204739408.png" alt="image-20210713204739408" style="zoom:67%;" />
- <font color=red size=5>大事务</font>:主库上必须等事务执行完成才会写入 binlog，再传给备库



### 34. 到底可不可以使用join

### 39. 自增主键为什么不是连续的

自增主键可以让主键索引尽可能地保持递增顺序插入，避免了页分裂，因此索引更紧凑

```sql

CREATE TABLE `t` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `c` int(11) DEFAULT NULL,
  `d` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `c` (`c`)
) ENGINE=InnoDB;
```

#### 自增值保存在哪儿

在这个空表 t 里面执行 `insert into t values(null, 1, 1); `插入一行数据，再执行 `show create table` 命令，就可以看到如下图所示的结果：

<img src="MySQL%E5%AE%9E%E6%88%9845%E8%AE%B2.assets/image-20210712164920921.png" alt="image-20210712164920921" style="zoom:67%;" />

可以看到，表定义里面出现了一个 `AUTO_INCREMENT=2`，表示==下一次插入数据时，如果需要自动生成自增值，会生成 id=2。==

==不同的引擎对于自增值的保存策略不同。==

- MyISAM 引擎的==自增值保存在数据文件中==。

- ==InnoDB 引擎的自增值，其实是保存在了内存里==，并且到了 ==MySQL 8.0 版本后==，才有了==“自增值持久化”的能力==，也就是才实现了“如果发生重启，表的自增值可以恢复为 MySQL 重启前的值”，具体情况是：
  - ==在 MySQL 5.7 及之前的版本==，自增值保存在内存里，并没有持久化。每次重启后，第一次打开表的时候，都会去找自增值的最大值 max(id)，然后将 max(id)+1 作为这个表当前的自增值。﻿举例来说，如果一个表当前数据行里最大的 id 是 10，AUTO_INCREMENT=11。这时候，我们删除 id=10 的行，AUTO_INCREMENT 还是 11。但如果马上重启实例，重启后这个表的 AUTO_INCREMENT 就会变成 10。﻿也就是说，MySQL 重启可能会修改一个表的 AUTO_INCREMENT 的值。
  - ==在 MySQL 8.0 版本==，将自增值的变更记录在了 redo log 中，重启的时候依靠 redo log 恢复重启之前的值。

#### 自增值修改机制

在 MySQL 里面，如果字段 id 被定义为 AUTO_INCREMENT，在插入一行数据的时候，自增值的行为如下：

1. 如果插入数据时 id 字段指定为 ==0、null 或未指定值==，那么就把这个表当前的==AUTO_INCREMENT 值填到自增字段==；
2. 如果插入数据时 id 字段==指定了具体的值==，就直接使用语句里指定的值

根据要==插入的值==和==当前自增值的大小关系==，自增值的变更结果也会有所不同。

假设，某次要插入的值是 X，当前的自增值是 Y。

- 如果 X<Y，那么这个表的自增值不变；
- 如果 X≥Y，就需要把当前自增值修改为新的自增值。

<img src="MySQL%E5%AE%9E%E6%88%9845%E8%AE%B2.assets/image-20210712194906004.png" alt="image-20210712194906004" style="zoom: 67%;" />

#### 自增值的修改时机

假设，表 t 里面已经有了 (1,1,1) 这条记录，这时我再执行一条插入数据命令：

```sql

insert into t values(null, 1, 1); 
```

<img src="MySQL%E5%AE%9E%E6%88%9845%E8%AE%B2.assets/image-20210712201720452.png" alt="image-20210712201720452" style="zoom: 67%;" />

这个语句的执行流程就是：

- 执行器调用 InnoDB 引擎接口写入一行，传入的这一行的值是 (0,1,1);

- InnoDB 发现用户没有指定自增 id 的值，获取表 t 当前的自增值 2；

- 将传入的行的值改成 (2,1,1);将表的自增值改成 3；

- 继续执行插入数据操作，由于==已经存在 c=1 (c的unique key属性)的记录==，所以报 ==Duplicate key error==，语句返回。

可以看到，==这个表的自增值改成 3，是在真正执行插入数据的操作之前==。这个语句真正执行的时候，因为碰到唯一键 c 冲突，所以 ==<font color=red>id=2 这一行并没有插入成功，但也没有将自增值再改回去</font>==。所以，在这之后，==再插入新的数据行时，拿到的自增 id 就是 3==。也就是说，<font color=red>出现了自增主键不连续的情况</font>。

可见<font color=red size=5>唯一键冲突是导致自增主键id不连续的第一种原因</font>

<font color=red size=5>事务回滚是是自增主键id不连续的第二种原因</font>

```sql

insert into t values(null,1,1);
begin;
insert into t values(null,2,2);
rollback;
insert into t values(null,2,2);
//插入的行是(3,2,2)
```

#### 自增值为什么不能回退

<img src="MySQL%E5%AE%9E%E6%88%9845%E8%AE%B2.assets/image-20210712203010241.png" alt="image-20210712203010241" style="zoom:67%;" />

<img src="MySQL%E5%AE%9E%E6%88%9845%E8%AE%B2.assets/image-20210712203108049.png" alt="image-20210712203108049" style="zoom:80%;" />

所以，<font color=red size=5>自增id保证递增，不保证连续</font>

#### 自增锁的优化

==自增id锁并不是事务锁==，而是==每次申请完就马上释放==，以便允许别的事务再申请

- 在MySQL 5.0 版本的时候，自增锁的范围是==语句级别==。也就是说，如果一个语句申请了一个表自增锁，这个==<font color=red>锁会等语句执行结束以后才释放</font>==。显然，这样设计会影响并发度。也就是各种insert操作申请的==自增锁会在自增语句执行完后释放==
- MySQL 5.1.22 版本引入了一个新策略，新增参数 `innodb_autoinc_lock_mode`，默认值是 1。
- - 
  - 这个参数的值被设置为 0 时，表示采用之前 MySQL 5.0 版本的策略，即语句执行结束后才释放锁；
  - 这个参数的值被设置为==1== 时：
  - - 普通 insert 语句，自增锁在申请之后就马上释放；
    - 类似 insert … select 这样的==批量插入数据的语句==，自增锁还是要等语句结束后才被释放；
  - 这个参数的值被设置为 ==2==时，所有的申请自增主键的动作都是==申请后就释放锁==。

略吧。。。。



### 41.如何最快的复制一张表

### 45. 自增id用完了怎么办

表定义的自增值达到上限后的逻辑是：<font color=red>再申请下一个 id 时，得到的值保持不变</font>

```sql

create table t(id int unsigned auto_increment primary key) auto_increment=4294967295;
insert into t values(null);
//成功插入一行 4294967295
show create table t;
/* CREATE TABLE `t` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4294967295;
*/

insert into t values(null);
//Duplicate entry '4294967295' for key 'PRIMARY'
```

可以看到，第一个 insert 语句插入数据成功后，这个表的 AUTO_INCREMENT 没有改变（还是 4294967295），就导致了第二个 insert 语句又拿到相同的自增 id 值，再试图执行插入语句，报主键冲突错误。

2（32）-1（4294967295）不是一个特别大的数，对于一个频繁插入删除数据的表来说，是可能会被用完的。因此在建表的时候你需要考察你的表==是否有可能达到这个上限==，如果有可能，==<font color=red size=5>就应该创建成 8 个字节的 `bigint unsigned。`</font>==

#### InnoDB系统自增row_id

如果你创建的<font color=red> InnoDB 表没有指定主键</font>，那么 InnoDB 会给你创建一个不可见的，长度为<font color=red> 6 个字节的 row_id</font>。

InnoDB 维护了一个<font color=red>全局的 dict_sys.row_id 值</font>，所有无主键的 InnoDB 表，每插入一行数据，都将当前的 dict_sys.row_id 值作为要插入数据的 row_id，然后把 dict_sys.row_id 的值加 1。

实际上，在代码实现时 row_id 是一个长度为 8 字节的无符号长整型 (bigint unsigned)。但是，InnoDB 在设计时，给 row_id 留的只是 6 个字节的长度，这样写到数据表中时只放了最后 6 个字节，所以 row_id 能写到数据表中的值，就有两个特征：

<img src="MySQL%E5%AE%9E%E6%88%9845%E8%AE%B2.assets/image-20210712221932848.png" alt="image-20210712221932848" style="zoom:67%;" />

<font color=red>也就是，用完自增的row_id之后，就会从头覆盖原有的行</font>

<font color=red size=5>所以一定要有一个主键</font>

#### Xid:事务id

之后略吧





