# 自动构建工具：Maven基础Eclipse版  
## 1. Maven的简介  
![](Maven%E5%9F%BA%E7%A1%80Eclipse%E7%89%88.assets/20190710213606210_15152.png )  
### 1.1 Maven的概念  
maven是一个`Java语言编写`的开源项目管理工具，是Apache软件基金会的顶级项目.主要用于`项目构建，依赖管理，项目信息管理`,是现今最流行的`Java项目构建工具`.  
### 1.2 项目构建的概念  
项目构建是一个项目从编写源代码到编译、测试、运行、打包、部署的过程。  
![](Maven%E5%9F%BA%E7%A1%80Eclipse%E7%89%88.assets/20190710211341580_14708.png)  
![](Maven%E5%9F%BA%E7%A1%80Eclipse%E7%89%88.assets/20190710212942867_8207.png)  
![](Maven%E5%9F%BA%E7%A1%80Eclipse%E7%89%88.assets/20190710213259924_1658.png)  
![](Maven%E5%9F%BA%E7%A1%80Eclipse%E7%89%88.assets/20190710214306863_283.png )  

## 2. Maven的安装配置及目录结构  
首先，确保`JAVA_HOME`正确配置  
然后解压Maven安装包到某个非中文路径下  
配置Maven的环境变量MAVEN_HOME  
验证：mvn -v  
![](Maven%E5%9F%BA%E7%A1%80Eclipse%E7%89%88.assets/20190710220344302_10763.png )  

## 3. Maven的仓库  
![](Maven%E5%9F%BA%E7%A1%80Eclipse%E7%89%88.assets/20190710221317935_20361.png )  
![](Maven%E5%9F%BA%E7%A1%80Eclipse%E7%89%88.assets/20190710221357020_3679.png )  

## 4. Maven工程的目录结构  
![](Maven%E5%9F%BA%E7%A1%80Eclipse%E7%89%88.assets/20190710223956507_21084.png )  
![](Maven%E5%9F%BA%E7%A1%80Eclipse%E7%89%88.assets/20190710224900003_3111.png )  

## 5. Maven的常用命令  
![](Maven%E5%9F%BA%E7%A1%80Eclipse%E7%89%88.assets/20190710224714147_6555.png )  
## 6. Maven的生命周期(其功能由插件完成)  
### 6.1 clean生命周期  
![](Maven%E5%9F%BA%E7%A1%80Eclipse%E7%89%88.assets/20190710231015518_5593.png )  
### 6.2 default生命周期  
![](Maven%E5%9F%BA%E7%A1%80Eclipse%E7%89%88.assets/20190710231622735_14476.png )  
### 6.3 site生命周期  
![](Maven%E5%9F%BA%E7%A1%80Eclipse%E7%89%88.assets/20190710231839324_5190.png )  
### 6.4 命令行与生命周期  
![](Maven%E5%9F%BA%E7%A1%80Eclipse%E7%89%88.assets/20190710232252724_29166.png )  
## 7. Maven的概念模型  
![](Maven%E5%9F%BA%E7%A1%80Eclipse%E7%89%88.assets/20190710234557593_23788.png )  
## 8. 在Eclipse中创建Maven项目  
### 8.1 配置本地maven和设置仓库地址  
![](Maven%E5%9F%BA%E7%A1%80Eclipse%E7%89%88.assets/20190727155350250_29419.png )  
![](Maven%E5%9F%BA%E7%A1%80Eclipse%E7%89%88.assets/20190727155438077_28413.png )  

### 8.2 根据向导创建maven项目  
![](Maven%E5%9F%BA%E7%A1%80Eclipse%E7%89%88.assets/20190727155911117_19285.png )  
### 8.3 处理红色叉号  
![](Maven%E5%9F%BA%E7%A1%80Eclipse%E7%89%88.assets/20190727160416699_27863.png )  
### 8.4 重建仓库索引  
![](Maven%E5%9F%BA%E7%A1%80Eclipse%E7%89%88.assets/20190727161002258_11297.png )  
### 8.5 修改JRE运行环境  
![](Maven%E5%9F%BA%E7%A1%80Eclipse%E7%89%88.assets/20190727161343824_32094.png )  
### 8.6 修改tomcat配置  
![](Maven%E5%9F%BA%E7%A1%80Eclipse%E7%89%88.assets/20190727161328258_13180.png )  
### 8.7 依赖范围：scope属性  
![](Maven%E5%9F%BA%E7%A1%80Eclipse%E7%89%88.assets/20190727163218987_29351.png )  
### 8.8 依赖管理：处理版本冲突  
#### 8.8.1 依赖传递  
![](Maven%E5%9F%BA%E7%A1%80Eclipse%E7%89%88.assets/20190727164854158_32618.png )  
#### 8.8.2 版本冲突  
![](Maven%E5%9F%BA%E7%A1%80Eclipse%E7%89%88.assets/20190727170314049_4424.png )  
##### 8.8.2.1 第一声明优先原则  
![](Maven%E5%9F%BA%E7%A1%80Eclipse%E7%89%88.assets/20190727170526759_12260.png )  
##### 8.8.2.2 路径优先原则  
![](Maven%E5%9F%BA%E7%A1%80Eclipse%E7%89%88.assets/20190727170842459_31493.png )  
##### 8.8.2.3 排除原则  
![](Maven%E5%9F%BA%E7%A1%80Eclipse%E7%89%88.assets/20190727171229068_13173.png )  
##### 8.8.2.4 版本锁定原则  
![](Maven%E5%9F%BA%E7%A1%80Eclipse%E7%89%88.assets/20190727171608830_20634.png )  
## 9. Maven分模块开发  
## 10. Maven私服  
