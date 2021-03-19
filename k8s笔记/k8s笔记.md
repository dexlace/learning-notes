什么是k8s







k8s集群分为两类节点：

- master node：主
- worker node：工作

master节点的组件（程序）：

- apiserver：接收客户端操作k8s的指令
- scheduler：从多个worker node节点的组件中选举一个来启动服务
- controller manager：向worker节点的kubelet发送指令

node节点的组件（程序）：

- kubelet：向docker发送指令管理docker容器
- kubeproxy：管理docker容器的网络

pod

- 一组容器的集合
- k8s最小部署单元
- 一个pod中的容器共享网络命名空间

k8s中不能直接启动容器

Controllers

- 控制pod：启动、停止、删除
- 分类
  - ReplicaSet:确保预期的pod副本数量
  - Deployment：无状态应用部署
  - StatefulSet：有状态应用部署
  - DaemonSet：确保所有Node运行同一个pod
  - Job：一次性任务
  - cronjob：定时任务

 Service：

![image-20200908000355964](k8s%E7%AC%94%E8%AE%B0.assets/image-20200908000355964.png)

- 将一组pod关联起来们提供一个统一的入口，即使pod地址发生改变，统一入口的地址也不会发生变化，可以保证用户访问不受影响

Label：标签

- 一组pod是一个统一的标签
- service是通过标签和一组pod进行关联的

namespace：名称空间

- 用来隔离pod的运行环境【默认情况下，pod是可以互相访问的】

- 为不同的公司提供隔离的pod运行环境
- 为开发、测试、生产环境分别准备不同的名称空间进行隔离

## 1、 生产环境K8S平台规划

![image-20200908001818078](k8s%E7%AC%94%E8%AE%B0.assets/image-20200908001818078.png)

![image-20200908001914047](k8s%E7%AC%94%E8%AE%B0.assets/image-20200908001914047.png)

master建议3台

etcd必须三台或以上的奇数台

worker节点越多越好

![image-20200908002734062](k8s%E7%AC%94%E8%AE%B0.assets/image-20200908002734062.png)

![image-20200908002754524](k8s%E7%AC%94%E8%AE%B0.assets/image-20200908002754524.png)

## 2.官方提供三种部署方式

### 2.1 minikube

一个工具、可以在本地快速运行一个单点的Kubernets，仅用于尝试Kubernetes

部署地址：https://kubernetes.io/docs/setup/minikube/

### 2.2 kubeadm

提供kubeadm init和kubeadm join，用于快速部署Kubernetes集群

部署地址：https://kubernetes.io/docs/reference/setup-tools/kubeadm/kubeadm

### 2.3 二进制

推荐，从官方下载发行版的二进制包，手动部署每个组件，组成Kubernetes集群

下载地址：https://github.com/kubernetes/kubernetes/releases





