SpringCloud教程

## 一、前言

### 1.1 微服务架构

微服务架构是一种架构模式，它提倡==将单一应用程序划分成一组小的服务==，服务之间相互协调，为用户提供最终的价值。每个服务运行在独立的进程中，服务与服务之间==采用轻量级的通信机制互相协作==（==通常是基于HTTP协议的RESTful API==），每个服务围绕具体的业务构建，并且能够被独立部署到生产环境、类生产环境中。另外，应当尽量避免统一的、集中式的服务管理机制，对于具体的一个服务而言，应该根据业务上下文，选择合适的语言、工具进行构建。

<img src="SpringCloud%E6%95%99%E7%A8%8B.assets/image-20210222211800968.png" alt="image-20210222211800968" style="zoom:80%;" />

<img src="SpringCloud%E6%95%99%E7%A8%8B.assets/image-20210222212047885.png" alt="image-20210222212047885" style="zoom:80%;" />

<img src="SpringCloud%E6%95%99%E7%A8%8B.assets/image-20210222212258946.png" alt="image-20210222212258946" style="zoom:80%;" />

<img src="SpringCloud%E6%95%99%E7%A8%8B.assets/image-20210222212755061.png" alt="image-20210222212755061" style="zoom:80%;" />

<img src="SpringCloud%E6%95%99%E7%A8%8B.assets/image-20210222212828467.png" alt="image-20210222212828467" style="zoom:80%;" />

<img src="SpringCloud%E6%95%99%E7%A8%8B.assets/image-20210222213805050.png" alt="image-20210222213805050" style="zoom:80%;" />

<img src="SpringCloud%E6%95%99%E7%A8%8B.assets/image-20210222213934922.png" alt="image-20210222213934922" style="zoom:80%;" />

<img src="SpringCloud%E6%95%99%E7%A8%8B.assets/image-20210222214358725.png" alt="image-20210222214358725" style="zoom:60%;" />

![image-20210222215542181](SpringCloud%E6%95%99%E7%A8%8B.assets/image-20210222215542181.png)



## 二、工程搭建

构建父工程，后面的项目模块都在此工程中：

![1597214225785](SpringCloud%E6%95%99%E7%A8%8B.assets/1597214225785.png)

设置编码：Settings -> File Encodings

注解激活：![1597214602636](SpringCloud%E6%95%99%E7%A8%8B.assets/1597214602636.png)

Java版本确定：

![1597214699619](SpringCloud%E6%95%99%E7%A8%8B.assets/1597214699619.png)

### 父工程pom配置

```xml
<?xml version="1.0" encoding="UTF-8"?>

<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <groupId>com.dkf.cloud</groupId>
  <artifactId>cloud2020</artifactId>
  <version>1.0-SNAPSHOT</version>
    <!-- 第一步 -->
  <packaging>pom</packaging>

  <!-- 统一管理 jar 包版本 -->
  <properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <maven.compiler.source>1.8</maven.compiler.source>
    <maven.compiler.target>1.8</maven.compiler.target>
    <junit.version>4.12</junit.version>
    <log4j.version>1.2.17</log4j.version>
    <lombok.version>1.16.18</lombok.version>
    <mysql.version>5.1.47</mysql.version>
    <druid.version>1.1.16</druid.version>
    <mybatis.spring.boot.version>1.3.0</mybatis.spring.boot.version>
  </properties>

  <!-- 子块基础之后，提供作用：锁定版本 + 子module不用写 groupId 和 version -->
  <dependencyManagement>
    <dependencies>
        <dependency>
          <groupId>org.apache.maven.plugins</groupId>
          <artifactId>maven-project-info-reports-plugin</artifactId>
          <version>3.0.0</version>
        </dependency>

        <!-- 下面三个基本是微服务架构的标配 -->
        <!--spring boot 2.2.2-->
        <dependency>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-dependencies</artifactId>
          <version>2.2.2.RELEASE</version>
          <type>pom</type>
          <scope>import</scope>
        </dependency>
        <!--spring cloud Hoxton.SR1-->
        <dependency>
          <groupId>org.springframework.cloud</groupId>
          <artifactId>spring-cloud-dependencies</artifactId>
          <version>Hoxton.SR1</version>
          <type>pom</type>
          <scope>import</scope>
        </dependency>
        <!--spring cloud 阿里巴巴-->
        <dependency>
          <groupId>com.alibaba.cloud</groupId>
          <artifactId>spring-cloud-alibaba-dependencies</artifactId>
          <version>2.1.0.RELEASE</version>
          <type>pom</type>
          <scope>import</scope>
        </dependency>

        <!--mysql-->
        <dependency>
          <groupId>mysql</groupId>
          <artifactId>mysql-connector-java</artifactId>
          <version>${mysql.version}</version>
          <scope>runtime</scope>
        </dependency>
        <!-- druid-->
        <dependency>
          <groupId>com.alibaba</groupId>
          <artifactId>druid</artifactId>
          <version>${druid.version}</version>
        </dependency>
        <dependency>
          <groupId>org.mybatis.spring.boot</groupId>
          <artifactId>mybatis-spring-boot-starter</artifactId>
          <version>${mybatis.spring.boot.version}</version>
        </dependency>
        <!--junit-->
        <dependency>
          <groupId>junit</groupId>
          <artifactId>junit</artifactId>
          <version>${junit.version}</version>
        </dependency>
        <!--log4j-->
        <dependency>
          <groupId>log4j</groupId>
          <artifactId>log4j</artifactId>
          <version>${log4j.version}</version>
        </dependency>
    </dependencies>
  </dependencyManagement>

  <build>
    <plugins>
      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
        <configuration>
          <fork>true</fork>
          <addResources>true</addResources>
        </configuration>
      </plugin>
    </plugins>
  </build>
</project>
```

上面配置的解释：

> 首先要加  <packaging>pom</packaging>  这个。
>
> 聚合版本依赖，dependencyManagement 只声明依赖，并不实现引入，所以子项目还需要写要引入的依赖。
>
> ![1597215801116](SpringCloud%E6%95%99%E7%A8%8B.assets/1597215801116.png)

![1597215611540](images/1597215611540.png)

![1597215639355](SpringCloud%E6%95%99%E7%A8%8B.assets/1597215639355.png)

![1597215666979](SpringCloud%E6%95%99%E7%A8%8B.assets/1597215666979.png)

## 三、Eureka-==服务注册中心==

### 3.1 服务治理

Spring Cloud封装了Netflix公司开发的Eureka模块来实现==服务治理==

在传统的rpc远程调用框架中，管理每个服务与服务之间的==依赖关系==比较复杂，服务治理就是用于==管理服务与服务之间依赖关系==，实现==服务调用、负载均衡、容错==等，实现==服务发现与注册==。

### 3.3 服务注册与发现

![1597291942982](SpringCloud%E6%95%99%E7%A8%8B.assets/1597291942982.png)

![1597291981643](SpringCloud%E6%95%99%E7%A8%8B.assets/1597291981643.png)

![image-20210225230739399](SpringCloud%E6%95%99%E7%A8%8B.assets/image-20210225230739399.png)

### 3.4 启动eureka server

server 模块使用 7001端口，下面是pom文件需要的依赖：

```xml
<artifactId>cloud-eureka-server7001</artifactId>

 <dependencies>
     <dependency>
         <groupId>org.springframework.cloud</groupId>
         <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
     </dependency>
     <dependency>
         <groupId>org.springframework.boot</groupId>
         <artifactId>spring-boot-starter-web</artifactId>
     </dependency>
     <dependency>
         <groupId>org.springframework.boot</groupId>
         <artifactId>spring-boot-starter-actuator</artifactId>
     </dependency>
     <dependency>
         <groupId>org.mybatis.spring.boot</groupId>
         <artifactId>mybatis-spring-boot-starter</artifactId>
     </dependency>
     <dependency>
         <groupId>org.springframework.boot</groupId>
         <artifactId>spring-boot-devtools</artifactId>
         <scope>runtime</scope>
         <optional>true</optional>
     </dependency>
     <dependency>
         <groupId>org.springframework.boot</groupId>
         <artifactId>spring-boot-starter-test</artifactId>
         <scope>test</scope>
     </dependency>
     <dependency><!-- 引入自己定义的api通用包，可以使用Payment支付Entity -->
         <groupId>com.atguigu.springcloud</groupId>
         <artifactId>cloud-api-commons</artifactId>
         <version>${project.version}</version>
     </dependency>
 </dependencies>
```

下面是yml文件

```yml
server:
port: 7001

eureka:
instance:
 hostname: localhost  # eureka 服务器的实例名称

client:
 # false 代表不向服务注册中心注册自己，因为它本身就是服务中心
 register-with-eureka: false
 # false 代表自己就是服务注册中心，自己的作用就是维护服务实例，并不需要去检索服务
 fetch-registry: false
 service-url:
   # 设置与 Eureka Server 交互的地址，查询服务 和 注册服务都依赖这个地址
   defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
```

最后写主启动类，如果启动报错，说没有配置 DataSource ，就在 主启动类的注解加上 这样的配置：

```java
// exclude ：启动时不启用 DataSource的自动配置检查
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableEurekaServer   // 表示它是服务注册中心
public class EurekaServerMain7001 {
 public static void main(String[] args){
     SpringApplication.run(EurekaServerMain7001.class, args);
 }
}
```

启动测试，访问 7001 端口

### 3.5 注册eureka client

#### 3.5.1 注册provider

> 这里的提供者，还是使用 上面的 cloud-provider-payment8001 模块，做如下修改：

1. 在 pom 文件的基础上引入 eureka 的client包，即：

```xml
        <!--eureka-client-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
```

全部的依赖展示如下：

```xml
    <artifactId>cloud-provider-payment8001</artifactId>
    <dependencies>
        <!--eureka-client-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid-spring-boot-starter</artifactId>
            <version>1.1.10</version>
        </dependency>
        <!--mysql-connector-java-->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>
        <!--jdbc-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jdbc</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency><!-- 引入自己定义的api通用包，可以使用Payment支付Entity -->
            <groupId>com.dkf.cloud</groupId>
            <artifactId>cloud-api-commons</artifactId>
            <version>${project.version}</version>
        </dependency>
    </dependencies>
```

2. 主启动类 加上注解 ：==@EnableEurekaClient==
3. yml 文件添加关于 Eureka 的配置：

```yml
eureka:
  client:
	# 注册进 Eureka 的服务中心
    register-with-eureka: true
    # 检索 服务中心 的其它服务
    fetch-registry: true
    service-url:
      # 设置与 Eureka Server 交互的地址
      defaultZone: http://localhost:7001/eureka/
```

<img src="SpringCloud%E6%95%99%E7%A8%8B.assets/1597294710656.png" alt="1597294710656" style="zoom: 67%;" />

#### 3.5.2 注册consumer

同注册provider一样

### 3.6 Eureka集群

> Eureka 集群的原理，就是 相互注册，互相守望
>
> 模拟多个 Eureka Server 在不同机器上 ： 进入C:\Windows\System32\drivers\etc\hosts  添加如下：
>
> 127.0.0.1 eureka7001
>
> 127.0.0.1 eureka7002

现在创建 cloud-eureka-server7002 ，也就是第二个 Eureka 服务注册中心，pom 文件和 主启动类，与第一个Server一致。

现在修改这两个 Server 的 yml 配置：

7001 端口的Server yml文件：

```yml
server:
  port: 7001

eureka:
  instance:
    hostname: eureka7001  # eureka 服务器的实例地址

  client:
    register-with-eureka: false
    fetch-registry: false
    service-url:
    ## 一定要注意这里的地址，这是搭建集群的关键
      defaultZone: http://eureka7002:7002/eureka/
```

7002 端口的Server yml文件：

```yml
server:
  port: 7002

eureka:
  instance:
    hostname: eureka7002  # eureka 服务器的实例地址

  client:
    register-with-eureka: false
    fetch-registry: false
    service-url:
    ## 一定要注意这里的地址 这是搭建集群的关键
      defaultZone: http://eureka7001:7001/eureka/
```

> eureka.instance.hostname 才是启动以后 本 Server 的注册地址，而 service-url  是 map 类型，只要保证 key:value 格式就行，它代表 本Server 指向了那些 其它Server 。利用这个，就可以实现Eureka Server 相互之间的注册，从而实现集群的搭建。

### 3.7 Provider集群

> 为提供者，即 cloud-provider-payment8001 模块创建集群，新建模块为 cloud-provider-payment8002，其实复制一下改下==端口号，主启动程序名、pom文件中的名字==、还有就是记得==在父项目中添加cloud-provider-payment8002的项目名==
>
> 最终实现：
>
> <img src="SpringCloud%E6%95%99%E7%A8%8B.assets/1597301105015.png" alt="1597301105015" style="zoom:80%;" />
>
> 注意在 Controller 返回不同的消息，比如==端口号==返回区分一下，从而区分者两个提供者的工作状态。

两个provider的主要配置如下：

注意==端口号不一样==，但是==spring.application.name一样==，以对外暴露同样的应用名

```yml
server:
  port: 8001  # 端口号不一样

spring:
  application:
    name: cloud-provider-service  # 这次重点是这里，两个要写的一样，这是这个集群的关键
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    driver-class-name: org.gjt.mm.mysql.Driver
    url: jdbc:mysql://localhost:3306/cloud2020?useUnicode=true&characterEncoding=utf-8&useSSL=false
    username: root
    password: 123456

mybatis:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: com.atguigu.springcloud.entities  

eureka:
  client:
    register-with-eureka: true
    fetch-registry: true
    service-url:
      defaultZone: http://eureka7001.com:7001/eureka/,http://eureka7002.com:7002/eureka/
```

消费者的配置

> 消费者如何访问由上述两个提供者组成的集群？

==注意Eureka Server上的暴露的提供者的服务名==：

<img src="SpringCloud%E6%95%99%E7%A8%8B.assets/image-20210226172802060.png" alt="image-20210226172802060" style="zoom:80%;" />

消费者的controller修改如下，主要是将==提供者在Eureka上的名称==换掉以前的url

```java
@RestController
@Slf4j
public class OrderController {
							    // 重点是这里，改成 提供者在Eureka 上的名称，而且无需写端口号	
    public static final String PAYMENY_URL = "http://CLOUD-PROVIDER-SERVICE";

    @Resource
    private RestTemplate restTemplate;

    @PostMapping("customer/payment/create")
    public CommonResult<Payment> create (Payment payment){
        return restTemplate.postForObject(PAYMENY_URL + "/payment/create", payment, CommonResult.class);
    }

    @GetMapping("customer/payment/{id}")
    public CommonResult<Payment> getPaymentById(@PathVariable("id")Long id){
        return restTemplate.getForObject(PAYMENY_URL + "/payment/" + id, CommonResult.class);
    }

}
```

消费者里面对RestTemplate配置的config文件，需要更改成如下：（就是==加一个注解 @LoadBalanced==）

```java
@Configuration
public class ApplicationContextConfig {

    @Bean
    @LoadBalanced  //这个注解，就赋予了RestTemplate 负载均衡的能力
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }
}
```

