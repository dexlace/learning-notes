package com.scct.zookeeper.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/2/27
 */

@Slf4j
public class MyZkConnect {

    //集群节点
    public static final String zkServerClusterConnect = "192.168.205.100:2181,192.168.205.101:2181,192.168.205.102:2181";

    //单一节点
    public static final String zkServerSingleConnect = "192.168.205.100:2181";

    //超时毫秒数
    public static final int timeout = 3000;

    public static void main(String[] args) throws InterruptedException, IOException, KeeperException {
        //建立连接
        ZooKeeper zk = connect();
        //zk.close();//关闭后不支持重连
        log.info("zk 状态："+zk.getState());

        /**恢复会话连接**/
        //long sessionId = zk.getSessionId();
        //byte[] sessionPasswd = zk.getSessionPasswd();
        //zk2会话重连后，zk会话将失效，不再支持做增删改查操作。
        //ZooKeeper zk2 = reconnect(sessionId, sessionPasswd);

        /**创建节点**/
//        create(zk, "/myzk", "myzk");
//        create(zk, "/create/node1", "node1");
//        create(zk, "/create/node2", "node2");
//        create(zk, "/create/node3", "node3");
//        create(zk, "/create/node4", "node4");
//        create(zk, "/create/node5", "node5");
//        create(zk, "/create/node6", "node6");
      //  create(zk, "/create/node7", "node7");

        /**查询节点Data**/
        //queryData(zk, "/myzk");

        /**修改节点data**/
//        update(zk, "/myzk", "myzk-update");

        /**删除节点**/
        delete(zk, "/myzk");
    }


    /**
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static ZooKeeper connect() throws IOException, InterruptedException{
        CountDownLatch cdl = new CountDownLatch(1);
        log.info("准备建立zk服务");
        ZooKeeper zk = new ZooKeeper(zkServerClusterConnect, timeout, new MyZkWatcher(cdl,"建立连接"));
        log.info("完成建立zk服务");
        cdl.await();//这里为了等待wather监听事件结束
        return zk;
    }



    /**
     * 描述：重新连接服务
     * 作者：七脉
     * @param sessionId 现有会话ID
     * @param sessionPasswd 现有会话密码
     * @return
     * @throws IOException
     * @throws InterruptedException
     * 重点：关闭后的会话连接，不支持重连。重连后，前会话连接将会失效。
     */
    public static ZooKeeper reconnect(long sessionId, byte[] sessionPasswd) throws IOException, InterruptedException{
        CountDownLatch cdl = new CountDownLatch(1);
        log.info("准备重新连接zk服务");
        ZooKeeper zk = new ZooKeeper(zkServerClusterConnect, timeout, new MyZkWatcher(cdl,"重新连接"), sessionId, sessionPasswd);
        log.info("完成重新连接zk服务");
        cdl.await();//这里为了等待wather监听事件结束
        return zk;
    }

