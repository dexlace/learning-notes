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

## DAY2

### 1. 当选者

leetcode-574

<img src="SQL%E7%BB%83%E4%B9%A0.assets/image-20211105002556733.png" alt="image-20211105002556733" style="zoom:67%;" />

编写sql语句找到当选者的名字

首先要找到==当选者最多的id==

```sql
  select CandidateId as id
  from Vote
  group by CandidateId
  order by count(id) desc
  limit 1
```

确定和Name所在的表的交集关系，所以是==内联结==，以至于最终的结果应该是：

```sql
select Name
from Candidate join(
  select CandidateId as id
  from Vote
  group by CandidateId
  order by count(id) desc
  limit 1
) as Winner 
on Winner.id = Candidate.id
```

我的问题在于：==`group by`用的不熟，聚合后排序这种用法就是更没接触过==

### 2. 员工奖金

leetcode-577

<img src="SQL%E7%BB%83%E4%B9%A0.assets/image-20211105004923723.png" alt="image-20211105004923723" style="zoom:67%;" />

<img src="SQL%E7%BB%83%E4%B9%A0.assets/image-20211105005001459.png" alt="image-20211105005001459" style="zoom: 67%;" />

**以下错误答案**，使用的是交集，属于==一种内联==，但其实用的是==外联==

```sql
select e.name,b.bonus from
Employee e, (
    select empId, bonus from Bonus 
    where bonus<1000 or bonus is null

)as b where e.empId=b.empId
```

**正确答案**

```sql
select e.name,b.bonus from
Employee e left join Bonus b
on e.empId=b.empId where b.bonus<1000 or b.bonus is null
```

### 3. 统计各个专业的学生人数

将你的查询结果==按照学生人数降序排列==。 如果==有两个或两个以上专业有相同的学生数目==，将这些专业==按照专业名字的字典序==从小到大排列。

<img src="SQL%E7%BB%83%E4%B9%A0.assets/image-20211105010113032.png" alt="image-20211105010113032" style="zoom:67%;" />

<img src="SQL%E7%BB%83%E4%B9%A0.assets/image-20211105010138537.png" alt="image-20211105010138537" style="zoom:67%;" />

<img src="SQL%E7%BB%83%E4%B9%A0.assets/image-20211105010138537.png" alt="image-20211105010138537" style="zoom:67%;" /<img src="SQL%E7%BB%83%E4%B9%A0.assets/image-20211105012744119.png" alt="image-20211105012744119" style="zoom:67%;" />



一步步解析，首先查的是`dept_name`和`student_number`,分别来自两个表

先写select啥啥啥吧

```sql
SELECT
    dept_name, COUNT(*) AS student_number
```

from

```sql
department
LEFT OUTER JOIN student 
```

条件

```sql
ON department.dept_id = student.dept_id
```

分组和排序

```sql
  GROUP BY department.dept_name -- 必须先聚合 不去count聚合之前的
  ORDER BY student_number DESC , department.dept_name -- 按照数字和名字分别排序
```

==总体sql即==

```sql
SELECT
    dept_name, COUNT(*) AS student_number FROM department
LEFT OUTER JOIN student 
    ON department.dept_id = student.dept_id
    GROUP BY department.dept_name
    ORDER BY student_number DESC , department.dept_name

```

但是count(*)会对不存在数据返回1，实际上，我们可以使用 `COUNT(expression)` 语句，因为如果 `expression is null`，那么这条记录不会被计数。

所以正确答案应该是

```sql
SELECT
    dept_name, COUNT(student_id) AS student_number FROM department
LEFT OUTER JOIN student 
ON department.dept_id = student.dept_id
GROUP BY department.dept_name
ORDER BY student_number DESC , department.dept_name
;

```

### 4. 订单数最多的用客户

leetcode-586

<img src="SQL%E7%BB%83%E4%B9%A0.assets/image-20211105014334019.png" alt="image-20211105014334019" style="zoom:67%;" />

```sql
select customer_number from orders
group by customer_number
order by count(customer_number) desc
limit 1
```

和当选者那题部分逻辑一致

### 5. 树节点

leetcode-608

给定一个表 tree，id 是树节点的编号， p_id 是它父节点的 id 。

+----+------+
| id | p_id |
+----+------+
| 1  | null |
| 2  | 1    |
| 3  | 1    |
| 4  | 2    |
| 5  | 2    |
+----+------+
树中每个节点属于以下三种类型之一：

叶子：如果这个节点没有任何孩子节点。
根：如果这个节点是整棵树的根，即没有父节点。
内部节点：如果这个节点既不是叶子节点也不是根节点。


写一个查询语句，输出所有节点的编号和节点的类型，并将结果按照节点编号排序。上面样例的结果为：

 

+----+------+
| id | Type |
+----+------+
| 1  | Root |
| 2  | Inner|
| 3  | Leaf |
| 4  | Leaf |
| 5  | Leaf |
+----+------+