### 3.8 tips:主机名及主机地址

修改在Eureka注册中心显示的==主机名==

微服务所在的==主机地址==

```yml
eureka:
  instance:
  # 主机名
    instance-id: payment8001
    # 主机地址
    prefer-ip-address: true
```

<img src="SpringCloud%E6%95%99%E7%A8%8B.assets/1597301396971.png" alt="1597301396971" style="zoom: 67%;" />

### 3.9 服务发现Discovery

> 对于注册进eureka里面的微服务，可以通过服务发现来获得该服务的信息

1. 在主启动类上添加注解：==@EnableDiscoveryClient==
2. 添加Discoveryclient，注意导入的包名为==org.springframework.cloud.client.discovery.DiscoveryClient==在 Controller 里面打印信息：

```yml
@Resource
    private DiscoveryClient discoveryClient;

    @GetMapping("/customer/discovery")
    public Object discovery(){
        List<String> services = discoveryClient.getServices();
        for(String service: services){
            log.info("*****service: " + service);
        }
        List<ServiceInstance> instances = discoveryClient.getInstances("CLOUD-ORDER-SERVICE");
        for(ServiceInstance serviceInstance:instances){
            log.info(serviceInstance.getServiceId() + "\t" + serviceInstance.getHost()
                    + "\t" + serviceInstance.getPort() + "\t" + serviceInstance.getUri());
        }
        return this.discoveryClient;
    }
```

### 3.10 Eureka的自我保护机制

<img src="SpringCloud%E6%95%99%E7%A8%8B.assets/1597302605610.png" alt="1597302605610" style="zoom:80%;" />

![1597302314591](SpringCloud%E6%95%99%E7%A8%8B.assets/1597302314591.png)

![1597302449686](SpringCloud%E6%95%99%E7%A8%8B.assets/1597302449686.png)

禁止自我保护:

>在 Eureka Server 的模块中的 yml 文件进行配置：

<img src="SpringCloud%E6%95%99%E7%A8%8B.assets/1597302719594.png" alt="1597302719594" style="zoom:80%;" />

修改 Eureka Client 模块的 心跳间隔时间：

<img src="SpringCloud%E6%95%99%E7%A8%8B.assets/1597302855396.png" alt="1597302855396" style="zoom:80%;" />

## 四、Zookeeper-==服务注册中心==

> springCloud整合zookeeper

![1597303059965](SpringCloud%E6%95%99%E7%A8%8B.assets/1597303059965.png)

### 4.1 提供者注册

> 创建一个提供者，使用8003端口

==pom文件如下：==

```xml
<artifactId>cloud-provider-payment8003</artifactId>

    <dependencies>
        <!--springcloud 整合 zookeeper 组件-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-zookeeper-discovery</artifactId>
            <exclusions>
                <exclusion>
                    <groupId>org.apache.zookeeper</groupId>
                    <artifactId>zookeeper</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>org.apache.zookeeper</groupId>
            <artifactId>zookeeper</artifactId>
            <version>3.5.8</version>
            <exclusions>
                <exclusion>
                    <groupId>org.slf4j</groupId>
                    <artifactId>slf4j-log4j12</artifactId>
                </exclusion>
            </exclusions>
        </dependency>

        <dependency>
            <groupId>com.atguigu.springcloud</groupId>
            <artifactId>cloud-api-commons</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
```

==主启动类：==

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient	
public class PaymentMain8003 {
    public static void main(String[] args){
        SpringApplication.run(PaymentMain8003.class, args);
    }
}
```

==Controller 打印信息：==

```java
@RestController
@Slf4j
public class PaymentController {

    @Resource
    private PaymentService paymentService;

    @Value("${server.port}")
    private String serverPort;

    @RequestMapping("/payment/zk")
    public String paymentzk(){
        return "springcloud with zookeeper :" + serverPort + "\t" + UUID.randomUUID().toString();
    }
}
```

如果 zookeeper 的版本和导入的jar包版本不一致，启动就会报错，由jar包冲突的问题。

解决这种冲突，需要在 pom 文件中，==排除掉引起冲突的jar包，添加和服务器zookeeper版本一致的 jar 包==，但是新导入的 zookeeper jar包又==有 slf4j== 冲突问题，于是再次排除引起冲突的jar包

```xml
<!--springcloud 整合 zookeeper 组件-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-zookeeper-discovery</artifactId>
    <!-- 排除与zookeeper版本不一致到导致 冲突的 jar包 -->
    <exclusions>
        <exclusion>
            <groupId>org.apache.zookeeper</groupId>
            <artifactId>zookeeper</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<!-- 添加对应版本的jar包 -->
<dependency>
    <groupId>org.apache.zookeeper</groupId>
    <artifactId>zookeeper</artifactId>
    <version>3.5.8</version>
    <!-- 排除和 slf4j 冲突的 jar包 -->
    <exclusions>
        <exclusion>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-log4j12</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

==yml文件：==

```yml
server:
  port: 8003

spring:
  application:
    name: cloud-provider-service
  cloud:
    zookeeper:
      connect-string: 192.168.205.100:2181
```

==启动测试：==

![1597306855233](SpringCloud%E6%95%99%E7%A8%8B.assets/1597306855233.png)

### 4.2 消费者注册

> 创建测试zookeeper作为服务注册中心的 消费者 模块 cloud-customerzk-order80
>
> 主启动类、pom文件、yml文件和提供者的类似

==config类，注入 RestTemplate==

```java
@SpringBootConfiguration
public class ApplicationContextConfig {
    @Bean
    @LoadBalanced
    public RestTemplate getTemplate(){
        return new RestTemplate();
    }
}
```

==controller层也是和之前类似：==

```java
@RestController
@Slf4j
public class CustomerZkController {

    public static final String INVOKE_URL="http://cloud-provider-service";

    @Resource
    private RestTemplate restTemplate;

    @RequestMapping("/customer/payment/zk")
    public String paymentInfo(){
        String result = restTemplate.getForObject(INVOKE_URL + "/payment/zk",String.class);
        return result;
    }
}
```

关于 zookeeper 的集群搭建，目前使用较少，而且在 yml 文件中的配置也是类似，以列表形式写入 zookeeper 的多个地址即可，而且zookeeper 集群，在 hadoop的笔记中也有记录。总而言之，只要配合zookeeper集群，以及yml文件的配置就能完成集群搭建

## 五、Consul-==服务注册中心==

> consul也是服务注册中心的一个实现，是由go语言写的。官网地址： https://www.consul.io/intro 
>
> 中文地址： https://www.springcloud.cc/spring-cloud-consul.html 
>
> 功能：![1597380885054](SpringCloud%E6%95%99%E7%A8%8B.assets/1597380885054.png)

### 5.1 安装运行

> 下载地址：https://www.consul.io/downloads.html
>
> 打开下载的压缩包，只有一个exe文件，实际上是不用安装的，在exe文件所在目录打开dos窗口使用即可。
>
> 使用开发模式启动：consul agent -dev
>
> 访问8500端口，即可访问首页

### 5.2 提供者

> 新建提供者模块：cloud-providerconsul-service8006 

==pom 文件：==

```xml
<dependencies>
        <!--springcloud consul server-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-consul-discovery</artifactId>
        </dependency>

        <!-- springboot整合Web组件 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>

        <!-- 日常通用jar包 -->

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency><!-- 引入自己定义的api通用包，可以使用Payment支付Entity -->
            <groupId>com.atguigu.springcloud</groupId>
            <artifactId>cloud-api-commons</artifactId>
            <version>${project.version}</version>
        </dependency>
    </dependencies>
```

==yml文件==

```yml
server:
  port: 8006
spring:
  application:
    name: consul-provider-service
  cloud:
    consul:
      host: localhost
      port: 8500
      discovery:    # 指定注册对外暴露的服务名称
        service-name: ${spring.application.name}
```

==主启动类：==

```java
@SpringBootApplication
@EnableDiscoveryClient
public class ConsulProviderMain8006 {
    public static void main(String[] args) {
        SpringApplication.run(ConsulProviderMain8006.class,args);
    }
}
```

controller:其实也就是简单的打印而已

```java
@RestController
@Slf4j
public class PaymentController {
    @Value("${server.port}")
    private String serverPort;

    @RequestMapping("/payment/consul")
    public String paymentConsul(){
        return "springCloud with consul :" + serverPort + "\t" + UUID.randomUUID().toString();
    }
}
```

### 5.3 消费者

> 新建 一个 在82端口的 消费者模块。pom和yml和提供者的类似，==主启动类==不用说，记得==注入RestTemplate==

controller层：

```java
@RestController
public class CustomerConsulController {

    public static final String INVOKE_URL="http://consul-provider-service";

    @Resource
    private RestTemplate restTemplate;

    @RequestMapping("/customer/payment/consul")
    public String paymentInfo(){
        String result = restTemplate.getForObject(INVOKE_URL + "/payment/consul",String.class);
        return result;
    }
}
```

### 5.4 总结

<img src="images/1597384290665.png" alt="1597384290665" style="zoom:80%;" />

<img src="SpringCloud%E6%95%99%E7%A8%8B.assets/1597384344513.png" alt="1597384344513" style="zoom:80%;" />

<img src="SpringCloud%E6%95%99%E7%A8%8B.assets/1597384362750.png" alt="1597384362750" style="zoom:80%;" />

AP:

<img src="SpringCloud%E6%95%99%E7%A8%8B.assets/1597384508291.png" alt="1597384508291" style="zoom:80%;" />

CP：

<img src="images/1597384554249.png" alt="1597384554249" style="zoom:80%;" />

## 六、Ribbon-==服务调用==

> 我们这里提前启动好之前搭建好的eureaka Server集群（==两个eureka Server注册模块，两个提供者模块，一个消费者模块==）

<img src="SpringCloud%E6%95%99%E7%A8%8B.assets/image-20210304171921584.png" alt="image-20210304171921584" style="zoom:80%;" />

### 6.1 Ribbon简介

Spring Cloud Ribbon是基于Netflix Ribbon实现的一套==客户端负载均衡==工具

简单的说，Ribbon是Netflix发布的开源项目，主要功能是==提供客户端的负载均衡算法和服务调用==。Ribbon客户端组件提供一系列完善的配置项如连接超时，重试等。简单的说，就是在配置文件中列出==Load Balancer==（简称LB）后面的所有的机器，Ribbon会自动的帮助你==基于某种规则（如简单轮询，随机连接等）去连接这些机器==。我们很容易使用Ribbon实现自定义的负载均衡算法

<font color=red>LB负载均衡是什么</font>

简单的说就是将用户的请求平摊的分配到多个服务上，从而达到系统的HA（高可用）。

==常见的负载均衡有Nginx，LVS，硬件F5等==

Ribbon本地负载均衡客户端VS Nginx服务端负载均衡的区别

==Nginx服务器负载均衡==，客户端所有请求都会交给Ngix，然后由Nginx实现转发请求。即负载均衡是由服务端实现的。

Ribbon本地负载均衡，在调用微服务接口时候，会在==注册中心上获取注册信息服务列表之后缓存到JVM本地==，从而在本地实现RPC远程服务调用技术

<img src="images\1597385250463.png" alt="1597385250463" style="zoom: 67%;" />

![1597385267753](images\1597385267753.png)

上面在eureka时，确实实现了==负载均衡机制==，那是因为 eureka-client包里面自带着ribbon：

> 一句话，Ribbon 就是==负载均衡 + RestTemplate 调用==。实际上不止eureka的jar包有，==zookeeper的jar包，还有consul的jar包都包含了他==，就是上面使用的服务调用。

<img src="SpringCloud%E6%95%99%E7%A8%8B.assets/1597385486515.png" alt="1597385486515" style="zoom:80%;" />

如果自己添加，在 模块的 pom 文件中引入：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
</dependency>
```

对于RestTemplate 的一些说明：

> 有两种请求方式：post和get ,还有两种返回类型：object 和 Entity 

<img src="SpringCloud%E6%95%99%E7%A8%8B.assets/1597385762892.png" alt="1597385762892" style="zoom:80%;" />

RestTemplate 的 ForEntity 相比 ForObject特殊的地方:

![1597385959115](SpringCloud%E6%95%99%E7%A8%8B.assets/1597385959115.png)

就是 如果使用 ForObject 得到的就是提供者返回的对象，而如果要使用==ForEntity 得到时 ResponstEntity对象，使用getBody()==才能得到提供者返回的数据。

```java
	//使用forEnriry示例：
	@GetMapping("customer/payment/forEntity/{id}")
    public CommonResult<Payment> getPaymentById2(@PathVariable("id")Long id){
        
        ResponseEntity<CommonResult> entity = restTemplate.getForEntity(PAYMENY_URL + "/payment/" + id, CommonResult.class);
        
        if(entity.getStatusCode().is2xxSuccessful()){
            return entity.getBody();
        }else{
            return new CommonResult<>(444, "操作失败");
        }
    }
