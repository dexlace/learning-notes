# Docker

==弱小和无知不是生存的障碍，傲慢才是。==

## 一、简介

==软件带环境安装==

Docker镜像的设计，使得Docker得以打破过去「程序即应用」的观念。透过镜像(images)将作业系统核心除外，运作应用程式所需要的系统环境，由下而上打包，达到应用程式跨平台间的无缝接轨运作。

Docker是==基于Go语言实现的云开源项目==。

Docker的主要目标是==“Build，Ship and Run Any App,Anywhere”==，也就是通过对应用组件的封装、分发、部署、

运行等生命周期的管理，使用户的APP（可以是一个WEB应用或数据库应用等等）及其运行环境能够做到==一次封装，到处运行==

==Linux 容器技术==的出现就解决了这样一个问题，而 Docker 就是在它的基础上发展过来的。将应用运行在 Docker 

容器上面，而 Docker 容器在任何操作系统上都是一致的，这就实现了跨平台、跨服务器。只需要一次配置好环

境，换到别的机子上就可以一键部署好，大大简化了操作

> <font color=red>与虚拟机的区别</font>

虚拟机（virtual machine）就是==带环境安装的一种解决方案。==

它可以在一种操作系统里面运行另一种操作系统，比如在Windows 系统里面运行Linux 系统。应用程序对此毫无感知，因为虚拟机看上去跟真实系统一模一样，而对于底层系统来说，虚拟机就是一个普通文件，不需要了就删掉，对其他部分毫无影响。这类虚拟机完美的运行了另一套系统，能够使应用程序，操作系统和硬件三者之间的逻辑不变。  

> > <font color=red>虚拟机的缺点：</font>

- ==资源占用多==              

- ==冗余步骤多==           

- ==启动慢==

由于前面虚拟机存在这些缺点，Linux 发展出了另一种虚拟化技术：==Linux 容器（Linux Containers，缩写为 LXC）。==

Linux 容器不是模拟一个完整的操作系统，而是对进程进行隔离。有了容器，就可以将软件运行所需的所有资源打包到一个隔离的容器

中。容器与虚拟机不同，不需要捆绑一整套操作系统，只需要软件工作所需的库资源和设置。系统因此而变得高效轻量并保证部署在任何

环境中的软件都能始终如一地运行。

> <font color=red>比较了 Docker 和传统虚拟化方式的不同之处：</font>

- 传统虚拟机技术是==虚拟出一套硬件后，在其上运行一个完整操作系统==，在该系统上再运行所需应用进程；
- 而==容器内的应用进程直接运行于宿主的内核==，==容器内没有自己的内核==，而且==也没有进行硬件虚拟==。因此容器要比传统虚拟机更为==轻便。==

* 每个容器之间==互相隔离==，每个容器有自己的文件系统 ，容器之间进程不会相互影响，能区分计算资源。

> <font color=red>能干嘛</font>

- 更快速的应用交付和部署
- 更便捷的升级和扩容
- 更简单的系统运维
- 更高效的计算资源利用

> <font color=red>Docker的基本概念</font>

- ==镜像（image）==:

​       就是一个==只读的模板==。镜像可以用来创建 Docker 容器，==一个镜像可以创建很多容器。==

- ==容器（container）==

  Docker 利用容器（Container）独立运行的一个或一组应用。容器是==用镜像创建的运行实例==。

  它可以被==启动、开始、停止、删除==。每个容器都是相互隔离的、保证安全的平台。

  可以把容器看做是一个简易版的 Linux 环境（包括root用户权限、进程空间、用户空间和网络空间等）和运行在其中的应用程序。


  容器的定义和镜像几乎一模一样，也是一堆层的统一视角，唯一区别在于容器的最上面那一层是可读可写的。

- ==仓库（repository）==

​      仓库(Repository)和仓库注册服务器（Registry）是有区别的。仓库注册服务器上往往存放着多个仓库，每个仓库中又包含了多个镜

​      像，每个镜像有不同的标签（tag）。

​      仓库分为==公开仓库（Public）==和==私有仓库（Private）==两种形式。

