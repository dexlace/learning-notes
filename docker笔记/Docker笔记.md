# Docker

弱小和无知不是生存的障碍，傲慢才是。

## 一、简介



## 二、Docker安装（Centos7）

https://docs.docker-cn.com/engine/installation/linux/docker-ce/centos/#prerequisites

```bash
# 1.安装gcc相关
yum -y install gcc
yum -y install gcc-c++
# 2.卸载旧版本
yum -y remove docker docker-common docker-selinux docker-engine
# 3.安装依赖
yum install -y yum-utils device-mapper-persistent-data lvm2
# 4.添加镜像仓库
  # 官方仓库
  yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
  # 或者阿里云仓库
  yum-config-manager --add-repo http://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo
# 5.更新yum软件包索引
  yum makecache fast
# 6.安装docker社区版
  yum -y install docker-ce
# 7.启动
  systemctl start docker
# 8.测试
  docker version
  docker run hello-world

```

配置镜像加速

```bash
sudo mkdir -p /etc/docker
sudo tee /etc/docker/daemon.json <<-'EOF'
{
  "registry-mirrors": ["https://k5c60z38.mirror.aliyuncs.com"]
}
EOF
sudo systemctl daemon-reload
sudo systemctl restart docker
```

卸载

```bash
systemctl stop docker 
yum -y remove docker-ce
rm -rf /var/lib/docker
```

## 三、 Docker常用命令

### 1. 帮助命令

```bash
docker version
docker info
docker --help
```

### 2. 镜像命令

#### 2.1 列出本机镜像

```bash
docker images # 列出本机的镜像
```

<img src="Docker%E7%AC%94%E8%AE%B0.assets/image-20200826224814222.png" alt="image-20200826224814222" style="zoom:80%;" />

各个选项说明:REPOSITORY：表示镜像的仓库源

TAG：镜像的标签

IMAGE ID：镜像ID

CREATED：镜像创建时间

SIZE：镜像大小

TAG: 代表这个仓库源的不同个版本，我们使用 REPOSITORY:TAG 来定义不同的镜像。

如果你不指定一个镜像的版本标签，例如你只使用 ubuntu，docker 将默认使用 ubuntu:latest 镜像

```bash
docker images  [options] IMAGE #IMAGE表示指定镜像 否则是所有
        -a :列出本地所有的镜像（含中间映像层）
        -q :只显示镜像ID。
        --digests :显示镜像的摘要信息
        --no-trunc :显示完整的镜像信息
```

#### 2.2 查找镜像

```bash
docker search 某个XXX镜像名字
# 或者
docker search [OPTIONS] 镜像名字
              --no-trunc : 显示完整的镜像描述
              -s : 列出收藏数不小于指定值的镜像。
              --automated : 只列出 automated build类型的镜像；            
```

<img src="Docker%E7%AC%94%E8%AE%B0.assets/image-20200826225603751.png" alt="image-20200826225603751" style="zoom:80%;" />

#### 2.3 拉取镜像

- 普通拉取

```bash
docker pull 某个XXX镜像名字 # 默认拉取latest版本
```

- 指定tag

```bash
docker pull 某个镜像名字:tag # 拉取指定tag版本
```

#### 2.4 删除镜像

```bash
docker rmi  -f 镜像ID

docker rmi -f 镜像名1:TAG 镜像名2:TAG 

docker rmi -f $(docker images -qa)
```

### 3. 容器命令

<font color=red>有镜像才能启动容器</font>

#### 3.1 创建并启动容器

```bash
docker run [OPTIONS] IMAGE [COMMAND] [ARG...]

# [OPTIONS]
-i：以交互模式运行容器，通常与 -t 同时使用；
-t：为容器重新分配一个伪输入终端，通常与 -i 同时使用；

# 例如
# 使用镜像centos:latest以交互模式启动一个容器,在容器内执行/bin/bash命令。
docker run -it centos /bin/bash 
 
## 以上常用
--name="容器新名字": 为容器指定一个名称；
## 注意-d
-d: 后台运行容器，并返回容器ID，也即启动守护式容器；
-P: 随机端口映射；
-p: 指定端口映射，有以下四种格式
      ip:hostPort:containerPort
      ip::containerPort
      hostPort:containerPort
      containerPort
 # [COMMAND] [ARG...]
 # 即该镜像可以运行的命令及其参数
```

#### 3.2 列出当前正在运行的容器

```bash
docker ps [OPTIONS]
# [OPTIONS]
-a :列出当前所有正在运行的容器+历史上运行过的
-l :显示最近创建的容器。
-n：显示最近n个创建的容器。
-q :静默模式，只显示容器编号。
--no-trunc :不截断输出。
```

#### 3.3 退出容器

```bash
exit #退出，在退出之后会关闭容器
```

```bash
Ctrl+P+Q #快捷键，仅退出容器，不关闭容器
```

#### 3.4 启动、重启、停止、强制停止容器