```

## 七、OpenFeign-==服务调用==

### 7.1 概述

> 这里和之前学的dubbo很像，例如==消费者的controller== 可以调用==提供者的 service层==方法，但是不一样，它貌似==只能调用提供者的 controller==，即写一个提供者项目的controller的接口，消费者来调用这个接口方法，就==还是相当于是调用提供者的 controller==，和RestTemplate 没有本质区别

<img src="SpringCloud%E6%95%99%E7%A8%8B.assets/1597389321006.png" alt="1597389321006" style="zoom:80%;" />

![1597389522738](images\1597389522738.png)

### 7.2 使用

> 新建一个消费者募模块。<font color=red>feign自带负载均衡配置</font>，所以不用手动配置

==pom ：==

```xml
	<dependencies>
        <!-- Open Feign -->
         <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>
        <!-- eureka Client -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
        <dependency><!-- 引入自己定义的api通用包，可以使用Payment支付Entity -->
            <groupId>com.atguigu.springcloud</groupId>
            <artifactId>cloud-api-commons</artifactId>
            <version>${project.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
```

==主启动类：==

```java
@SpringBootApplication
@EnableFeignClients   //关键注解
public class CustomerFeignMain80 {
    public static void main(String[] args) {
        SpringApplication.run(CustomerFeignMain80.class, args);
    }
}
```

==新建一个service==

> 这个service还是 customer 模块的接口，和提供者没有任何关系，不需要包类名一致。它使用起来就相当于是普通的service。
>
> ==推测大致原理，对于这个service 接口，读取它某个方法的注解==（GET或者POST注解不写报错），知道了请求方式和请求地址，而抽象方法，只是对于我们来讲，调用该方法时，可以进行传参等。

```java
@Component
@FeignClient(value = "CLOUD-PROVIDER-SERVICE")  //服务名称，要和eureka上面的一致才行
public interface PaymentFeignService {

    //这个就是provider 的controller层的方法定义,注意要一模一样。
    @GetMapping(value = "/payment/get/{id}")
    public CommonResult getPaymentById(@PathVariable("id")Long id);

}
```

==Controller层：==

```java
//使用起来就相当于是普通的service。
@RestController
public class CustomerFeignController {

    @Resource
    private PaymentFeignService paymentFeignService;

    @GetMapping("customer/feign/payment/{id}")
    public CommonResult<Payment> getPaymentById(@PathVariable("id") Long id){
        return paymentFeignService.getPaymentById(id);
    }
}

```

### <img src="SpringCloud%E6%95%99%E7%A8%8B.assets/image-20210306113548439.png" alt="image-20210306113548439" style="zoom:80%;" />

### 7.3 超时控制

> Openfeign默认超时等待为一秒，在消费者里面配置超时时间

<img src="images/1597467640971.png" alt="1597467640971" style="zoom:80%;" />

### 7.4 开启日志打印

首先写一个config配置类：

<img src="images/1597468025532.png" alt="1597468025532" style="zoom:80%;" />

然后在yml文件中开启日志打印配置：

<img src="images/1597468071030.png" alt="1597468071030" style="zoom:80%;" />

## ==中级部分==

> 主要是==服务降级、服务熔断、服务限流==的开发思想和框架实现

## 八、Hystrix-springcloud中级

> 官方地址：https://github.com/Netflix/Hystrix/wiki/How-To-Use

### 8.1 概述

#### 8.1.1 服务雪崩

多个微服务之间调用时候，假设微服务A调用微服务B和微服务C，微服务B和微服务C又调用其他的微服务，即所谓的==“扇出”==。如果扇出的链路上某个微服务的调用==响应时间过长或者不可用==，对微服务==A的调用就会占用越来越多的系统资源==，进而引起系统崩溃，即“雪崩效应”。

#### 8.1.2 Hystrix介绍

Hystrix是一个用于处理==分布式系统的延迟==和==容错==的开源库，在分布式系统中，许多依赖不可避免的会调用失败，比如==超时、异常==等。Hystrix能够保证在一个依赖出问题的情况下，==不会导致整体服务的失败，避免级联故障==，以提高分布式系统的弹性

“断路器”本身是一种开关装置，当某个服务单元发生故障之后，通过断路器的故障监控，==向调用方返回一个符合预期的、可处理的备选响应==，而不是长时间的等待或者抛出调用方无法处理的异常，这样就保证了服务调用方的线程不会被长时间、不必要的占用，从而避免了故障在分布式系统中的蔓延，乃至雪崩

#### 8.1.3 服务降级（fallback）

> > 服务降级是指 当服务器压力剧增的情况下，根据==实际业务情况及流量==，对一些服务和页面==有策略的不处理或换种简单的方式处理==，从而释放服务器资源以保证核心业务正常运作或高效运作。说白了，就是==尽可能的把系统资源让给优先级高的服务==。
> >   资源有限，而请求是无限的。如果在并发高峰期，不做服务降级处理，一方面肯定会影响整体服务的性能，严重的话可能会导致宕机某些重要的服务不可用。所以，一般在高峰期，为了保证核心功能服务的可用性，都要对某些服务降级处理。比如当双11活动时，把交易无关的服务统统降级，如查看蚂蚁深林，查看历史订单等等。
>
>   服务降级主要用于什么场景呢？当整个微服务架构整体的负载超出了预设的上限阈值或即将到来的流量预计将会超过预设的阈值时，为了保证重要或基本的服务能正常运行，可以将一些 不重要 或 不紧急 的服务或任务进行服务的 延迟使用 或 暂停使用。
>   降级的方式可以根据业务来，可以延迟服务，比如延迟给用户增加积分，只是放到一个缓存中，等服务平稳之后再执行 ；或者在粒度范围内关闭服务，比如关闭相关文章的推荐。
>
>
>   实现服务降级需要考虑几个问题
>
> 1）那些服务是==核心服务==，哪些服务是非核心服务
> 2）那些服务可以支持降级，那些服务不能支持降级，降级策略是什么
> 3）除服务降级之外是否存在更复杂的业务放通场景，策略是什么？
>   
>   自动降级分类
>   1）==超时降级==：主要配置好超时时间和超时重试次数和机制，并使用异步机制探测回复情况
>   2）==失败次数降级==：主要是一些不稳定的api，当失败调用次数达到一定阀值自动降级，同样要使用异步机制探测回复情况
>   3）==故障降级==：比如要调用的远程服务挂掉了（网络故障、DNS故障、http服务返回错误的状态码、rpc服务抛出异常），则可以直接降级。降级后的处理方案有：默认值（比如库存服务挂了，返回默认现货）、兜底数据（比如广告挂了，返回提前准备好的一些静态页面）、缓存（之前暂存的一些缓存数据）
>   4）==限流降级==：秒杀或者抢购一些限购商品时，此时可能会因为访问量太大而导致系统崩溃，此时会使用限流来进行限制访问量，当达到限流阀值，后续请求会被降级；降级后的处理方案可以是：排队页面（将用户导流到排队页面等一会重试）、无货（直接告知用户没货了）、错误页（如活动太火爆了，稍后重试）。

服务器==忙碌或者网络拥堵==时，==不让客户端等待并立刻返回一个友好提示==

> 程序运行异常
>
> 超时
>
> 服务熔断触发服务降级
>
> 线程池/信号量打满也会导致服务降级

#### 8.1.4 服务熔断(break)

> ==直接拒绝访问==，然后调用==服务降级的方法并返回友好提示==

> 就是保险丝

#### 8.1.5 服务限流（flow limit）

> 　熔断机制是应对雪崩效应的一种==微服务链路保户机制==，当扇出链路的==某个微服务不可用或者响应时间太长时==，会进行服务的降级，进而熔断该节点微服务的调用，快速返回错误的相应信息。==当检测当该节点微服务调用响应正常后恢复调用链路，熔断机制的注解是@HystrixCommand==
>
> 　　“熔断器”本身是一种开关装置，当某个服务单元发生故障之后，通过断路器的故障监控，，某个异常条件被触发，直接熔断整个服务。，向调用方法返回一个符合预期的、可处理的备选响应(FallBack),而不是长时间的等待或者抛出吊牌用方法无法处理的异常，就保证了服务调用方的线程不会被长时间占用，避免故障在分布式系统中蔓延，乃至雪崩

### 8.2 服务降级详解

新建==提供者端的moudle==：cloud-provider-hystrix-payment8001

> ==yml文件==

```yml
server:
  port: 8001
spring:
  application:
    name: cloud-provider-hystrix-payment

eureka:
  client:
    register-with-eureka: true
    fetch-registry: true
    service-url:
      defaultZone: http://eureka7001:7001/eureka,http://eureka7002:7002/eureka


```

> ==pom文件==

``` xml
<dependencies>
        <!-- hystrix -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
        </dependency>
        <!--eureka-client-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency><!-- 引入自己定义的api通用包，可以使用Payment支付Entity -->
            <groupId>com.atguigu.springcloud</groupId>
            <artifactId>cloud-api-commons</artifactId>
            <version>${project.version}</version>
        </dependency>
    </dependencies>
```

==主启动类：==

```java
@SpringBootApplication
@EnableEurekaClient
public class PaymentHystrixMain8001 {
    public static void main(String[] args) {
        SpringApplication.run(PaymentHystrixMain8001.class,args);
    }
}

```

==service层==：很简单的两个访问方法

```java
@Service
public class PaymentService {

    /**
     * 可以正常访问的方法
     * @param id
     * @return
     */
    public String paymentinfo_Ok(Integer id){
        return "线程池：" + Thread.currentThread().getName() + "--paymentInfo_OK，id:" + id;
    }

    /**
    超时访问的方法
    */
    public String paymentinfo_Timeout(Integer id){
        int interTime = 3;
        try{
            TimeUnit.SECONDS.sleep(interTime);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "线程池：" + Thread.currentThread().getName() + "--paymentInfo_Timeout，id:" + id + "耗时" + interTime + "秒钟--";
    }
}
```

==controller层：==

```java
@RestController
@Slf4j
public class PaymentController {

    @Resource
    private PaymentService paymentService;

    @Value("${server.port}")
    private String serverPort;

    @GetMapping("/payment/hystrix/{id}")
    public String paymentInfo_OK(@PathVariable("id")Integer id){
        log.info("paymentInfo_OKKKKOKKK");
        return paymentService.paymentinfo_Ok(id);
    }

    @GetMapping("/payment/hystrix/timeout/{id}")
    public String paymentInfo_Timeout(@PathVariable("id")Integer id){
        log.info("paymentInfo_timeout");
        return paymentService.paymentinfo_Timeout(id);
    }
}
```

#### 8.2.1 模拟高并发

> 这里使用一个新东西 JMeter 压力测试器
>
> 下载压缩包，解压，双击 /bin/ 下的 jmeter.bat 即可启动
>
> <img src="images/1597471683432.png" alt="1597471683432" style="zoom:80%;" />
>
> <img src="images/1597471750363.png" alt="1597471750363" style="zoom:80%;" />
>
> ctrl + S 保存。
>
> ![1597472029967](/images/1597472029967.png)

从测试可以看出，当模拟的==超长请求==被==高并发以后==，访问==普通的小请求速率也会被拉低==。

测试可见，当启动高并发测试时，消费者访问也会变得很慢，甚至出现超时报错

==降级思路==

> 提供者（8001)超时了，调用者（80）不能一直卡死等待，必须有服务降级
>
> 提供者（8001）挂了，调用者（80）不能一直卡死等待，必须有服务降级
>
> 提供者（8001）OK，调用者（80）自己出故障或者有自我要求（自己的等待时间小于提供者的等待时间）

#### 8.2.2 提供者端服务降级

一般服务降级放在客户端，即==消费者端==，但是==提供者端一样能使用==

> 首先提供者，即8001 先从自身找问题，==设置自身调用超时的峰值==，峰值内正常运行，超出峰值需要==有兜底的方法处理，作服务降级fallback==

==对8001的service层进行配置==:主要是==@HystrixCommand==注解和==@HystrixProperty==注解

```java
@HystrixCommand(fallbackMethod = "paymentInfo_timeoutHandler", commandProperties = {
            //设置峰值，超过 3 秒，就会调用兜底方法，这个时间也可以由feign控制
            @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds", value = "3000")
    })
    public String paymentinfo_Timeout(Integer id){
        ......会执行5秒.....
    }

	//兜底方法，根据上述配置，程序内发生异常、或者运行超时，都会执行该兜底方法
    public String paymentInfo_timeoutHandler(Integer id){
        .......
    }
}
```

==主启动类添加注解： @EnableCircuitBreaker==，即如下所示：

```java
@SpringBootApplication
@EnableEurekaClient
@EnableCircuitBreaker
public class PaymentHystrixMain8001 {
    public static void main(String[] args) {
        SpringApplication.run(PaymentHystrixMain8001.class,args);
    }
}
```

#### 8.2.3 消费者服务降级

##### 1、具体调用降级

> ==一、yml文件：==
>
> 注意添加：
>
> feign:
>   hystrix:
>     enabled: true

```yml
server:
  port: 80
spring:
  application:
    name: cloud-consumer-hystrix-payment

eureka:
  client:
    register-with-eureka: true
    fetch-registry: true
    service-url:
      defaultZone: http://eureka7001:7001/eureka,http://eureka7002:7002/eureka
feign:
  hystrix:
    enabled: true
```

> ==二、主启动：==
>
> ==// 以feign为服务调用==
>
> @EnableFeignClients
>
> ==// 开启Hystrix==
>
> ==@EnableHystrix==

```java
@SpringBootApplication
@Slf4j
@EnableFeignClients
@EnableHystrix
public class OrderHystrixMain80 {
    public static void main(String[] args) {
        SpringApplication.run(OrderHystrixMain80.class,args);
    }
}
```

> 三、Controller改动
>
> 注意，==利用feign调用提供端的代码不再赘述==
>
> 这里主要是==@HystrixCommand==的使用，和在提供端使用是一样

``` java
@RestController
@Slf4j
public class OrderHystrixController {
    @Resource
    private OrderHystrixService orderHystrixService;
    
   @GetMapping("/consumer/payment/hystrix/timeout/{id}")
   @HystrixCommand(fallbackMethod = "paymentTimeOutFallbackMethod",
            commandProperties = {@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1500")})
  public String paymentInfo_Timeout(@PathVariable("id") Integer id) {
      return orderHystrixService.paymentInfo_Timeout(id);
  }
    
   public String paymentTimeOutFallbackMethod(@PathVariable("id") Integer id) {
        return "我是消费者80，对方支付系统繁忙，请10秒钟后再试或自己运行出错，请检查自己";
    }

}
```

##### 2、全局服务降级

> 上面的降级策略，很明显造成了代码的杂乱，提升了耦合度，而且按照这样，每个方法都需要配置一个兜底方法，很繁琐。现在将降级处理方法（兜底方法）做一个全局的配置，设置==共有的兜底方法和独享的兜底方法==。

> 改动Controller即可
>
> 主要==使用@DefaultProperties注解==

```java
@RestController
@Slf4j
@DefaultProperties(defaultFallback = "payment_Global_FallbackMethod")
public class OrderHystrixController {
    @Resource
    private OrderHystrixService orderHystrixService;

    @GetMapping("/consumer/payment/hystrix/timeout/{id}")
    // 没有具体指明是哪种降级方法，所以使用的是全局的降级方法，
    // @HystrixCommand注解需要用到
    // @HystrixCommand 
    // @HystrixCommand 也可以不配置别的东西,默认使用全局,而且是一些运行异常时的降级策略
     // 不写自己的 fallbackMethod 属性，就使用全局默认的
    @HystrixCommand(commandProperties = {           @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds", value = "3000")
    })
    public String paymentInfo_Timeout(@PathVariable("id") Integer id) {
        // 比如...,则默认使用全局,如果没有特别指定
        int age=10/0;
        return orderHystrixService.paymentInfo_Timeout(id);

    }

    // 下面是全局fallback方法
    public String payment_Global_FallbackMethod(){
        return "Global 异常处理信息，请稍后重试";
    }
```

问题-跟业务逻辑混合，解决（解耦）：

> 在这种方式一般是在客户端，即消费者端，首先上面再controller中添加的  ==@HystrixCommand 和 @DefaultProperties== 两个注解去掉。就是保持原来的controller

1. ==yml文件配置==

```yml
server:
  port: 80
spring:
  application:
    name: cloud-customer-feign-hystrix-service
eureka:
  client:
    register-with-eureka: true
    fetch-registry: true
    service-url:
      defaultZone: http://eureka7001.com:7001/eureka/

# 用于服务降级 在注解@FeignClient 中添加 fallback 属性值
feign:
  hystrix:
    enabled: true  # 在feign中开启 hystrix
```

2. ==修改service 接口：==

> ==重点是:fallback属性==

```java
@Component											// 这里是重点
@FeignClient(value = "CLOUD-PROVIDER-HYSTRIX-PAYMENT", fallback = OrderFallbackService.class)
public interface OrderService {

    @GetMapping("/payment/hystrix/{id}")
    public String paymentInfo_OK(@PathVariable("id")Integer id);

    @GetMapping("/payment/hystrix/timeout/{id}")
    public String paymentInfo_Timeout(@PathVariable("id")Integer id);

}
```

3. ==fallback 指向的类：==

```java
package com.atguigu.springcloud.service;

import org.springframework.stereotype.Component;

@Component						
//注意这里，它实现了service接口
public class OrderFallbackService implements  OrderService{


    @Override
    public String paymentInfo_OK(Integer id) {
        return "OrderFallbackService --发生异常";
    }

    @Override
    public String paymentInfo_Timeout(Integer id) {
        return "OrderFallbackService --发生异常--paymentInfo_Timeout";
    }
}
```

新问题，这样配置如何设置超时时间？

> 首先要知道 下面两个 yml 配置项：
>
> ```properties
> hystrix.command.default.execution.timeout.enable=true    ## 默认值
> 
> ## 为false则超时控制有ribbon控制，为true则hystrix超时和ribbon超时都是用，但是谁小谁生效，默认为true
> 
> hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds=1000  ## 默认值
> 
> ## 熔断器的超时时长默认1秒，最常修改的参数
> ```
>
> 看懂以后，所以：
>
> 只需要在yml配置里面配置 Ribbon 的 超时时长即可。注意：hystrix 默认自带 ribbon包。
>
> ```yml
> ribbon:
> 	ReadTimeout: xxxx
> 	ConnectTimeout: xxx
> ```

### 8.3 服务熔断

> 实际上服务熔断 和 服务降级 没有任何关系，就像 java 和 javaScript
>
> 服务熔断，有点自我恢复的味道

![1597552755235](images/1597552755235.png)

![1597555271916](images/1597555271916.png)

![1597555381295](images/1597555381295.png)

以 8001 项目为示例：

主方法:==@EnableCircuitBreaker==

```java
@SpringBootApplication
@EnableEurekaClient
@EnableCircuitBreaker
public class PaymentHystrixMain8001 {
    public static void main(String[] args) {
        SpringApplication.run(PaymentHystrixMain8001.class,args);
    }
}
```

service层的方法设置服务熔断:

```java
 	//=====服务熔断
    @HystrixCommand(fallbackMethod = "paymentCircuitBreaker_fallback", commandProperties = {
            @HystrixProperty(name="circuitBreaker.enabled", value="true"),  // 是否开启断路器
            @HystrixProperty(name="circuitBreaker.requestVolumeThreshold", value="10"),  //请求次数
            @HystrixProperty(name="circuitBreaker.sleepWindowInMilliseconds", value="10000"), // 时间窗口期
            @HystrixProperty(name="circuitBreaker.errorThresholdPercentage", value="60"),  // 失败率达到多少后跳闸
            //整体意思：10秒内 10次请求，有6次失败，就跳闸
    })
    public String paymentCircuitBreaker(Integer id){
        //模拟发生异常
        if(id < 0){
            throw new RuntimeException("*****id，不能为负数");
        }
        String serialNumber = IdUtil.simpleUUID();
        return Thread.currentThread().getName() + "\t" + "调用成功，流水号：" + serialNumber;
    }

    public String paymentCircuitBreaker_fallback(Integer id){
        return "id 不能为负数，请稍后再试....";
    }
```

controller:

```java
	//====服务熔断
    @GetMapping("/payment/circuit/{id}")
    public String paymentCircuitBreaker(@PathVariable("id")Integer id){
        return paymentService.paymentCircuitBreaker(id);
    }
```

关于解耦以后的全局配置说明：

例如上面提到的全局服务降级，并且是feign+hystrix整合，即 service 实现类的方式，如何做全局配置？

> 上面有 做全局配置时，设置超时时间的方式，我们可以从中获得灵感，即在yml文件中 进行熔断配置：
>
> ```yml
> hystrix:
> command:
>  default:
>    circuitBreaker:
>      enabled: true
>      requestVolumeThreshold: 10
>      sleepWindowInMilliseconds: 10000
>      errorThresholdPercentage: 60
> ```

## 九、服务网关-SpringCloud Gateway

### 9.1 简介

![1597559672607](images/1597559672607.png)

![1597559829633](images/1597559829633.png)

<img src="images/1597559787064.png" alt="1597559787064" style="zoom: 67%;" />

### 9.2 入门配置

新建模块cloud-gateway-gateway9527

> 现在实现==通过Gateway来访问其它项目==，这里选择之前的8001项目，要求注册进Eureka Server。其它没要求

==pom文件==：

> 主要是==网关依赖==
>
> <!--gateway-->
>         <dependency>
>             <groupId>org.springframework.cloud</groupId>
>             <artifactId>spring-cloud-starter-gateway</artifactId>
>         </dependency>
>
> 同时==注意都要注册进eureka服务器==,所以有依赖==spring-cloud-starter-netflix-eureka-client==

```xml
<dependencies>
        <!--gateway-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-gateway</artifactId>
        </dependency>
        <!--eureka-client gateWay作为网关，也要注册进服务中心-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
    <!-- gateway和web不能同时存在，即web相关jar包不能导入 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency><!-- 引入自己定义的api通用包，可以使用Payment支付Entity -->
            <groupId>com.atguigu.springcloud</groupId>
            <artifactId>cloud-api-commons</artifactId>
            <version>${project.version}</version>
        </dependency>
    </dependencies>
```

==yml文件==

```yml
server:
  port: 9527
spring:
  application:
    name: cloud-gateway
  ## GateWay配置
  cloud:
    gateway:
      routes:
        - id: payment_routh  # 路由ID ， 没有固定的规则但要求唯一，建议配合服务名
          uri: http://localhost:8001  # 匹配后提供服务的路由地址
          predicates:
            - Path=/payment/get/**  # 断言，路径相匹配的进行路由

        - id: payment_routh2  # 路由ID ， 没有固定的规则但要求唯一，建议配合服务名
          uri: http://localhost:8001  # 匹配后提供服务的路由地址
          predicates:
            - Path=/payment/lb/**  # 断言，路径相匹配的进行路由

# 注册进 eureka Server
eureka:
  client:
    service-url:
      defaultZone: http://eureka7001:7001/eureka/
    register-with-eureka: true
    fetch-registry: true
```

==主启动类==,除了==注解@EnableEurekaClient==没什么特别配置

```java
@SpringBootApplication
@EnableEurekaClient
public class GatewayMain9527 {
    public static void main(String[] args) {
        SpringApplication.run(GatewayMain9527.class,args);
    }
}
```

访问测试：1 启动eureka Server，2 启动 8001 项目，3 启动9527（Gateway项目）

> 可见，当我们访问 http://localhost:9527/payment/get/1 时，即访问网关地址时，会给我们转发到 8001 项目的请求地址，以此作出响应。
>
> 加入网关前：http://localhost:8001/payment/get/1
>
> 加入网关后：http://localhost:9527/payment/get/1

### 9.3 动态配置

> 这里所谓的动态配置就是利用服务注册中心，来实现 负载均衡 的调用 多个微服务。
>
> 注意，这是==GateWay 的负载均衡==

上述yml配置修改如下：

```yml
spring:
  application:
    name: cloud-gateway
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true # 开启从注册中心动态创建路由的功能，利用微服务名进行路由
      routes:
        - id: payment_routh  # 路由ID ， 没有固定的规则但要求唯一，建议配合服务名
         # uri: http://localhost:8001  # 匹配后提供服务的路由地址
          uri: lb://CLOUD-PROVIDER-SERVICE
          predicates:
            - Path=/payment/get/**  # 断言，路径相匹配的进行路由

        - id: payment_routh2  # 路由ID ， 没有固定的规则但要求唯一，建议配合服务名
        #  uri: http://localhost:8001  # 匹配后提供服务的路由地址
          uri: lb://CLOUD-PROVIDER-SERVICE
          predicates:
            - Path=/payment/lb/**  # 断言，路径相匹配的进行路由

# uri: lb://CLOUD-PROVIDER-SERVICE  
# 解释：lb 属于GateWay 的关键字，代表是动态uri，即代表使用的是服务注册中心的微服务名，它默认开启使用负载均衡机制 
# 在微服务下CLOUD-PROVIDER-SERVICE的所有provider都能访问到
```

### 9.4 Predicate

> 注意到上面yml配置中，有个predicates 属性值。

<img src="images/1597563769828.png" alt="1597563769828" style="zoom: 50%;" />

具体使用：

<img src="images/1597563898772.png" alt="1597563898772" style="zoom: 67%;" />

<img src="images/1597564084571.png" alt="1597564084571" style="zoom:67%;" />

<img src="images/1597564241670.png" alt="1597564241670" style="zoom:67%;" />

<img src="images/1597564397523.png" alt="1597564397523" style="zoom: 67%;" />

> 放爬虫思路，前后端分离的话，只限定前端项目主机访问，这样可以屏蔽大量爬虫。
>
> 例如我加上： - Host=localhost:**       ** 代表允许任何端口
>
> 就只能是主机来访

配置错误页面:

> 注意，springboot默认/static/error/ 下错误代码命名的页面为错误页面，即 404.html
>
> 而且不需要导入额外的包，Gateway 里面都有。

### 9.5 Filter

> 主要是配置全局自定义过滤器，其它的小配置具体看官网吧

自定义全局过滤器配置类：

```java
@Component
public class GateWayFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        System.out.println("------come in MyGlobalFilter : "+ new Date());
        String uname = exchange.getRequest().getQueryParams().getFirst("uname");
        //合法性检验
        if(uname == null){
            System.out.println("----用户名为null,非法用户，请求不被接受");
            //设置 response 状态码   因为在请求之前过滤的，so就算是返回NOT_FOUND 也不会返回错误页面
            exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
            //完成请求调用
            return exchange.getResponse().setComplete();
        }
        return chain.filter(exchange);
    }

    //返回值是加载顺序，一般全局的都是第一位加载
    @Override
    public int getOrder() {
        return 0;
    }
}
```

## 十、服务配置-SpringCloud Config分布式配置中心

### 10.1 概述

![1597643368929](G:/%25E7%2599%25BE%25E5%25BA%25A6%25E4%25BA%2591%25E5%25AD%2598%25E5%2582%25A8/springCloud/images/1597643368929.png)

![1597643414093](images/1597643414093.png)

![1597643478588](images/1597643478588.png)

![1597643504380](images/1597643504380.png)

### 10.2 服务端配置

> 首先在github上新建一个仓库 springcloud-config
>
> 然后使用git命令克隆到本地，命令：git clone https://github.com/dexlace/springcloud-config.git
>
> 注意上面的操作不是必须的，只要github上有就可以，克隆到本地只是修改文件。

==新建 cloud-config-center3344 模块：==

pom文件：

> ​       <dependency>
> ​            <groupId>org.springframework.cloud</groupId>
> ​            <artifactId>spring-cloud-config-server</artifactId>
> ​        </dependency>

```xml
<dependencies>
        <!-- config Server -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-config-server</artifactId>
        </dependency>

        <!--eureka-client config Server也要注册进服务中心-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency><!-- 引入自己定义的api通用包，可以使用Payment支付Entity -->
            <groupId>com.atguigu.springcloud</groupId>
            <artifactId>cloud-api-commons</artifactId>
            <version>${project.version}</version>
        </dependency>
    </dependencies>
```

==yml 配置：==

```yml
server:
  port: 3344
spring:
  application:
    name: cloud-config-center  # 注册进eureka Server 的微服务名
  cloud:
    config:
      server:
        git:
          uri: https://github.com/dexlace/springcloud-config.git # github 仓库位置，不建议用ssh那个版本的地址
          ## 搜索目录
          search-paths:
            - springcloud-config
          # 读取的分支
          label: master
eureka:
  client:
    service-url:
      defaultZone: http://eureka7001:7001/eureka/,http://eureka7002:7002/eureka/
```

==主启动类：==

```java
@SpringBootApplication
@EnableConfigServer   //关键注解
public class ConfigCenterMain3344 {
    public static void main(String[] args) {
        SpringApplication.run(ConfigCenterMain3344.class,args);
    }
}
```

==添加模拟映射：【C:\Windows\System32\drivers\etc\hosts】文件中添加：  127.0.0.1 config-3344.com==

==启动微服务3344，访问http://config-3344.com:3344/master/config-dev.yml 文件（注意，要提前在git上弄一个这文件==

==文件命名和访问的规则：==

![1597646186970](G:/%25E7%2599%25BE%25E5%25BA%25A6%25E4%25BA%2591%25E5%25AD%2598%25E5%2582%25A8/springCloud/images/1597646186970.png)

==不加分支名默认是master:==

![1597646308915](G:/%25E7%2599%25BE%25E5%25BA%25A6%25E4%25BA%2591%25E5%25AD%2598%25E5%2582%25A8/springCloud/images/1597646308915.png)

### 10.3 客户端配置

> 这里的客户端指的是，使用 Config Server 统一配置文件的项目。==既有之前说的消费者，又有提供者==

新建cloud-config-client3355模块

==pom文件：==

> 主要是依赖
>
> ​        <dependency>
> ​            <groupId>org.springframework.cloud</groupId>
> ​            <artifactId>spring-cloud-starter-config</artifactId>
> ​        </dependency>

```xml
<dependencies>
        <!-- config Client 和 服务端的依赖不一样 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-config</artifactId>
        </dependency>

        <!--eureka-client config Server也要注册进服务中心-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency><!-- 引入自己定义的api通用包，可以使用Payment支付Entity -->
            <groupId>com.atguigu.springcloud</groupId>
            <artifactId>cloud-api-commons</artifactId>
            <version>${project.version}</version>
        </dependency>
    </dependencies>
```

==**bootstrap.yml文件**==

![1597646809831](SpringCloud%E6%95%99%E7%A8%8B.assets/1597646809831.png)

```yml
server:
  port: 3355

spring:
  application:
    name: config-client
  cloud:
    # config 客户端配置
    config:
      label: master # 分支名称
      name: config # 配置文件名称
      profile: dev # 使用配置环境
      uri: http://config-3344:3344  # config Server 地址
      # 综合上面四个 即读取配置文件地址为： http://config-3344:3344/master/config-dev.yml

eureka:
  client:
    service-url:
      defaultZone: http://eureka7001:7001/eureka/,http://eureka7002:7002/eureka/
```

注意，==不能少了application.yml文件==，就算是空的也要留着

==主启动类，极其普通：==

```java
@SpringBootApplication
@EnableEurekaClient
public class ConfigClientMain3355 
    public static void main(String[] args) {
        SpringApplication.run(ConfigClientMain3355.class, args);
    }
}
```

==controller层，测试读取配置信息==

```java
package com.atguigu.springcloud.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigClientController {

    @Value("${config.info}")
    private String configInfo;

    @GetMapping("/configInfo")
    public String getConfigInfo(){
        return configInfo;
    }
}
```

启动测试完成！如果报错，注意github上的 yml 格式有没有写错！ 

### 10.4 动态刷新

问题：

<img src="images/1597649646383.png" alt="1597649646383" style="zoom:80%;" />

> 就是github上面配置更新了，config Server 项目上是动态更新的，但是，client端的项目中的配置，目前还是之前的，它不能动态更新，必须重启才行。

解决：

1. ==client端一定要有如下依赖：==

   <img src="images/1597649915331.png" alt="1597649915331" style="zoom:80%;" />

2. ==client 端增加 yml 配置如下，即在 bootstrap.yml 文件中：==

```yml
# 暴露监控端点
management:
  endpoints:
    web:
      exposure:
        include: "*"
```

3. ==在controller 上添加如下注解：==

<img src="images/1597650236798.png" alt="1597650236798" style="zoom:80%;" />

> 到此为止，配置已经完成，但是测试仍然不能动态刷新，需要下一步。

4. ==向 client 端发送一个 POST 请求==

> 如 curl -X POST "http://localhost:3355/actuator/refresh"
>
> 两个必须：1.必须是 POST 请求，2.请求地址：http://localhost:3355/actuator/refresh

成功！

但是又有一个问题，就是要==向每个微服务发送一次POST请求==，==当微服务数量庞大，又是一个新的问题。==

就有下面的消息总线！

## 十一、消息总线

### 11.1 简介

![1597651086179](SpringCloud%E6%95%99%E7%A8%8B.assets/1597651086179.png)

![1597651183100](SpringCloud%E6%95%99%E7%A8%8B.assets/1597651183100.png)

==前提安装RabbitMQ==,略

### 11.2 广播式刷新配置

<img src="SpringCloud%E6%95%99%E7%A8%8B.assets/1597723463350.png" alt="1597723463350" style="zoom:80%;" />

#### 11.2.1 Config Server配置

> 以cloud-config-center3344为例改造config server
>
> ==一、添加依赖，该依赖其实client也需要添加==

``` xml
<!-- 添加rabbitMQ的消息总线支持包 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-bus-amqp</artifactId>
        </dependency>
```

> 二、application.yml增加如下配置
>
> ==主要是关于rabbitmq的配置和bus刷新暴露的端点==
>
> 整个yml文件如下：

```yml
server:
  port: 3344
spring:
  application:
    name: cloud-config-center  # 注册进eureka Server 的微服务名
  cloud:
    config:
      server:
        git:
          uri: https://github.com/dexlace/springcloud-config.git # github 仓库位置
          ## 搜索目录
          search-paths:
            - springcloud-config
          # 读取的分支
          label: master
  # rabbitMq的相关配置
  rabbitmq:
    host: 192.168.205.107
    port: 5672  # 这里没错，虽然rabbitMQ网页是 15672
    username: guest
    password: guest


eureka:
  client:
    service-url:
      defaultZone: http://eureka7001:7001/eureka/,http://eureka7002:7002/eureka/


# rabbitmq 的相关配置2 暴露bus刷新配置的端点
management:
  endpoints:
    web:
      exposure:
        include: 'bus-refresh'
```

其他照旧，具体参照第十章的内容

#### 11.2.2 Config Client配置

> 一、添加依赖，和Config Server的一样

```xml
<!-- 添加rabbitMQ的消息总线支持包 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-bus-amqp</artifactId>
        </dependency>
```

> 二、Client的bootstrap文件配置如下
>
> 复制了一份，重建了一个微服务，端口号是3366，这里以端口号3355为例，二者除了端口号没区别

```yml
server:
  port: 3355

spring:
  application:
    name: config-client
  cloud:
    # config 客户端配置
    config:
      label: master # 分支名称
      name: config # 配置文件名称
      profile: dev # 使用配置环境
      uri: http://config-3344:3344  # config Server 地址
      # 综合上面四个 即读取配置文件地址为： http://config-3344:3344/master/config-dev.yml
  rabbitmq:
    host: 192.168.205.107
    port: 5672
    username: guest
    password: guest
eureka:
  client:
    service-url:
      defaultZone: http://eureka7001:7001/eureka/,http://eureka7002:7002/eureka/
# 暴露监控端点
management:
  endpoints:
    web:
      exposure:
        include: "*"

```

可在github上修改yml文件进行测试，修改完文件，向 config server 发送 请求：

==【curl -X POST "http://localhost:3344/actuator/bus-refresh"】==

> 注意，之前是==向config client 一个个发送请求==，但是这次是==向 config Server 发送请求==，而所有的config client 的配置也都全部更新。

注：这是在rabbitmq上注册的virtual host

<img src="SpringCloud%E6%95%99%E7%A8%8B.assets/image-20210321205051579.png" alt="image-20210321205051579" style="zoom:67%;" />

### 11.3 定点通知

<img src="SpringCloud%E6%95%99%E7%A8%8B.assets/1597725204568.png" alt="1597725204568" style="zoom:80%;" />

![1597725365978](images\1597725365978.png)

## 十二、消息驱动

<img src="SpringCloud%E6%95%99%E7%A8%8B.assets/1597725567239.png" alt="1597725567239" style="zoom: 67%;" />

> ==屏蔽底层消息中间件的差异==，降低切换成本，统一消息的编程模型

> 官方定义的Spring Cloud Stream是一个构建==消息驱动微服务的框架==
>
> 应用程序通过==input或者output==来与Spring Cloud Stream中的==binder对象交互==
>
> 通过我们配置来binding(绑定)，而Sping Cloud Steam的==binder对象==负责==与消息中间贱进行交互==
>
> 所以，我们只需要搞清楚如何与Spring Cloud Stream交互就可以方便的使用消息驱动方式
>
> Spring Cloud Stream为一些供应商的消息中间件产品提供了个性化的自动化配置实现，引用了==发布-订阅、消费组、分区==的三个核心概念。

<font color=red>目前只支持RabbitMQ、Kafka</font>

<img src="SpringCloud%E6%95%99%E7%A8%8B.assets/1597726446212.png" alt="1597726446212" style="zoom:80%;" />

<img src="images\1597730581088.png" alt="1597730581088" style="zoom:80%;" />

### 12.1 消息生产者

新建模块 cloud-stream-rabbitmq-provider8801 

> ==pom文件==

```xml
 <!-- stream-rabbit -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-stream-rabbit</artifactId>
    </dependency>
    <!--eureka-client 目前，这个不是必须的-->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <scope>runtime</scope>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency><!-- 引入自己定义的api通用包，可以使用Payment支付Entity -->
        <groupId>com.atguigu.springcloud</groupId>
        <artifactId>cloud-api-commons</artifactId>
        <version>${project.version}</version>
    </dependency>
```

==application.yml==

> 注意要==配置rabbitmq选项==，而==不只是在stream中配置==

```yml

server:
  port: 8801
spring:
  rabbitmq:
    host: 192.168.205.107
    port: 5672
    username: guest
    password: guest
  application:
    name: cloud-stream-provider
  cloud:
    stream:
      binders: # 在次配置要绑定的rabbitMQ的服务信息
        defaultRabbit: # 表示定义的名称，用于和binding整合
          type: rabbit  # 消息组件类型
          environment:  # 设置rabbitmq的相关环境配置
            spring:
              rabbitmq:
                host: 192.168.205.107
                port: 5672
                username: guest
                password: guest
      bindings:  # 服务的整合处理
        output:   # 表示是生产者，向rabbitMQ发送消息
          destination: studyExchange  # 表示要使用的Exchange名称
          content-type: application/json  # 设置消息类型，本次是json，文本是 "text/plain"
          binder: defaultRabbit  # 设置要绑定的消息服务的具体配置
eureka:
  client:
    service-url:
      defaultZone: http://eureka7001:7001/eureka/,http://eureka7002:7002/eureka/
  instance:
    lease-renewal-interval-in-seconds: 2 # 设置心跳时间，默认是30秒
    lease-expiration-duration-in-seconds: 5 # 最大心跳间隔不能超过5秒,默认90秒
    instance-id: send-8801.com # 在信息列表显示主机名称
    prefer-ip-address: true # 访问路径变为ip地址


```

主启动类没什么特殊的注解。

>  ==业务类==：（此业务类不是以前的service，而实负责推送消息的服务类）用的是==@EnableBingding注解==

```java
package com.atguigu.springcloud.service.impl;

import com.atguigu.springcloud.service.IMessageProvider;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

import javax.annotation.Resource;
import java.util.UUID;


/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/3/22
 */
@EnableBinding(Source.class) //定义消息的推送管道
public class IMessageProviderImpl implements IMessageProvider {

    @Resource
    private MessageChannel output; // 消息发送管道

    @Override
    public String send() {
        String serial= UUID.randomUUID().toString();
        output.send(MessageBuilder.withPayload(serial).build());
        System.out.println("***serial: " + serial);
        return null;
    }
}

```

> controller

```java
@RestController
public class SendMessageController {

    @Resource
    private IMessageProvider messageProvider;

    @GetMapping("/sendMessage")
    public String sendMessage(){
        return messageProvider.send();
    }
}
```

<img src="SpringCloud%E6%95%99%E7%A8%8B.assets/image-20210323162735398.png" alt="image-20210323162735398" style="zoom:80%;" />

### 12.2 消息消费者

新建模块 cloud-stream-rabbitmq-consumer8802

pom依赖和生产者一样。

yml配置: 在 stream的配置上，和生产者只有一处不同的地方，==output 改成 input==

```yml

server:
  port: 8802
spring:
  rabbitmq:
    host: 192.168.205.107
    port: 5672
    username: guest
    password: guest
  application:
    name: cloud-stream-consumer
  cloud:
    stream:
      binders: # 在次配置要绑定的rabbitMQ的服务信息
        defaultRabbit: # 表示定义的名称，用于和binding整合
          type: rabbit  # 消息组件类型
          environment:  # 设置rabbitmq的相关环境配置
            spring:
              rabbitmq:
                host: 192.168.205.107
                port: 5672
                username: guest
                password: guest
      bindings:  # 服务的整合处理
        input:   # 表示是生产者，向rabbitMQ发送消息
          destination: studyExchange  # 表示要使用的Exchange名称
          content-type: application/json  # 设置消息类型，本次是json，文本是 "text/plain"
          binder: defaultRabbit  # 设置要绑定的消息服务的具体配置

eureka:
  client:
    service-url:
      defaultZone: http://eureka7001:7001/eureka/,http://eureka7002:7002/eureka/
  instance:
    lease-renewal-interval-in-seconds: 2 # 设置心跳时间，默认是30秒
    lease-expiration-duration-in-seconds: 5 # 最大心跳间隔不能超过5秒,默认90秒
    instance-id: send-8801.com # 在信息列表显示主机名称
    prefer-ip-address: true # 访问路径变为ip地址
```

> 接收消息的业务类
>
> 主要是==@EnableBinding(Sink.class)==
>
> ==@StreamListener(Sink.INPUT)==

```yml
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@EnableBinding(Sink.class)
public class ConsumerController {

    @Value("${server.port}")
    private String serverPort;

    @StreamListener(Sink.INPUT)
    public void input(Message<String> message){
        System.out.println("消费者1号，serverport: " + serverPort + "，接受到的消息：" + message.getPayload());
    }
}
```

### 12.3 配置分组消费

新建 cloud-stream-rabbitmq-consumer8802 模块：

> 8803 就是 8802 clone出来的。

当运行时，会有两个问题。

第一个问题，两个消费者都接收到了消息，这属于重复消费。例如，消费者进行订单创建，这样就创建了两份订单，会造成系统错误。

![1597731525531](SpringCloud%E6%95%99%E7%A8%8B.assets/1597731525531.png)

> Stream默认不同的微服务是不同的组

![1597731630685](SpringCloud%E6%95%99%E7%A8%8B.assets/1597731630685.png)

对于重复消费这种问题，导致的原因是默认每个微服务是不同的group，==组流水号不一样，所以被认为是不同组，两个都可以消费。==

解决的办法就是自定义配置分组：

消费者 yml 文件配置：

```yml
	# 8802 的消费者
	bindings:
        input:   
          destination: studyExchange  
          content-type: application/json  
          binder: defaultRabbit  
          group: juddy  # 自定义分组配置
    # 8803 的消费者
	bindings:
        input:   
          destination: studyExchange  
          content-type: application/json  
          binder: defaultRabbit  
          group: buddy  # 自定义分组配置
```

另外：

> ==加上了group配置，就已经实现了消息的持久化==

> 分布式请求链路跟踪，超大型系统。需要在微服务模块极其多的情况下，比如80调用8001的，8001调用8002的，这样就形成了一个链路，如果链路中某环节出现了故障，我们可以使用Sleuth进行链路跟踪，从而找到出现故障的环节。

## 十三、链路追踪-Sleuth

> 分布式请求链路跟踪，超大型系统。需要在微服务模块极其多的情况下，比如80调用8001的，8001调用8002的，这样就形成了一个链路，如果链路中某环节出现了故障，我们可以使用Sleuth进行链路跟踪，从而找到出现故障的环节。

### 13.1 简介

<img src="images/1597732562780.png" alt="1597732562780" style="zoom:80%;" />

> sleuth 负责跟踪，而zipkin负责展示。
>
> zipkin 下载地址： http://dl.bintray.com/openzipkin/maven/io/zipkin/java/zipkin-server/2.12.9/zipkin-server-2.12.9-exec.jar
>
> 使用 【java -jar】 命令运行下载的jar包，访问地址：【 http://localhost:9411/zipkin/ 】

### 13.2 小case

> 使用之前的 提供者8001 和 消费者80

分别给他们引入依赖：

```xml
	<!-- 引入sleuth + zipkin -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-zipkin</artifactId>
        </dependency>
```

yml增加配置：

```yml
spring:
  zipkin:
    base-url: http://localhost:9411  # zipkin 地址
  sleuth:
    sampler:
      # 采样率值 介于0-1之间 ，1表示全部采集
      probability: 1
```

启动==访问http://localhost:9411/zipkin/就可以看到调用链路情况==

## 高级

> alibaba 的 github上有中文文档

![1597735215211](SpringCloud%E6%95%99%E7%A8%8B.assets/1597735215211.png)

## 十四、 Spring Cloud Alibaba之Nacos

### 14.1 简介

> Nacos = Eureka + Config + Bus

> github地址：  https://github.com/alibaba/Nacos 
>
> Nacos 地址：  https://nacos.io/zh-cn/ 

<img src="SpringCloud%E6%95%99%E7%A8%8B.assets/1597755893534.png" alt="1597755893534" style="zoom:80%;" />

> nacos可以切换 AP 和 CP ,可使用如下命令切换成CP模式：
>
> ![1597756097369](G:/%25E7%2599%25BE%25E5%25BA%25A6%25E4%25BA%2591%25E5%25AD%2598%25E5%2582%25A8/springCloud/images/1597756097369.png)

==下载：==

> 下载地址：  https://github.com/alibaba/nacos/releases/tag/1.1.4 
>
> 直接下载网址： https://github.com/alibaba/nacos/releases/download/1.1.4/nacos-server-1.1.4.zip
>
> 下载压缩包以后解压，进入bin目录，打开dos窗口，执行startup命令启动它。
>
> 可访问 ： 【 http://192.168.101.105:8848/nacos/index.html】地址，默认账号密码都是nacos

### 14.2 注册中心

#### 14.2.1 提供者

新建模块 cloudalibaba-provider-payment9001

==pom依赖：==

```xml
	 <dependencies>
    <!-- springcloud alibaba nacos 依赖 -->
    <dependency>
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
    </dependency>
    <!-- springboot整合Web组件 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>

    <!-- 日常通用jar包 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <scope>runtime</scope>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency><!-- 引入自己定义的api通用包，可以使用Payment支付Entity -->
        <groupId>com.atguigu.springcloud</groupId>
        <artifactId>cloud-api-commons</artifactId>
        <version>${project.version}</version>
    </dependency>
</dependencies>
```

==yml 配置：==

```yml
server:
  port: 9001
spring:
  application:
    name: nacos-payment-provider
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
management:
  endpoints:
    web:
      exposure:
        include: '*'
```

==主启动类==

```java
// 注意注解
@EnableDiscoveryClient
```

==业务类==,很简单，不用赘述

```java
@RestController
public class PaymentController9001 {

    @Value("${server.port}")
    private String serverPort;

    @GetMapping(value="/payment/nacos/{id}")
    public String getPayment(@PathVariable("id") Integer id){
        return "nacos registry, serverPort: "+serverPort+"\t id"+id;
    }
}

```

<font color=red>Nacos 自带负载均衡机制</font>，下面创建第二个提供者。

新建 cloudalibaba-provider-payment9002提供者模块，clone 9001 就可以，或者==指定端口号运行多个实例就好了==

#### 14.2.2 消费者

新建消费者 模块： cloudalibaba-customer-order80

pom依赖和主启动类没有好说的，l和提供者一致，yml依赖也是类似配置，作为消费者注册进nacos服务中心。

nacos底层也是ribbon，注入ReatTemplate

```java
@Configuration
public class ApplicationContextConfig {

    @Bean
    @LoadBalanced
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }
}
```

controller :

```java
@RestController
public class OrderController {

    //在yml里面写的提供者服务路径，  值为：http://nacos-provider
    @Value("${service-url.nacos-user-service}")
    private String nacos_user_service;

    @Resource
    private RestTemplate restTemplate;

    @GetMapping("customer/nacos/{id}")
    public String orderId(@PathVariable("id")Integer id){
        return restTemplate.getForObject(nacos_user_service + "/payment/nacos/" + id, String.class);
    }
}
```

### 14.3 配置中心

> nacos还可以作为服务配置中心
>
> nacos 作为配置中心，不需要像springcloud config 一样做一个Server端模块。

新建模块 cloudalibaba-nacos-config3377

==pom依赖：==

```xml
<dependencies>
        <!-- 以 nacos 做服务配置中心的依赖 -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
        </dependency>
        <!-- springcloud alibaba nacos 依赖 -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        </dependency>
        <!-- springboot整合Web组件 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>

        <!-- 日常通用jar包 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency><!-- 引入自己定义的api通用包，可以使用Payment支付Entity -->
            <groupId>com.atguigu.springcloud</groupId>
            <artifactId>cloud-api-commons</artifactId>
            <version>${project.version}</version>
        </dependency>
    </dependencies>
```

==主启动类，注意有@EnableDiscoveryClient注解==

```java
@SpringBootApplication
@EnableDiscoveryClient
public class CloudAlibabaConfigMain3377 {
    public static void main(String[] args) {
        SpringApplication.run(CloudAlibabaConfigMain3377.class,args);
    }
}
```

==bootstrap.yml配置==

> 注意file-extension很不智能，支持yaml和properties，==但是yaml和yml虽然性质一样，但是要严格区分，这是nacos的问题==

```yml
server:
  port: 3377
spring:
  application:
    name: nacos-config-client
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848  # nacos作为服务注册中心
      config:
        server-addr: localhost:8848 # nacos作为服务配置中心
        file-extension: yml # 指定yml 格式的配置
```

==application.yml配置==

```yml
spring:
  profiles:
#    active: dev
    active: test

```

controller 层进行读取配置测试：

> 注意支持Nacos的动态刷新
>
> ==@RefreshScope==

```java
@RestController
@RefreshScope  //支持Nacos的动态刷新
public class ConfigClientController {

    @Value("${config.info}")
    private String configInfo;

    @GetMapping("configclient/getconfiginfo")
    public String getConfigInfo(){
        return configInfo;
    }
}
```

<font color=red>注意以下是Nacos中添加配置文件，遵循如下规则</font>

在 Nacos Spring Cloud 中，`dataId` 的完整格式如下：

```plain
${prefix}-${spring.profiles.active}.${file-extension}
```

- `prefix` 默认为 `spring.application.name` 的值，也可以通过配置项 `spring.cloud.nacos.config.prefix`来配置。
- `spring.profiles.active` 即为当前环境对应的 profile，详情可以参考 [Spring Boot文档](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-profiles.html#boot-features-profiles)。 **注意：当 `spring.profiles.active` 为空时，对应的连接符 `-` 也将不存在，dataId 的拼接格式变成 `${prefix}.${file-extension}`**
- `file-exetension` 为配置内容的数据格式，可以通过配置项 `spring.cloud.nacos.config.file-extension` 来配置。目前只支持 `properties` 和 `yaml` 类型。

综合以上说明，Nacos 的dataid（类似文件名）应为： ==nacos-config-client-dev.yml  (必须是yml)==

当修改配置值，==会发现 3377 上也已经修改，Nacos自带自动刷新功能！==而不需要运维人员的多此一举

==<font color=red>Nacos的Group和namespace</font>==

> Nacos 的 Group ,默认创建的配置文件，都是在DEFAULT_GROUP中，可以在创建配置文件时，给文件指定分组。

<img src="SpringCloud%E6%95%99%E7%A8%8B.assets/image-20210325181009467.png" alt="image-20210325181009467" style="zoom:80%;" />

Nacos 的namespace ,默认的命名空间是public ,这个是不允许删除的，可以创建一个新的命名空间，会自动==给创建的命名空间一个流水号==。

最后，dataid、group、namespace 三者关系如下：（不同的dataid，是相互独立的，不同的group是相互隔离的，不同的namespace也是相互独立的）

<img src="SpringCloud%E6%95%99%E7%A8%8B.assets/1597808385154.png" alt="1597808385154" style="zoom:80%;" />

### 14.4 Nacos持久化

> 上面只是小打小闹，下面才是真正的高级操作。

<img src="images/1597808678658.png" alt="1597808678658" style="zoom:80%;" />

> ==搭建集群必须持久化==，不然多台机器上的nacos的配置信息不同，造成系统错乱。它不同于单个springcloud config，没有集群一说，而且数据保存在github上，也不同于eureka，配置集群就完事了，没有需要保存的配置信息。
>
> Nacos默认使用它自带的嵌入式数据库derby:
>
> ![1597809107487](images/1597809107487.png)

Nacos持久化配置：

> 在 nacos的 conf目录下，有个==nacos-mysql.sql 的sql文件==，创建一个名为【nacos_config】的数据库，执行里面内容，在nacos_config数据库里面创建数据表。
>
> 找到conf/application.properties 文件，尾部追加如下内容：
>
> ```properties
> spring.datasource.platform=mysql
> 
> db.num=1
> db.url.0=jdbc:mysql://localhost:3306/nacos_config?characterEncoding=utf-8&connectTimeout=1000&socketTimeout=3000&autoReconnect=true
> db.user=root
> db.password=123456
> ```
>
> ==重启nacos，即完成持久化配置。==
>
> 注意：linux启动nacos时默认是集群启动的，如果是单机启动需要，加==-m standalone==

### 14.5 集群架构

> 现在进行企业中真正需要的nacos集群配置，而不是上面的单机模式，需要准备如下：
>
> 一台linux虚拟机：nginx服务器，3个nacos服务，一个mysql数据库。
>
> nginx的安装参考之前学，使用 ContOs7 至少需要安装gcc库，不然无法编译安装【yum install gcc】
>
> nacos下载linux版本的 tar.gz 包：https://github.com/alibaba/nacos/releases/download/1.1.4/nacos-server-1.1.4.tar.gz
>

==Nacos集群配置==

1. 首先对 nacos 进行==持久化操作==，操作如上面一致。

2. 修改 nacos/conf 下的cluster文件，最好先复制一份，添加如下内容:

   ![image-20210326234844154](SpringCloud%E6%95%99%E7%A8%8B.assets/image-20210326234844154.png)

3. 模拟三台nacos服务，编辑nacos的startup启动脚本，使他能够支持不同的端口启动多次。

   这里是==伪集群==，不是在不同机器上布置nacos服务，所以==修改startup脚本==，使得其能==按照端口启动不同的实例==。

   <img src="SpringCloud%E6%95%99%E7%A8%8B.assets/image-20210326235254248.png" alt="image-20210326235254248" style="zoom:80%;" />

   ![image-20210326235357048](SpringCloud%E6%95%99%E7%A8%8B.assets/image-20210326235357048.png)

4. nginx配置负载均衡：

<img src="SpringCloud%E6%95%99%E7%A8%8B.assets/image-20210327000048304.png" alt="image-20210327000048304" style="zoom:80%;" />



5. 测试完成！

使用 9003 模块注册进Nacos Server 并获取它上面配置文件的信息，进行测试。

## 十五、Spring Cloud Alibaba之Sentinel

### 15.1 简介

> sentinel在springcloud Alibaba的作用是实现==熔断和限流==

Sentinel 具有以下特征:

- **丰富的应用场景**：Sentinel 承接了阿里巴巴近 10 年的双十一大促流量的核心场景，例如==秒杀（即突发流量控制在系统容量可以承受的范围）、消息削峰填谷、集群流量控制、实时熔断下游不可用应用==等。
- **完备的实时监控**：Sentinel 同时提供==实时的监控功能==。您可以在控制台中看到==接入应用的单台机器秒级数据==，甚至 500 台以下规模的集群的汇总运行情况。
- **广泛的开源生态**：Sentinel 提供==开箱即用的与其它开源框架/库的整合模块==，例如与 Spring Cloud、Dubbo、gRPC 的整合。您只需要引入相应的依赖并进行简单的配置即可快速地接入 Sentinel。
- **完善的 SPI 扩展点**：Sentinel ==提供简单易用、完善的 SPI 扩展接口==。您可以通过实现扩展接口来==快速地定制逻辑==。例如定制规则管理、适配动态数据源等。

Sentinel 分为两个部分:

- ==核心库（Java 客户端）==不依赖任何框架/库，能够运行于所有 Java 运行时环境，同时对 Dubbo / Spring Cloud 等框架也有较好的支持。
- ==控制台（Dashboard）==基于 Spring Boot 开发，打包后可以直接运行，不需要额外的 Tomcat 等应用容器。

<img src="SpringCloud%E6%95%99%E7%A8%8B.assets/image-20210327153115873.png" alt="image-20210327153115873" style="zoom:80%;" />

再次厘清==熔断、降级和限流的关系==

熔断强调的是==<font color=red>服务之间的调用能实现自我恢复的状态；</font>==

限流是==从系统的流量入口考虑==，从进入的流量上进行限制，达到保护系统的作用；

降级，是==从系统内部的平级服务或者业务的维度考虑，流量大了，可以干掉一些==，保护其他正常使用；

==<font color=red>熔断是降级方式的一种；</font>==

==<font color=red>降级又是限流的一种方式；</font>==

三者都是为了通过一定的方式去保护流量过大时，保护系统的手段。

### 15.2 简单入门案例

> 新建模块cloudalibaba-sentinel-service8041，使用Nacos作为服务注册中心，测试Sentinel的功能

==pom依赖：==

```xml
 <dependencies>
        <!-- 后续做Sentinel的持久化会用到的依赖 -->
        <dependency>
            <groupId>com.alibaba.csp</groupId>
            <artifactId>sentinel-datasource-nacos</artifactId>
        </dependency>
        <!-- sentinel  -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
        </dependency>
        <!-- springcloud alibaba nacos 依赖,Nacos Server 服务注册中心 -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        </dependency>
        <!-- springboot整合Web组件 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>

        <!-- 日常通用jar包 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency><!-- 引入自己定义的api通用包，可以使用Payment支付Entity -->
            <groupId>com.atguigu.springcloud</groupId>
            <artifactId>cloud-api-commons</artifactId>
            <version>${project.version}</version>
        </dependency>
    </dependencies>

```

==yml 配置：==

```yml
server:
  port: 8401
spring:
  application:
    name: cloudalibaba-sentinel-service
  cloud:
    nacos:
      discovery:
        # 服务注册中心
        server-addr: localhost:8848
    sentinel:
      transport:
        # 配置 Sentinel Dashboard 的地址
        dashboard: localhost:8080
        # 默认8719 ，如果端口被占用，端口号会自动 +1，提供给 sentinel 的监控端口
        port: 8719
management:
  endpoints:
    web:
      exposure:
        include: '*'
```

主启动类和简单controller：

```java
//主启动类
@SpringBootApplication
@EnableDiscoveryClient
public class SentinelMain {
    public static void main(String[] args) {
        SpringApplication.run(SentinelMain.class,args);
    }
}


// 简单controller
@RestController
public class FlowLimitController {

    @GetMapping("/testA")
    public String testA(){
        return "-------testA";
    }

    @GetMapping("/testB")
    public String testB(){
        return "-------testB";
    }

}
```

打开sentinel的控制台，访问controller内的任意资源，即可出现对他们的监控。下面继续深入。

### 15.3 流控规则

**流量控制**（flow control），其原理是监控应用流量的 ==QPS（每秒请求数量）==或==并发线程数==等指标，当达到指定的阈值时对流量进行控制，以避免被瞬时的流量高峰冲垮，从而保障应用的高可用性。

==名词解释：==

- 资源名：resource，唯一名称，默认为请求路径
- 针对来源：Sentinel可以针对调用者进行限流，填写微服务名，默认是default（不区分来源）
- ==阈值类型/单机阈值==：
- - ==QPS(每秒请求数量)==：当调用该api的QPS达到阈值时，进行限流
  - ==线程数==：当调用该api的线程数达到阈值时，进行限流，==可以在某个访问的资源里面sleep一下测试效果==
- 是否集群：不需要集群
- ==流控模式==：
- - ==直接==：api达到限流条件时，直接限流
  - ==关联==：当关联的资源达到阈值时，限流自己
  - ==链路==：只记录指定链路上的流量（指定资源从入口资源进来的流量，如果达到阈值，就进行限流）、
- ==流控效果==：
- - ==快速失败==：直接失败，抛出异常
  - ==warm up==：根据coldFactor（冷加载因子，默认值为3）的值，从==阈值/coldFactor==，经过==预热时长==，才达到设置的QPS阈值
  - ==排队等待==：严格控制请求通过的间隔时间，也即是让请求以均匀的速度通过，对应的是漏桶算法

#### 15.3.1 流控模式

==流控模式--直接==：

<img src="SpringCloud%E6%95%99%E7%A8%8B.assets/1597819546992.png" alt="1597819546992" style="zoom:80%;" />

> 限流表现：当超过阀值，就会被降级。
>
> ![1597819613273](SpringCloud%E6%95%99%E7%A8%8B.assets/1597819613273.png)

==流控模式--关联：==

<img src="images/1597819976569.png" alt="1597819976569" style="zoom: 67%;" />

<img src="SpringCloud%E6%95%99%E7%A8%8B.assets/1597820015308-1616838530911.png" alt="1597820015308" style="zoom:80%;" />

#### 15.3.2 流控效果

==warm up==

即预热/冷启动方式。当系统长期处于低水位的情况下，当流量突然增加时，直接把系统拉升到高水位可能瞬间把系统压垮。==通过"冷启动"，让通过的流量缓慢增加==，在==一定时间内逐渐增加到阈值上限==，给冷系统一个预热的时间，避免冷系统被压垮。

<img src="SpringCloud%E6%95%99%E7%A8%8B.assets/image-20210328095554443.png" alt="image-20210328095554443" style="zoom:80%;" />

<img src="images\1597820534168.png" alt="1597820534168" style="zoom:80%;" />

==排队等待：==

会严格控制请求通过的间隔时间，也即是让请求以均匀的速度通过，对应的是漏桶算法。详细文档可以参考 [流量控制 - 匀速器模式](https://github.com/alibaba/Sentinel/wiki/流量控制-匀速排队模式)，具体的例子可以参见 [PaceFlowDemo](https://github.com/alibaba/Sentinel/blob/master/sentinel-demo/sentinel-demo-basic/src/main/java/com/alibaba/csp/sentinel/demo/flow/PaceFlowDemo.java)。

<img src="SpringCloud%E6%95%99%E7%A8%8B.assets/image-20210328100235816.png" alt="image-20210328100235816" style="zoom:80%;" />

这种方式主要用于处理间隔性突发的流量，例如消息队列。想象一下这样的场景，在某一秒有大量的请求到来，而接下来的几秒则处于空闲状态，我们希望系统能够在接下来的空闲期间逐渐处理这些请求，而不是在第一秒直接拒绝多余的请求。

> 注意：匀速排队模式暂时不支持 QPS > 1000 的场景。

### 15.4 熔断降级

<img src="SpringCloud%E6%95%99%E7%A8%8B.assets/image-20210328104455976.png" alt="image-20210328104455976" style="zoom: 67%;" />

#### 15.4.1 RT（平均响应时间，秒级）

- ==慢调用比例== (`SLOW_REQUEST_RATIO`)：选择以慢调用比例作为阈值，需要设置允许的==慢调用 RT（即最大的响应时间）==，==请求的响应时间大于该值则统计为慢调用==。当单位统计时长（`statIntervalMs`）内==<font color=red>请求数目大于设置的最小请求数目，并且慢调用的比例大于阈值</font>==，则接下来的熔断时长内请求会自动被熔断。经过==熔断时长后熔断器会进入探测恢复状态（HALF-OPEN 状态）==，若接下来的一个==请求响应时间小于设置的慢调用 RT 则结束熔断，若大于设置的慢调用 RT 则会再次被熔断==。
- - <font color=red>平均响应时间超出阈值</font>
  - <font color=red>时间窗口期内通过的请求数>=5</font>
- ==窗口期过后关闭断路器==
- RT最大4900（更大的需要通过-Dcsp.sentinel.statistic.max.rt=xxx才能生效）

#### 15.4.2 异常比例（秒级）

- 针对的是==调用异常（比如除0的溢出）==
- - ==QPS>=5,==
  - ==异常比例（秒级统计）超过阈值时，触发降级==
- 时间窗口结束后，关闭降级

#### 15.4.3 异常数

- 针对的是==调用异常==
- - ==近1分钟的异常数目超过阈值==<font color=red>进入熔断</font>
  - 注意统计时间窗口是==分钟级别的==，如果<font color=red>统计时间窗口小于60s</font>，结束熔断后仍可能再次进入熔断

<img src="SpringCloud%E6%95%99%E7%A8%8B.assets/1597821618735-1616925298196.png" alt="1597821618735" style="zoom:80%;" />

### 15.5 热点Key限流

热点：热点即经常访问的数据。很多时候我们希望统计某个热点数据中访问频次最高的topK数据，并对其访问进行限制。比如：

- 商品ID为参数，统计一段时间内最常购买的商品ID进行限制
- 用户ID为参数，针对一段时间内频繁访问的用户ID进行限制

热点参数限流会统计传入参数中的热点参数，并根据配置的限流阈值与模式，对包含热点参数的资源调用进行限流。热点参数限流可以看作是一种特殊的流量控制，仅对包含热点参数的资源调用生效。

controller层写一个demo:

```java
	@GetMapping("/testhotkey")
    @SentinelResource(value = "testhotkey", blockHandler = "deal_testhotkey")
    //这个value是随意的值，并不和请求路径必须一致
    //在填写热点限流的 资源名 这一项时，可以填 /testhotkey 或者是 @SentinelResource的value的值
    public String testHotKey(
            @RequestParam(value="p1", required = false) String p1,
            @RequestParam(value = "p2", required = false) String p2
    ){
        return "testHotKey__success";
    }

	//类似Hystrix 的兜底方法
    public String deal_testhotkey(String p1, String p2, BlockException e){
        return "testhotkey__fail"; 
    }
```

<img src="SpringCloud%E6%95%99%E7%A8%8B.assets/1597822501876.png" alt="1597822501876" style="zoom:80%;" />

可以配置==热点参数的例外项==

<img src="images/1597822772165.png" alt="1597822772165" style="zoom:80%;" />

说明：

<img src="SpringCloud%E6%95%99%E7%A8%8B.assets/1597822972448.png" alt="1597822972448" style="zoom:80%;" />

### 15.6 系统自适应限流

> 一般配置在网关或者入口应用中，但是这个东西有点危险，一旦值不合适，就相当于系统瘫痪。

详细去查阅官网文档

### 15.7 @SentinelResource

> @SentinelResource 注解，主要是指定资源名（也可以==用请求路径作为资源名==），和==指定降级处理方法的==。

```java
package com.atguigu.springcloud.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.atguigu.springcloud.entities.CommonResult;
import com.atguigu.springcloud.entities.Payment;
import com.atguigu.springcloud.myhandler.CustomerBlockHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/3/29
 */
@RestController
public class RateLimitController {

    
    
    
    // 示例一：
    // blockHandler = "handleException"
    // 降级的保底方法名是handleException
    // 降级方法和业务代码耦合在一起
    @GetMapping("/byResource")
    @SentinelResource(value="byResource",blockHandler = "handleException")
    public CommonResult byResource(){

        return  new CommonResult(200,"按照资源名称限流测试OK",new Payment(2021L,"serial001"));

    }


    //降级方法
    public CommonResult handleException(BlockException e){
        return new CommonResult(444, e.getClass().getCanonicalName() + "\t 服务不可用");
    }
    
    
    
    // 示例二：不指定blockHandler的话则使用默认的降级方法
    @GetMapping("/rateLimit/byUrl")
    @SentinelResource(value = "byUrl")
    public CommonResult byUrl(){

        return new CommonResult(200,"按照url限流测试OK",new Payment(2021L,"serial002"));
    }
    
    
    // 示例三：自定义降级类及其方法
    @GetMapping("rateLimit/customerBlockHandler")
    @SentinelResource(value = "customerBlockHandler",
            blockHandlerClass = CustomerBlockHandler.class,blockHandler = "handlerException2")
    public CommonResult customerBlockHandler(){

        return new CommonResult(200,"按客户自定义", new Payment(2021L,"serial003"));
    }


}

```

### 15.7 fallback

> fallback：管理运行异常
>
> blockHandler：管理配置违规

以上使用sentinel有一个很明显的问题，就是sentinel，对程序内部异常（各种异常，包括超时）这种捕捉，显得很乏力。以上主要针对流量控制，系统吞吐量活着异常比例，会发生降级或熔断；但是当程序内部发生异常，直接返回给用户错误页面，根本不会触发异常比例这种降级。

> 以下面的例子为例：
>
> 一、首先是==两个提供者模块，如下==：

==pom依赖==：

```xml
<dependencies>
        <!-- springcloud alibaba nacos 依赖 -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        </dependency>
        
        <!-- springboot整合Web组件 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency><!-- 引入自己定义的api通用包，可以使用Payment支付Entity -->
            <groupId>com.atguigu.springcloud</groupId>
            <artifactId>cloud-api-commons</artifactId>
            <version>${project.version}</version>
        </dependency>
    </dependencies>
```

==yml配置==：

```yml
server:
  port: 9005  # / 9004
spring:
  application:
    name: nacos-payment-provider
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
management:
  endpoints:
    web:
      exposure:
        include: '*'
```

==主启动类只是注意别忘了@EnableDiscoveryClient注解==

==业务类==

```java
@RestController
public class PaymentController {

    @Value("${server.port}")
    private String serverPort;

    //模拟sql查询
    public static HashMap<Long, Payment> hashMap = new HashMap<>();
    static {
        hashMap.put(1L, new Payment(1L, "xcxcxcxcxcxcxcxcxcxcxcxcxc11111111"));
        hashMap.put(2L, new Payment(2L, "xcxcxcxcggggggggg2222222222222222"));
        hashMap.put(3L, new Payment(3L, "xcxcxcxccxxcxcfafdgdgdsgdsgds33333"));
    }


    @GetMapping("/payment/get/{id}")
    public CommonResult paymentSql(@PathVariable("id")Long id){
        Payment payment = hashMap.get(id);
        CommonResult result = new CommonResult(200, "from mysql, server port : " + serverPort + " ,查询成功", payment);
        return result;
    }
}
```

> 二、下面是==消费者模块，如下==：
>
> 主要在消费者上做fallback和blockHandler的演示

==pom依赖==

```xml
<dependencies>
        <!-- 后续做Sentinel的持久化会用到的依赖 -->
        <dependency>
            <groupId>com.alibaba.csp</groupId>
            <artifactId>sentinel-datasource-nacos</artifactId>
        </dependency>
        <!-- sentinel  -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
        </dependency>
        <!-- springcloud alibaba nacos 依赖 -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        </dependency>

        <!-- springboot整合Web组件 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>

        <!-- 日常通用jar包 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency><!-- 引入自己定义的api通用包，可以使用Payment支付Entity -->
            <groupId>com.atguigu.springcloud</groupId>
            <artifactId>cloud-api-commons</artifactId>
            <version>${project.version}</version>
        </dependency>
    </dependencies>
```

==yml配置==

```yml
server:
  port: 84
spring:
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
    sentinel:
      transport:
        dashboard: localhost:8080
        port: 8719
  application:
    name: nacos-order-consumer
```

==主启动类不必多说==

==config类里面注入 RestTemplate：==

```java
@Configuration
public class ApplicationContextConfig {
    @Bean
    @LoadBalanced
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }
}
```

==controller 层：==

```java
@RestController
public class OrderController {

    private static final String PAYMENT_URL="http://nacos-payment-provider";

    @Resource
    private RestTemplate restTemplate;

    @GetMapping("/consutomer/payment/get/{id}")
    public CommonResult getPayment(@PathVariable("id")Long id){
        if(id >= 4){
            throw new IllegalArgumentException("非法参数异常...");
        }else {
            return restTemplate.getForObject(PAYMENT_URL + "/payment/get/" + id, CommonResult.class);
        }
    }
}
```

上面只实现了 以nacos 作为服务注册中心，消费者使用ribbon 实现负载均衡调用提供者的效果。

==下面介绍fallback==

#### 15.7.1 只配置fallback

> ==无fallback的兜底方法==

```java
@GetMapping("/consutomer/payment/get/{id}")
   //fallback只处理业务异常,但这里没有指出兜底方法
    @SentinelResource(value = "fallback") 
    public CommonResult getPayment(@PathVariable("id")Long id){
        if(id >= 4){
            throw new IllegalArgumentException("非法参数异常...");
        }else {
            return restTemplate.getForObject(PAYMENT_URL + "/payment/get/" + id, CommonResult.class);
        }
    }
```

<font color=red>会出现error页面，抛出上面定义的异常。</font>

> ==有fallback的兜底方法==

```java
@GetMapping("/consutomer/payment/get/{id}")
   //fallback只处理业务异常
    @SentinelResource(value = "fallback", fallback = "handleFallback") 
    public CommonResult getPayment(@PathVariable("id")Long id){
        if(id >= 4){
            throw new IllegalArgumentException("非法参数异常...");
        }else {
            return restTemplate.getForObject(PAYMENT_URL + "/payment/get/" + id, CommonResult.class);
        }
    }

    //兜底方法
    public CommonResult handleFallback(@PathVariable("id")Long id, Throwable e){
        return new CommonResult(414, "---非法参数异常--", e);
    }
```

<font color=red>不会出现error页面，而是自定义的CommonResult信息</font>

#### 15.7.2 只配置blockHandler

```java
@GetMapping("/consutomer/payment/get/{id}")
    @SentinelResource(value = "fallback", blockHandler = "handleblockHandler")
    public CommonResult getPayment(@PathVariable("id")Long id){
        if(id >= 4){
            throw new IllegalArgumentException("非法参数异常...");
        }else {
            return restTemplate.getForObject(PAYMENT_URL + "/payment/get/" + id, CommonResult.class);
        }
    }


    //====blockHandler                                       blockHandler的方法必须有这个参数
    public CommonResult handleblockHandler(@PathVariable("id")Long id, BlockException e){
        return new CommonResult(414, "---非法参数异常--", e);
    }
```

> 这时候的效果就是，运行异常直接报==错误页面==。在sentinel上==添加一个降级规则==，设置==2s内触发异常2次==，触发阈值以后，返回的是我们自定义的 ==blockhanlder 方法返回的内容==。

#### 15.7.3 两者都配置

```java
    @GetMapping("/consutomer/payment/get/{id}")
    @SentinelResource(value = "fallback", blockHandler = "handleblockHandler", fallback = "handleFallback")
    public CommonResult getPayment(@PathVariable("id")Long id){
        if(id >= 4){
            throw new IllegalArgumentException("非法参数异常...");
        }else {
            return restTemplate.getForObject(PAYMENT_URL + "/payment/get/" + id, CommonResult.class);
        }
    }


    //====fallback
    public CommonResult handleFallback(@PathVariable("id")Long id, Throwable e){
        return new CommonResult(414, "---非法参数异常--form fallback的提示", e);
    }

    //====blockHandler                                       blockHandler的方法必须有这个参数
    public CommonResult handleblockHandler(@PathVariable("id")Long id, BlockException e){
        return new CommonResult(414, "---非法参数异常--", e);
    }
```

> 明显两者都是有效的，可以同时配置，但是一旦达到==限流规则==，则会==进入blockHandler 进行处理==

#### 15.7.4 异常忽略

这是 @SentinelResource 注解的一个值：

![1597909285814](SpringCloud%E6%95%99%E7%A8%8B.assets/1597909285814.png)

### 15.8 全局降级

> 上面是单个进行 fallback 和 blockhandler 的测试，下面是整合 openfeign 实现把降级方法解耦。和Hystrix 几乎一摸一样！

还是使用上面 84 这个消费者做测试：

1. 先添加open-feign依赖：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

2. yml 追加如下配置：

```yaml
# 激活Sentinel对Feign的支持
feign:
  sentinel:
    enabled: true
```

3. 主启动类添加注解 ： @EnableFeignClients  激活open-feign
4. service : 

```java
@FeignClient(value = "nacos-payment-provider", fallback = PaymentServiceImpl.class)
public interface PaymentService {

    @GetMapping("/payment/get/{id}")
    public CommonResult paymentSql(@PathVariable("id")Long id);
}
```

5. service 实现类：

```java
@Component
public class PaymentServiceImpl implements PaymentService {

    @Override
    public CommonResult paymentSql(Long id) {
        return new CommonResult(414, "open-feign 整合 sentinel 实现的全局服务降级策略",null);
    }
}
```

6. controller 层代码没什么特殊的，和普通调用service 一样即可。
7. 测试，关闭提供者的项目，会触发 service 实现类的方法。
8. 总结: 这种全局熔断，是针对 “访问提供者” 这个过程的，只有访问提供者过程中发生异常才会触发降级，也就是这些降级，是给service接口上这些提供者的方法加的，以保证在远程调用时能顺利进行。而且这明显是 fallback ，而不是 blockHandler，注意区分。

> fallback 和 blockHandler 肤浅的区别：
>
> F ： 不需要指定规则，程序内部异常均可触发（超时异常需要配置超时时间）
>
> B :  配上也没用，必须去 Sentinel 上指定规则才会被触发。 

### 15.9 持久化

> 目前的sentinel 当重启以后，数据都会丢失，和 nacos 类似原理。需要持久化。它可以被持久化到 nacos 的数据库中。

1. pom依赖：

```xml
<dependency>
    <groupId>com.alibaba.csp</groupId>
    <artifactId>sentinel-datasource-nacos</artifactId>
</dependency>
```

2. yml配置：

```yaml
spring:
  cloud:
    sentinel:
      datasource:
        ds1:  
          nacos:
            server-addr: localhost:8848
            dataId: ${spring.application.name}
            group: DEFAULT_GROUP
            data-type: json
            rule-type: flow
```

3. 去nacos上创建一个dataid ,名字和yml配置的一致，json格式，内容如下：

```json
[
    {
        "resource": "/testA",
        "limitApp": "default",
        "grade": 1,
        "count": 1,
        "strategy": 0,
        "controlBehavior": 0,
        "clusterMode": false
    }
]
```

<img src="images/1597913169207.png" alt="1597913169207" style="zoom:80%;" />

4. 启动应用，发现存在 关于 /testA 请求路径的流控规则。
5. 总结: 就是在 sentinel 启动的时候，去 nacos 上读取相关规则配置信息，实际上它规则的持久化，就是第三步，粘贴到nacos上保存下来，就算以后在 sentinel 上面修改了，重启应用以后也是无效的。

## 十六、Spring Cloud Alibaba之分布式事务

> Seate 处理分布式事务。
>
> 微服务模块，连接多个数据库，多个数据源，而数据库之间的数据一致性需要被保证。
>
> 官网：  http://seata.io/zh-cn/ 

### 16.1 简介

<img src="SpringCloud%E6%95%99%E7%A8%8B.assets/image-20210330222749100.png" alt="image-20210330222749100" style="zoom:80%;" />

Seata术语： 一 + 三

<img src="SpringCloud%E6%95%99%E7%A8%8B.assets/1597982738615.png" alt="1597982738615" style="zoom:80%;" />

<img src="SpringCloud%E6%95%99%E7%A8%8B.assets/1597982788659.png" alt="1597982788659" style="zoom:80%;" />

![1597982858545](SpringCloud%E6%95%99%E7%A8%8B.assets/1597982858545.png)

> 下载地址 ： https://github.com/seata/seata/releases/download/v1.0.0/seata-server-1.0.0.zip

<img src="SpringCloud%E6%95%99%E7%A8%8B.assets/1597984908755.png" alt="1597984908755" style="zoom:80%;" />

### 16.2 初始化配置

> 一、修改conf/file.conf文件
>
> 主要修改自定义==事务组名称== +==事务日志存储模式为db== + ==数据库连接信息==

<img src="SpringCloud%E6%95%99%E7%A8%8B.assets/image-20210331205027925.png" alt="image-20210331205027925" style="zoom:80%;" />

> 二、创建==名和file.conf指定一致的数据库==

> 三、在新建的数据库里面创建数据表，==db_store.sql==文件在 conf 目录下（1.0.0有坑，没有sql文件，下载0.9.0的，使用它的sql文件即可）

> 四、修改==conf/registry.conf文件==的内容

<img src="SpringCloud%E6%95%99%E7%A8%8B.assets/image-20210331205741264.png" alt="image-20210331205741264" style="zoom:80%;" />

> 五、先启动nacos Server服务，再启动seata Server

### 16.3 案例介绍

> 创建三个微服务，一个==订单服务==，一个==库存服务==，一个==账户服务==
>
> 订单服务中创建一个订单，==远程调用库存服务==来扣减下单商品的库存
>
> 再通过==远程调用账户服务==来扣减用户账户里面的余额
>
> 最后在订单服务中==修改订单状态为已完成==
>
> 该操作跨越三个数据库，有两次远程调用，很明显会有分布式事务问题

#### 16.3.1 数据库准备

创建三个数据库： <img src="/images/1597987722338.png" alt="1597987722338" style="zoom:80%;" />

每个数据库创建数据表：

order 库： 

![1597988061545](G:/%25E7%2599%25BE%25E5%25BA%25A6%25E4%25BA%2591%25E5%25AD%2598%25E5%2582%25A8/springCloud/images/1597988061545.png)

account 库： 

![1597988768251](SpringCloud%E6%95%99%E7%A8%8B.assets/1597988768251.png)

storage 库：

![1597988262441](SpringCloud%E6%95%99%E7%A8%8B.assets/1597988262441.png)

三个数据库都创建一个回滚日志表，seata/conf/ 有相应的sql文件（1.0.0没有，依然使用0.9.0中的）。

最终效果：

![1597989003349](SpringCloud%E6%95%99%E7%A8%8B.assets/1597989003349.png)

#### 16.3.2 开发过程

> 实现==下订单-> 减库存 -> 扣余额 -> 改（订单）状态==
>
> 需要注意的是，下面做了 seata 与 mybatis 的整合，所以注意一下，和以往的mybatis的使用不太一样。

> 一、新建模块 cloudalibaba-seata-order2001 

==1. pom依赖：==

```xml
 <dependencies>
        <!-- seata -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-seata</artifactId>
            <exclusions>
                <exclusion>
                    <artifactId>seata-all</artifactId>
                    <groupId>io.seata</groupId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>io.seata</groupId>
            <artifactId>seata-all</artifactId>
            <version>0.9.0</version>
        </dependency>
        <!-- springcloud alibaba nacos 依赖,Nacos Server 服务注册中心 -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        </dependency>

        <!-- open feign 服务调用 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>

        <!-- springboot整合Web组件 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>

        <!-- 持久层支持 -->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid-spring-boot-starter</artifactId>
            <version>1.1.10</version>
        </dependency>
        <!--mysql-connector-java-->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>
        <!--jdbc-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jdbc</artifactId>
        </dependency>
        <!-- mybatis -->
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
        </dependency>

        <!-- 日常通用jar包 -->
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>com.atguigu.springcloud</groupId>
            <artifactId>cloud-api-commons</artifactId>
            <version>${project.version}</version>
        </dependency>
    </dependencies>
```

==2. yml配置==

```yml
server:
  port: 2001
spring:
  application:
    name: seata-order-service
  cloud:
    alibaba:
      seata:
        # 自定义事务组，需要和当时在 seata/conf/file.conf 中的一致
        tx-service-group: dexlace-group
    nacos:
      discovery:
        server-addr: localhost:8848
  datasource:
    driver-class-name: com.mysql.jdbc.Driver
    url: jdbc:mysql://localhost:3306/seata_order
    username: root
    password: 123456


# 注意，这是自定义的，原来的是mapper_locations
mybatis:
  mapperLocations: classpath:mapper/*.xml

logging:
  level:
    io:
      seata: info
```

3. 将 seata/conf/ 下的 ==file.conf 和 registry.cong 两个文件拷贝到 resource 目录下。==

4. ==创建 domain 实体类==： Order 和 CommonResult<T> 两个实体类。

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonResult<T> {

    private Integer code;
    private String message;
    private T data;

    public CommonResult(Integer code, String message){
        this(code,message,null);
    }
}
```

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    private Long id;
    private Long userId;
    private Long productId;
    private Integer count;
    private BigDecimal money;
    private Integer status;  //订单状态：0创建中，1创建完成
}

```

5. ==dao==

```java
/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/3/31
 */
@Mapper
public interface OrderDao {

    //创建订单
    public void create(Order order);

    //修改订单状态
    public void update(@Param("userId") Long userId, @Param("status") Integer status);

}
```

6. ==mapper文件==

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >

<mapper namespace="com.atguigu.springcloud.alibaba.dao.OrderDao">

    <resultMap id="BaseResultMap" type="com.atguigu.springcloud.alibaba.domain.Order">
        <id column="id" property="id" jdbcType="BIGINT"></id>
        <result column="user_id" property="userId" jdbcType="BIGINT"></result>
        <result column="product_id" property="productId" jdbcType="BIGINT"></result>
        <result column="count" property="count" jdbcType="INTEGER"></result>
        <result column="money" property="money" jdbcType="DECIMAL"></result>
        <result column="status" property="status" jdbcType="INTEGER"></result>
    </resultMap>

    <insert id="create">
        insert into t_order(id, user_id, product_id, count, money, status)
        values (null, #{userId},#{productId},#{count},#{money},0)
    </insert>

    <update id="update">
        update t_order set status = 1 where user_id=#{userId} and status=#{status}
    </update>

</mapper>


```

7. ==service层==

> <img src="SpringCloud%E6%95%99%E7%A8%8B.assets/image-20210331214523027.png" alt="image-20210331214523027" style="zoom:67%;" />

serviceImpl : 

```java
@Service
@Slf4j
public class OrderServiceImpl  implements OrderService {

    @Resource
    private OrderDao orderDao;

    @Resource
    private StorageService storageService;

    @Resource
    private AccountService accountService;

    @Override
    public void create(Order order) {
        log.info("--------》 开始创建订单");
        orderDao.create(order);
        log.info("--------》 订单微服务开始调用库存，做扣减---Count-");
        storageService.decrease(order.getProductId(), order.getCount());
        log.info("--------》 订单微服务开始调用库存，库存扣减完成！！");
        log.info("--------》 订单微服务开始调用账户，账户扣减---money-");
        accountService.decrease(order.getUserId(),order.getMoney());
        log.info("--------》 订单微服务开始调用账户，账户扣减完成!!");
        //修改订单状态，从0到1
        log.info("--------》 订单微服务修改订单状态，start");
        orderDao.update(order.getUserId(),0);
        log.info("--------》 订单微服务修改订单状态，end");

        log.info("--订单结束--");
    }

    @Override
    public void update(Long userId, Integer status) {

    }
}
```

```java
@FeignClient(value = "seata-account-service")
public interface AccountService {

    @PostMapping("/account/decrease")
    CommonResult decrease(@RequestParam("userId") Long userId, @RequestParam("money") BigDecimal money);
}

```

```java
@FeignClient(value="seata-storage-service")
public interface StorageService {

    @PostMapping("/storage/decrease")
    CommonResult decrease(@RequestParam("productId") Long productId, @RequestParam("count") Integer count);
}

```

8. Controller层

```java
@RestController
public class OrderController {

    @Resource
    private OrderService orderService;

    @GetMapping("/order/create")
    public CommonResult create(Order order){
        orderService.create(order);
        return new CommonResult(200, "订单创建成！");
    }
}
```

9. config（==seata整合了一下mybatis==）

   ```java
   package com.atguigu.springcloud.alibaba.config;
   
   import com.alibaba.druid.pool.DruidDataSource;
   import io.seata.rm.datasource.DataSourceProxy;
   import org.apache.ibatis.session.SqlSessionFactory;
   import org.mybatis.spring.SqlSessionFactoryBean;
   import org.mybatis.spring.transaction.SpringManagedTransactionFactory;
   import org.springframework.beans.factory.annotation.Value;
   import org.springframework.boot.context.properties.ConfigurationProperties;
   import org.springframework.context.annotation.Bean;
   import org.springframework.context.annotation.Configuration;
   import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
   
   import javax.sql.DataSource;
   
   @Configuration
   public class DataSourceProxyConfig {
   
       @Value("${mybatis.mapperLocations}")
       private String mapperLocations;
   
       @Bean
       @ConfigurationProperties(prefix = "spring.datasource")
       public DataSource druidDataSource(){
           return new DruidDataSource();
       }
   
       @Bean
       public DataSourceProxy dataSourceProxy(DataSource dataSource){
           return new DataSourceProxy(dataSource);
       }
   
       @Bean
       public SqlSessionFactory sqlSessionFactoryBean(DataSourceProxy dataSourceProxy) throws Exception {
           SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
           sqlSessionFactoryBean.setDataSource(dataSourceProxy);
           sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(mapperLocations));
           sqlSessionFactoryBean.setTransactionFactory(new SpringManagedTransactionFactory());
           return sqlSessionFactoryBean.getObject();
       }
   }
   
   ```

   ```java
   package com.atguigu.springcloud.alibaba.config;
   
   import org.mybatis.spring.annotation.MapperScan;
   import org.springframework.context.annotation.Configuration;
   
   @Configuration
   @MapperScan({"com.atguigu.springcloud.alibaba.dao"})
   public class MybatisConfig {
   }
   
   ```

10. 主启动类：

```java
package com.atguigu.springcloud.alibaba;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/3/31
 */
@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication( exclude = DataSourceAutoConfiguration.class)
public class SeataOrderMainApp2001 {
    public static void main(String[] args) {
        SpringApplication.run(SeataOrderMainApp2001.class,args);
    }
}

```

先启动 nacos  --> 再启动 seata --> 再启动此order服务，测试，可以启动。

> 仿照上面 创建  ==cloudalibaba-seata-storage2002== 和  ==cloudalibaba-seata-account2003== 两个模块，唯一大的区别就是这两个==不需要导入 open-feign 远程调用其它模块==。

#### 16.3.3 seata使用

```java
 @Override
//只需要在业务类的方法上加上该注解，name值自定义唯一即可。
    @GlobalTransactional(name="create-order",rollbackFor = Exception.class)
    public void create(Order order) {
        log.info("--------》 开始创建订单");
        orderDao.create(order);
        log.info("--------》 订单微服务开始调用库存，做扣减---Count-");
        storageService.decrease(order.getProductId(), order.getCount());
        log.info("--------》 订单微服务开始调用库存，库存扣减完成！！");

        log.info("--------》 订单微服务开始调用账户，账户扣减---money-");
        accountService.decrease(order.getUserId(),order.getMoney());
        log.info("--------》 订单微服务开始调用账户，账户扣减完成!!");

        //修改订单状态
        log.info("--------》 订单微服务修改订单状态，start");
        orderDao.update(order.getUserId(),0);
        log.info("--------》 订单微服务修改订单状态，end");

        log.info("--订单结束--");
    }
```

如此可以保证整个==调用链路过程中的分布式事务的一致性==

### 16.4 seata原理介绍

