# SVN学习笔记  
## 一、SVN简介  
SVN是`Subversion`的简称，是一个`开放源代码的版本控制系统`，相较于RCS、CVS，它采用了`分支管理系统`，它的设计目标就是`取代CVS`。也就是说 `Subversion 管理着随时间改变的数据`。 这些数据放置在一个`中央资料档案库(repository) `中。 这个档案库很像`一个普通的文件服务器`, 不过`它会记住每一次文件的变动`。 这样你就`可以把档案恢复到旧的版本, 或是浏览文件的变动历史`。说得简单一点SVN就是`用于多个人共同开发同一个项目，共用资源的目的`。  
## 二、SVN基本操作  
![](SVN%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190804171404681_10265.png )  
## 三、SVN服务端安装配置  
### 3.1 两种服务器端安装包  
#### 3.1.1 官方安装包  
[SVN官方网站](http://subversion.apache.org)  
[非图形化SVN服务器下载](http://subversion.apache.org/download.cgi)  
![](SVN%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190804172921653_4808.png )  
官方提供的服务端安装包，安装后需要通过命令行操作，适用于专业配置管理员使用。  

#### 3.2 图形化服务器  
志愿者开发的图形化操作界面的svn服务端，它适用于普通软件开发人员使用。  
[下载地址](https://www.visualsvn.com/downloads/)  
![](SVN%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190804173208637_28128.png )  

### 3.2 安装图形化服务器端  
![](SVN%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190804173546132_14318.png )  
### 3.3 创建仓库  
![](SVN%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190804174734838_7473.png )  
### 3.4 创建工程目录  
![](SVN%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190804175308893_25299.png )  
**trunk**：项目开发代码的主体，是从项目开始直到当前都<font color=red>**处于活动的状态**</font>，从这里可以<font color=red>**获得项目最新的源代码**</font>以及几乎<font color=red>**所有的变更历史信息**</font>。  

**branch**：<font color=red>**从trunk的某个点分离出来的代码拷贝**</font>，通常可以在不影响主干的前提下在这里**进行重大bug的修改，或者做实验性的开发，以及定制功能开发**等。如果分支达到了预期的目的，通常可以被**合并（Mgerge）到主干中**。  

**tag**：用来表示trunk和branch的某个点的状态，以代表项目的<font color=red>**某个稳定状态**</font>，通常为最终发布状态。  
![](SVN%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190804180116769_12954.png )  
![](SVN%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190804180157259_7790.png )  

## 四、TortoiseSVN客户端  
### 4.1 SVN客户端类型  
svn客户端需要通过网络访问svn服务端提交文件、查询文件等，可通过以下客户端类型访问svn服务端：  
1. 使用Subversion提供的客户端命令。使用方式：在命令行下输入命令操作。   
  
2. 使用<font color=red>Torotise图形化界面</font>操作（推荐）  
  
3. 使用<font color=red>Eclipse等开发工具插件</font>操作（推荐）  

### 4.2 下载安装  
TortoiseSVN是Subversion版本控制系统的一个免费开源客户端，不需要为使用它而付费。TortoiseSVN是 Subversion 的 Windows 扩展。它使你避免接触 Subversion 枯燥而且不方便的 Command Line。它完全嵌入 Windows Explorer，使用时只需<font color=red>在正常的窗口里右键操作</font>就可以了。  
[下载地址](http://tortoisesvn.net/downloads.html)  
### 4.3 浏览仓库  
![](SVN%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190804181149124_20972.png )  
## 五、权限管理  
### 5.1 创建用户  
![](SVN%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190804182326238_31026.png )  
### 5.2 创建组  
![](SVN%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190804182616958_2569.png )  
### 5.3 分配权限  
![](SVN%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190804182903900_8121.png )  
### 5.4 清除认证缓存  
有几种情况需要清除认证缓存：  
1、本地使用多个账号登陆，每次输入的账号和密码都不一样  
2、当账号密码修改后（建议清理）  
![](SVN%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190804183156242_12670.png )  

## 六、TortoiseSVN日常使用  
### 6.1 浏览仓库  
见4.3节  
### 6.2 导入导出  
![](SVN%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190804185032732_16739.png )  
导入类似操作  

### 6.3 修改提交  
#### 6.3.1 checkout  
![](SVN%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190804185516614_18694.png )  
#### 6.3.2 add与commit  
Add to ignore list ：添加到忽略列表（`标记该文件不需要版本控制` ）
![](SVN%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190804204717407_26720.png )  

#### 6.3.3 update  
![](SVN%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190804205645523_18668.png )  
#### 6.3.4 delete  
![](SVN%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190804212837292_23706.png )  
#### 6.3.5 恢复  
![](SVN%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190804213733209_6128.png )  
### 6.4 冲突处理  
![](SVN%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190804210107662_30795.png )  
![](SVN%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190804210425241_2073.png )  
![](SVN%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190804211126729_13322.png )  
然后bbb点了对话框弹出来的Update  
![](SVN%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190804212053016_10017.png )  

## 七、eclipse的SVN插件使用  
### 7.1 SVN插件安装  
![](SVN%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190804214608053_27168.png )  
### 7.2 将项目共享到SVN  
![](SVN%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190804225737802_24744.png )  
然后会来到SVN视图中  
![](SVN%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190804230520175_2232.png )  

### 7.3 从SVN检出   
![](SVN%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190805001553209_13503.png )  
### 7.4 增删改  
![](SVN%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190805002342511_11893.png )  

### 7.4 解决冲突  
按照原理就行呀，先与资源同步，再更新，再修改，最后提交。  
### 7.5 查看资源历史  
![](SVN%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190805002827335_18601.png )  
