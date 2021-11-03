# SQL练习

期限30天，开了leetcode会员，为了把200道SQL题刷完

还有一定要列出多种解法和垃圾的我不会想到的地方

这里等于是最细最细的解析，不怕笑话，本来就不是DBA

## DAY1

<img src="SQL%E7%BB%83%E4%B9%A0.assets/image-20211103223157579.png" alt="image-20211103223157579" style="zoom:80%;" />

### 1. 游戏玩法分析I

leetcode-511

标签：==字节跳动==

> 表的主键是 (player_id, event_date)。
> 这张表展示了一些游戏玩家在游戏平台上的行为活动。
> 每行数据记录了一名**玩家**在退出平台之前，**当天**使用同一台**设备**登录平台后打开的**游戏的数目**（可能是 0 个）。

写一条 SQL 查询语句获取每位玩家 **第一次登陆平台的日期**。

查询结果如下：

<img src="SQL%E7%BB%83%E4%B9%A0.assets/image-20211103223419667.png" alt="image-20211103223419667" style="zoom:80%;" />

1. 首先明确要查的字段，`player_id`和`first_login`,其中`first_login`必然对应的是表的字段`event_date`,所以有==**别名的操作**==
2. 第一次登陆平台的日期，必然是存在该用户的最小日期，所以==**可以用min函数**==
3. 如何使用min函数，应该匹配`group by`，==毕竟是指定用户的最小日期==，而不是别的

```sql
select player_id, min(event_date) as first_login from
Activity
group by player_id
```

```sql
select player_id, min(event_date) first_login
from Activity
group by player_id;
```

#### 1.1 如何使用as

以上都行，但如何使用as呢

尴尬，**as只是可以省略而已**，意义都一样的，马德，丢人

### 2. 游戏玩法分析II

leetcode-512

> 使用Activity表，同上

请编写一个 SQL 查询，描述每一个玩家**首次登陆的设备名称**

<img src="SQL%E7%BB%83%E4%B9%A0.assets/image-20211103233141051.png" alt="image-20211103233141051" style="zoom:80%;" />

1. 找==选手最早的时间==
2. 根据==上面的查找结果，找设备==

```sql
select player_id, device_id from Activity 
where (player_id, event_date) 
    IN
    (
        SELECT player_id, min(event_date)
        FROM Activity
        GROUP BY player_id
    )
```

#### 2.1 如何使用in

我估计只是看过一个`filed in`一群`value`中的场景，殊不知`多个filed in对应一群value的场景`,也是一样，丢人。

### 3. 游戏玩法分析III

leetcode-534

同样使用上述表，不过意义重新阐释如下

> （player_id，event_date）是此表的主键。
> 这张表显示了某些游戏的玩家的活动情况。
> 每一行是一个玩家的记录，他在某一天使用某个设备注销之前登录并玩了很多游戏（可能是 0 ）。
>

编写一个 SQL 查询，同时报告==每组玩家和日期，以及玩家到目前为止玩了多少游戏==。也就是说，在此日期之前玩家所玩的游戏总数。详细情况请查看示例。

<img src="SQL%E7%BB%83%E4%B9%A0.assets/image-20211103235117226.png" alt="image-20211103235117226" style="zoom:67%;" />

马德，思路肯定是一张表查`player_id`和`event_date`,但是每个日期前需要==**统计该日期前的打游戏次数，一张表怎么够**==

这里有个关键是==自联结==

#### 3.1 什么叫自联结

就是==一张表当两张表用==，其他内联结或者外联结都是不同表，这就是他们的区别之处

首先,先自联结

```sql
select a.player_id,a.event_date, --缺失查询字段待续
from Activity a,Activity b
where a.player_id =b.player_id
```

再就是确认，==次数去哪里拿==，必然是第二张表，==条件==是

```sql
a.event_date>=b.event_date
```

结果是 `sum(b.games_played_so_far)`

最后是一些==分组条件==

所以最终的结果是

```sql
select a.player_id,a.event_date,sum(b.games_played) games_played_so_far
from Activity a,Activity b
where a.player_id =b.player_id 
and a.event_date>=b.event_date
group by a.player_id,a.event_date
```