```bash
docker start 容器ID或者容器名
```

```bash
docker restart 容器ID或者容器名
```

```bash
docker stop 容器ID或者容器名
```

```bash
docker kill 容器ID或者容器名
```

#### 3.5 启动守护式容器

```bash
docker run -d 容器名
```

使用镜像centos:latest以后台模式启动一个容器docker run -d centos问题：然后<font color=red>docker ps -a</font> 进行查看, 会发现<font color=red>容器已经退出</font>

说明的一点: <font color=red>Docker容器后台运行,就必须有一个前台进程</font>.容器运行的命令如果<font color=red>不是那些一直挂起的命令</font>（比如运行top，tail），就是会

自动退出的。这个是docker的机制问题,比如你的web容器,我们以nginx为例，正常情况下,我们配置启动服务只需要启动响应的service即

可。例如service nginx start但是,这样做,nginx为后台进程模式运行,就导致<font color=red>docker前台没有运行的应用,这样的容器后台启动后,会立即自</font>

杀因为他觉得他没事可做了.所以，最佳的解决方案是,<font color=red>将你要运行的程序以前台进程的形式运行</font>

#### 3.6 查看容器日志

```bash
docker logs [OPTIONS:-f -t --tail] 容器ID
     -t 是加入时间戳
     -f 跟随最新的日志打印
     --tail 数字 显示最后多少条
```

#### 3.7 查看容器内运行的进程

```bash
docker top 容器ID
```

#### 3.8 查看容器内部细节

```bash
docker inspect 容器ID
```

#### 3.9 进入正在运行的容器并以命令行交互

```
docker exec -it 容器ID bashShell # 是在容器中打开新的终端，并且可以启动新的进程
```

```bash
docker attach 容器ID # attach 直接进入容器启动命令的终端，不会启动新的进程
```

#### 3.10 从容器内拷贝文件到主机上

```bash
docker cp  容器ID:容器内路径 目的主机路径
```

## 四、 Docker镜像

**<font color=red>是一个UnionFS（联合文件系统）</font>**

UnionFS（联合文件系统）：Union文件系统（UnionFS）是一种<font color=red>分层、轻量级并且高性能的文件系统</font>，它支持对文件系统的修改作为一

次提交来<font color=red>一层层的叠加</font>，同时可以将<font color=red>不同目录挂载到同一个虚拟文件系统</font>下(unite several directories into a single virtual filesystem)。

Union 文件系统是 Docker 镜像的基础。镜像可以通过<font color=red>分层来进行继承，基于基础镜像（没有父镜像）</font>，可以制作各种具体的应用镜像。

特性：<font color=red>一次同时加载多个文件系统，但从外面看起来，只能看到一个文件系统</font>，联合加载会把各层文件系统叠加起来，这样最终的文件系

统会包含所有底层的文件和目录

<font color=red> **Docker镜像加载原理：**</font>

   docker的镜像实际上由一层一层的文件系统组成，这种层级的文件系统UnionFS。

<font color=blue>**bootfs(boot file system)**</font>主要包含**<font color=pink>bootloader和kernel</font>**, bootloader主要是<font color=red>引导加载kernel</font>, Linux刚启动时会加载bootfs文件系统，在

Docker镜像的最底层是bootfs。这一层与我们典型的Linux/Unix系统是一样的，包含boot加载器和内核。<font color=red>当boot加载完成之后整个内核</font>

<font color=red>就都在内存中了</font>，此时内存的使用权已由bootfs转交给内核，**此时系统也会卸载bootfs**。

<font color=blue>**rootfs (root file system)**</font> ，在bootfs之上。包含的就是典型 Linux 系统中的 /dev, /proc, /bin, /etc 等标准目录和文件。<font color=red>rootfs就是各种</font>,

<font color=red>不同的操作系统发行版</font>，比如Ubuntu，Centos等等。 

 平时我们安装进虚拟机的CentOS都是好几个G，为什么docker这里才200M？？

对于一个精简的OS，<font color=blue>**rootfs可以很小，只需要包括最基本的命令、工具和程序库就可以了**</font>，因为<font color=blue>**底层直接用Host的kernel，自己只需要**</font>

<font color=blue>**提供 rootfs 就行了**</font>。由此可见对于不同的linux发行版, bootfs基本是一致的, rootfs会有差别, 因此不同的发行版可以公用bootfs。

<font color=red>**分层的镜像**</font>

以我们的pull为例，在下载的过程中我们可以看到docker的镜像**好像是在一层一层的在下载**


最大的一个好处就是 - 共享资源

比如：有多个镜像都从相同的 base 镜像构建而来，那么宿主机**只需在磁盘上保存一份base镜像，**

同时**内存中也只需加载一份 base 镜像**，就可以为所有容器服务了。而且镜像的每一层都可以被共享。

## 五、 Docker容器数据卷



## 六、 DockerFile解析



## 七、 Docker常用安装



## 八、 本地镜像发布到阿里云