    /**
     * 描述：创建节点
     * 作者：七脉
     * @param zk
     * @param nodePath
     * @param nodeData
     * @throws KeeperException
     * @throws InterruptedException
     */
    public static void create(ZooKeeper zk,String nodePath,String nodeData) throws KeeperException, InterruptedException{
        log.info("开始创建节点：{}， 数据：{}",nodePath,nodeData);
        // 1、授权模式是world
        // 权限列表
//        List<ACL> acls=new ArrayList<ACL>();
//        Id id=new Id("world","anyone");
//        // 权限设置
//        acls.add(new ACL(ZooDefs.Perms.READ,id));
//        acls.add(new ACL(ZooDefs.Perms.WRITE,id));

        // world权限模式，对象是anyone，默认所有权限
//        List<ACL> acl = ZooDefs.Ids.OPEN_ACL_UNSAFE;
        // 相当于以上的授权模式，但是权限是所有权限




//       // 2、ip授权模式
//         // 权限列表
//        List<ACL> acls = new ArrayList<ACL>();
//        // 授权模式和授权对象
//        // 注意登录的特定的服务器的命令是zkCli.sh -server ip地址
//        Id id = new Id("ip", "192.168.205.101");
//        // 权限设置
//         acls.add(new ACL(ZooDefs.Perms.ALL, id));



        // 3. auth授权模式
        // 添加授权用户
        // 注意进入验证的命令是：addauth digest Judy:ILoveU
//        zk.addAuthInfo("digest","Judy:ILoveU".getBytes());
//        zk.addAuthInfo("digest","ZhangSan:IHateU".getBytes());
//        List<ACL> acls=new ArrayList<ACL>();
//        Id id=new Id("auth","ZhangSan");
//        acls.add(new ACL(ZooDefs.Perms.READ,id));

        // 4. digest授权模式
        // digest授权模式
        // 权限列表
//        List<ACL> acls = new ArrayList<ACL>();
//        // 授权模式和授权对象
//        Id id = new Id("digest", "itheima:qlzQzCLKhBROghkooLvb+Mlwv4A=");
//        // 权限设置
//        acls.add(new ACL(ZooDefs.Perms.ALL, id));
//        zk.create("/create/node7", "node7".getBytes(), acls, CreateMode.PERSISTENT);


        // 5. 持久化顺序节点
//        String result = zk.create(nodePath, nodeData.getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
        // 6.临时节点
//        String result = zk.create(nodePath, nodeData.getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        // 7. 临时顺序节点
        String result = zk.create(nodePath, nodeData.getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

//        String result = zk.create(nodePath, nodeData.getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL, CreateMode.PERSISTENT);
//        String result = zk.create(nodePath, nodeData.getBytes(),acls, CreateMode.PERSISTENT);
        log.info("创建节点返回结果：{}",result);
        log.info("完成创建节点：{}， 数据：{}",nodePath,nodeData);
    }

    /**
     * 描述：查询节点结构信息
     * 作者：七脉
     * @param zk
     * @param nodePath
     * @return
     * @throws KeeperException
     * @throws InterruptedException
     */
    public static Stat queryStat(ZooKeeper zk,String nodePath) throws KeeperException, InterruptedException{
        log.info("准备查询节点Stat，path：{}", nodePath);
        Stat stat = zk.exists(nodePath, false);
        log.info("结束查询节点Stat，path：{}，version：{}", nodePath, stat.getVersion());
        return stat;
    }

    /**
     * 描述：查询节点Data值信息
     * 作者：七脉
     * @param zk
     * @param nodePath
     * @return
     * @throws KeeperException
     * @throws InterruptedException
     */
    public static String queryData(ZooKeeper zk,String nodePath) throws KeeperException, InterruptedException{
        log.info("准备查询节点Data,path：{}", nodePath);
        String data = new String(zk.getData(nodePath, false, queryStat(zk, nodePath)));
        log.info("结束查询节点Data,path：{}，Data：{}", nodePath, data);
        return data;
    }


    /**
     * 描述：修改节点
     * 作者：七脉
     * @param zk
     * @param nodePath
     * @param nodeData
     * @throws KeeperException
     * @throws InterruptedException
     * 重点：每次修改节点的version版本号都会变更，所以每次修改都需要传递节点原版本号，以确保数据的安全性。
     */
    public static Stat update(ZooKeeper zk, String nodePath, String nodeData) throws KeeperException, InterruptedException{
        //修改节点前先查询该节点信息
        Stat stat = queryStat(zk, nodePath);
        log.info("准备修改节点，path：{}，data：{}，原version：{}", nodePath, nodeData, stat.getVersion());
        Stat newStat = zk.setData(nodePath, nodeData.getBytes(), stat.getVersion());
        //修改节点值有两种方法，上面是第一种，还有一种可以使用回调函数及参数传递，与上面方法名称相同。
        //zk.setData(path, data, version, cb, ctx);
        log.info("完成修改节点，path：{}，data：{}，现version：{}", nodePath, nodeData, newStat.getVersion());
        return stat;
    }

    /**
     * 描述：删除节点
     * 作者：七脉
     * @param zk
     * @param nodePath
     * @throws InterruptedException
     * @throws KeeperException
     */
    public static void delete(ZooKeeper zk,String nodePath) throws InterruptedException, KeeperException{
        //删除节点前先查询该节点信息
        Stat stat = queryStat(zk, nodePath);
        log.info("准备删除节点，path：{}，原version：{}", nodePath, stat.getVersion());
        zk.delete(nodePath, stat.getVersion());
        //修改节点值有两种方法，上面是第一种，还有一种可以使用回调函数及参数传递，与上面方法名称相同。
        //zk.delete(path, version, cb, ctx);
        log.info("完成删除节点，path：{}", nodePath);
    }

}