### 4. 游戏玩法分析IV

leetcode-550

编写一个 SQL 查询，报告在首次登录的==第二天再次登录的玩家的比率==，四舍五入到小数点后两位。换句话说，您需要计算从==首次登录日期开始至少连续两天登录的玩家的数量==，然后==除以玩家总数==。

思路：

1. 首先肯定是要找出玩家`首次登录的时间和id`

```sql
--1.所有玩家首次登录的时间及ID
SELECT player_id, MIN(event_date) event_date
FROM Activity
GROUP BY player_id;

```

2. 找出第二天也登录的数量，要在第一步的基础上，==是个交集，所以用内联结==

```sql
-- 2.首次登陆之后第二天也登录的玩家数量
SELECT COUNT(*) replay_num
FROM Activity a
JOIN (
    SELECT player_id, MIN(event_date) event_date
    FROM Activity
    GROUP BY player_id
) b ON a.player_id=b.player_id AND DATEDIFF(a.event_date,b.event_date)=1
```

3. 求出玩家总数，没啥说的，统计`player_id`注意去重就行

```sql
SELECT COUNT(DISTINCT player_id) total_num
FROM Activity
```

4. 用==round函数求解==

**所以最终结果：**

```sql

select
    round(part.replay_num/total.total_num,2) as 'fraction'
 -- 连接连续登录人数   
from(
    SELECT COUNT(*) replay_num
    FROM Activity a
    JOIN (
        SELECT player_id, MIN(event_date) event_date
        FROM Activity
        GROUP BY player_id
    ) b ON a.player_id=b.player_id AND DATEDIFF(a.event_date,b.event_date)=1
) as part,
-- 连接总人数
(SELECT COUNT(DISTINCT player_id) total_num
FROM Activity) as total
```

关于求连续登陆的人数，还是==可以用自联结==，毕竟内联结只是当外表使用而已

```sql
-- 2.首次登陆之后第二天也登录的玩家数量
SELECT COUNT(*) replay_num
FROM Activity a,
(
    SELECT player_id, MIN(event_date) event_date
    FROM Activity
    GROUP BY player_id
) b where a.player_id=b.player_id AND DATEDIFF(a.event_date,b.event_date)=1
```

所以最终结果也可以是

```sql

select
    round(part.replay_num/total.total_num,2) as 'fraction'
 -- 连接连续登录人数   
from(
   SELECT COUNT(*) replay_num
    FROM Activity a,
    (
        SELECT player_id, MIN(event_date) event_date
        FROM Activity
        GROUP BY player_id
    ) b where a.player_id=b.player_id AND DATEDIFF(a.event_date,b.event_date)=1
) as part,
-- 连接总人数
(SELECT COUNT(DISTINCT player_id) total_num
FROM Activity) as total
```

太有意思了，这一题，我从来查过这种东西

### 5. 至少有5名直接下属的经理

leetcode-570

<img src="SQL%E7%BB%83%E4%B9%A0.assets/image-20211104011404077.png" alt="image-20211104011404077" style="zoom:67%;" />

思路：

1. 先将经理id出现次数大于5次的找出来
2. 内联一次表查name

```sql

SELECT e2.Name 
FROM 
    Employee AS e2
    INNER JOIN
        (
            SELECT e1.ManagerId     --先把经理id都找出来
            FROM Employee AS e1
            GROUP BY e1.ManagerId
            HAVING COUNT(*) >= 5
        ) AS tmp1
    ON e2.id = tmp1.ManagerId
;

```

注意别忘了`group by`

#### 5.1 怎么使用having

having字句可以让我们==**筛选成组合的各种数据**==，where字句在==聚合前先筛选记录==，也就是说作用在group by和having字句前。而 ==having子句在聚合后对组记录进行筛选==

![image-20211104013013058](SQL%E7%BB%83%E4%B9%A0.assets/image-20211104013013058.png)

![image-20211104013102472](SQL%E7%BB%83%E4%B9%A0.assets/image-20211104013102472.png)