​      最大的公开仓库是 Docker Hub(https://hub.docker.com/)，存放了数量庞大的镜像供用户下载。

​      国内的公开仓库包括==阿里云 、网易云==等

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
  # 清华大学
    yum-config-manager \
    --add-repo \
    https://mirrors.tuna.tsinghua.edu.cn/docker-ce/linux/centos/docker-ce.repo
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
## 多个镜像加速
  "registry-mirrors":     ["https://k5c60z38.mirror.aliyuncs.com","https://docker.mirrors.ustc.edu.cn/"]
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

==TAG==：镜像的标签，我们使用 ==REPOSITORY:TAG 来定义不同的镜像。==

==IMAGE ID==：==镜像ID==

CREATED：镜像创建时间

SIZE：镜像大小

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
####################
######注意端口映射很常用#########
####################
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

说明的一点: ==<font color=red>Docker容器后台运行,就必须有一个前台进程</font>==.容器运行的命令如果<font color=red>不是那些一直挂起的命令</font>（比如运行top，tail），就是会

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

## 四、 Docker镜像理解

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

### 5.1 数据卷

> ==作用==

- <font color=red>docker容器持久化，也就是在宿主机内操作指定目录，却能在容器内得到相应改变</font>

-  <font color=red>容器间继承和数据共享</font>

> 一、直接命令添加

```bash
 docker run -it  -v   /宿主机绝对路径目录:/容器内目录      镜像名
```

<img src="Docker%E7%AC%94%E8%AE%B0.assets/image-20210401165639614.png" alt="image-20210401165639614" style="zoom:80%;" />

==查看是否挂载成功==

<img src="Docker%E7%AC%94%E8%AE%B0.assets/image-20210401170006207.png" alt="image-20210401170006207" style="zoom:80%;" />

==如此，宿主主机和容器内目录的对应目录的下的数据都能感知并共享==

==此外，容器停止退出后，宿主主机修改数据，容器启动也会自动同步==

```bash
 # 带权限 这里是read only,表示容器内的目录下的数据不能修改
 docker run -it -v /宿主机绝对路径目录:/容器内目录:ro 镜像名
```

> 二、DockerFile:类似于描述镜像的源码，可以通过它直接生成新镜像，==在其中可以使用VOLUME指令添加一个或多个数据卷==

```bash
 
VOLUME["/dataVolumeContainer","/dataVolumeContainer2","/dataVolumeContainer3"]
 
# 说明：
 
# 出于可移植和分享的考虑，用-v 主机目录:容器目录这种方法不能够直接在Dockerfile中实现。
# 由于宿主机目录是依赖于特定宿主机的，并不能够保证在所有的宿主机上都存在这样的特定目录
```

步骤：

1. 准备一个==空目录==，比如/mydocker,并==新建一个dockerfile文件==，比如名字叫做mycentos-dockerfile

2. 写个简单的dockefile文件

   <img src="Docker%E7%AC%94%E8%AE%B0.assets/image-20210401174827323.png" alt="image-20210401174827323" style="zoom:80%;" />

3. 执行以下命令

```bash
docker build -f /mydocker/mycentos-dockerfile -t doglast/centos .
# /mydocker/mycentos-dockerfile 代表构建该镜像的dockerfile文件
# doglast/centos 代表新取得镜像名
# .本次执行的上下文路径
# 上下文路径，是指 docker 在构建镜像，有时候想要使用到本机的文件（比如复制），docker build 命令得知这个路径后，会将路径下的所有内容打包。
# 上下文路径下不要放无用的文件，因为会一起打包发送给 docker 引擎，如果文件过多会造成过程缓慢。
```

<img src="Docker%E7%AC%94%E8%AE%B0.assets/image-20210401180754172.png" alt="image-20210401180754172" style="zoom:67%;" />

注意在==镜像中的容器卷目录==和==主机中对应的目录==，可以由==docker inspect 容器id==得到对应关系，是docker在宿主机自动生成的

Docker挂载主机目录Docker访问出现cannot open directory .: Permission denied

==解决办法：在挂载目录后多加一个--privileged=true参数即可==

### 5.2 数据卷容器

> ==容器间传递共享==
>
> 容器之间配置信息的传递，数据卷的生命周期一直==持续到没有容器使用它为止==

<img src="Docker%E7%AC%94%E8%AE%B0.assets/image-20210401192853550.png" alt="image-20210401192853550" style="zoom:67%;" />

## 六、 DockerFile解析

<img src="Docker%E7%AC%94%E8%AE%B0.assets/image-20210401220912759.png" alt="image-20210401220912759" style="zoom:80%;" />![image-20210401220944692](Docker%E7%AC%94%E8%AE%B0.assets/image-20210401220944692.png)

<img src="Docker%E7%AC%94%E8%AE%B0.assets/image-20210401221104759.png" alt="image-20210401221104759" style="zoom:80%;" />

### 6.1 DockerFile的保留字指令

```bash
FROM #基础镜像
MAINTAINER #镜像维护者的姓名和邮箱地址
RUN #容器构建时需要运行的命令
EXPOSE #当前容器对外暴露出的端口

WORKDIR #指定在创建容器后，终端默认登陆进来的工作目录 WORKDIR $MY_PATH
ENV #设置环境变量，这个环境变量会在后续的任何RUN指令中使用，这就如同在命令前面指定了环境变量前缀一样；示例：ENV MY_PATH /usr/mytest
ADD #将宿主机目录下的文件拷贝进镜像且ADD命令会自动处理URL和解压tar压缩包
COPY # 类似于ADD,拷贝文件和目录到镜像中，将从构建上下文目录中<源路径>的文件/目录复制到新的一层的镜像内的<目标路径>位置。
VOLUME #容器数据卷，用于数据保存和持久化工作
CMD #指定一个容器启动时要运行的命令；有两种格式，一种是shell格式：CMD <命令>；一种是exec格式：CMD ["可执行文件"，"参数1"，"参数2"...];在Dockerfile中可以有多个CMD指令，但只有最后一个生效，CMD会被docker run之后的参数替换，也就是docker run命令后的参数会替代最后一个CMD命令的参数
ENTRYPOINT #指定一个容器启动时要运行的命令，ENTRYPOINT的目的和CMD一样，都是在指定容器启动程序及参数，这里不会，docker run命令后的参数只会追加到ENTRYPOINT命令的参数中
ONBUILD #当构建一个被继承的Dockerfile时运行命令，父镜像会在被子镜像继承后父镜像的ONBUILD会被触发
        # 也就是ONBUILD在当该镜像为父镜像时，ONBUILD后面的命令会被运行


```

> ==自定义centos dockfile文件==
>
> 构建命令：
>
> ==docker build -f== /mydocker/dockerfile2 ==-t== mycentos:1.11  ==.==

```dockerfile
FROM centos
MAINTAINER 1799076485@qq.com

ENV MYPATH /usr/local
WORKDIR $MYPATH

RUN yum -y install vim
RUN yum -y install net-tools

EXPOSE 80

CMD echo $MYPATH
CMD echo "success---------ok"
CMD /bin/bash


```

<img src="Docker%E7%AC%94%E8%AE%B0.assets/image-20210402104140954.png" alt="image-20210402104140954" style="zoom:67%;" />

> ==<font color=red>理解CMD和ENTRYPOINT</font>==
>
> ==CMD== #指定一个容器启动时要运行的命令；
>
> 有两种格式，一种是==shell格式：==CMD <命令>；
>
> 一种是==exec格式：==CMD ["可执行文件"，"参数1"，"参数2"...];
>
> 在Dockerfile中可以有多个CMD指令，但只有最后一个生效，CMD会被docker run之后的参数替换，也就是<font color=red>docker run命令后的参数会替代最后一个CMD命令的参数</font>
>
> 比如：运行==docker run -it -p 7777:8080 tomcat ls -l==
>
> <font color=red>ls -l替代了运行tomcat的指令，所以tomcat没有启动</font>
>
> ==ENTRYPOINT== #指定一个容器启动时要运行的命令，ENTRYPOINT的目的和CMD一样，都是在指定容器启动程序及参数，<font color=red>docker run命令后的参数会追加到ENTRYPOINT命令的参数中</font>

### 6.2 案例-以tomcat9为例

> 步骤一：==创建一个目录==，比如名为tomcat-diy-docker文件夹
>
> 步骤二：将==tomcat9的某个版本的压缩包==和==jdk8的压缩包==放进去，可以==touch一个任意文件==（只为说明会全部打包进入新镜像）
>
> 步骤三：==写dockerfile文件==，比如名字叫tomcatDockerfile

```dockerfile
FROM centos
MAINTAINER 1799076485@qq.com

# 把宿主机当前上下文的a.txt拷贝到/usr/local/路径下
COPY a.txt /usr/local/cincontainer.txt

# 把JAVA与tomcat添加到容器中
#
# jdk8u281.tar.gz  tomcat9044.tar.gz
ADD jdk8u281.tar.gz   /usr/local/
ADD tomcat9044.tar.gz  /usr/local/

# 安装vim编辑器

RUN yum -y install vim

# 设置工作访问时候的WORKDIR路径，登录落脚点
ENV MYPATH /usr/local
WORKDIR $MYPATH

#配置java与tomcat环境变量
ENV  JAVA_HOME /usr/local/jdk1.8.0_281
ENV CLASSPATH $JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
ENV CATALINA_HOME /usr/local/apache-tomcat-9.0.44
ENV CATALINA_BASE /usr/local/apache-tomcat-9.0.44
ENV PATH $PATH:$JAVA_HOME/bin:$CATALINA_HOME/lib:$CATALINA_HOME/bin

# 容器运行时监听的端口
EXPOSE 8080

# 启动时运行tomcat
# ENTRYPOINT ["/usr/local/apache-tomcat-9.0.44/bin/startup.sh"]
# CMD ["/usr/local/apache-tomcat-9.0.44/bin/catalina.sh","run"]

CMD /usr/local/apache-tomcat-9.0.44/bin/startup.sh && tail -F /usr/local/apache-tomcat-9.0.44/logs/catalina.out

```

> 步骤四：按照Dockfile进行==构建镜像==

```bash
# 注意，一般在以上目录下执行，因为执行该命令会打包很多东西   这里的.代表的是当前目录的上下文
# -f tomcatDockerfile 指定的是当前目录下的tomcatDockerfile的镜像文件
# 如果当前目录下的镜像文件是Dockerfile，则-f Dockerfile都不用写
docker build -f tomcatDockerfile -t mytomcat9 .
```

> 步骤五：==运行docker上的tomcat9==

```bash
docker run -d -p 9080:8080 --name myt9  \   
  -v /mydockerApplication/web/test:/usr/local/apache-tomcat-9.0.44/webapps/test \
  -v /mydockerApplication/web/logs:/usr/local/apache-tomcat-9.0.44/logs \
  --privillage=true \
  mytomcat9
  
  # 以上命令做了端口映射，取了新名字myt9
  # 挂载了容器卷两个，其中webapps目录下的项目被映射到主机
```

<img src="Docker%E7%AC%94%E8%AE%B0.assets/image-20210402140334153.png" alt="image-20210402140334153" style="zoom:67%;" />

果然访问宿主主机的9080端口即可访问

> 其实这不仅仅是tomcat9的镜像，只是里面最后的==CMD执行的是tomcat9的运行命令==，如果你==后台运行了上面的镜像==，还是可以
>
> ==docker exec -it 容器ID [命令]== 以执行其他操作

## 七、 Docker常用安装

> 一、docker安装mysql
>
> ```bash
> docker pull mysql:5.6
> ```
>
> 二、运行之
>
> ```bash
> docker run -itd --name mysql -p 3306:3306 
> -e MYSQL_ROOT_PASSWORD=123456 mysql:5.6
> ```
>
> <img src="Docker%E7%AC%94%E8%AE%B0.assets/image-20210402145110485.png" alt="image-20210402145110485" style="zoom:80%;" />
>
> 三、查验之
>
> ![image-20210402145602297](Docker%E7%AC%94%E8%AE%B0.assets/image-20210402145602297.png)
>
> 四、进入正在运行的mysql中
>
> ```bash
> docker exec  -it  mysql容器id /bin/bash
> ```
>
> <img src="Docker%E7%AC%94%E8%AE%B0.assets/image-20210402150606652.png" alt="image-20210402150606652" style="zoom:80%;" />
>
> 五、运行mysql
>
> <img src="Docker%E7%AC%94%E8%AE%B0.assets/image-20210402150743102.png" alt="image-20210402150743102" style="zoom:67%;" />

> 其他的类似

## 八、 本地镜像发布到阿里云

https://blog.csdn.net/pjsdsg/article/details/90600471

