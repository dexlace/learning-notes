# Nginx笔记

## 一、Nginx简介

### 1.1 什么是Nginx

Nginx 可以作为==静态页面的 web 服务器==，同时还支持 CGI 协议的动态语言，比如 perl、php等。==但是不支持 java,Java 程序只能通过与 tomcat 配合完成==。==Nginx 专为性能优化而开发==，性能是其最重要的考量,实现上非常注重效率 ，能经受高负载的考验,有报告表明能支持高达 **50,000** 个并发连接数。

### 1.2 能做什么

#### 1.2.1 反向代理

#### 1.2.2 负载均衡

### 1.3 安装

下载官网：http://nginx.org/ ，这里选择1.18

下载pcre：wget http://downloads.sourceforge.net/project/pcre/pcre/8.37/pcre-8.37.tar.gz

安装

```bash
# 安装gcc环境
yum install gcc-c++

# zlib压缩和解压缩依赖，
yum install -y zlib zlib-devel
# SSL 安全的加密的套接字协议层，用于HTTP安全传输，也就是https
yum install -y openssl openssl-devel

# 安装pcre
先解压
进入解压文件
./configure
make && make install
# 检验pcre版本
pcre-config  --version

# 安装nginx
先解压
./configure
make && make install
# 然后会在/usr/local中出现一个nginx文件夹
# 启动，到/usr/local/nginx
./sbin/nginx
# 浏览器直接通过ip地址访问，测试能否成功！(这里如果访问不成，进不去，要考虑是否将防火墙关闭！)
关闭命令
在/usr/local/nginx/sbin 目录下执行 ./nginx -s stop

重新加载命令
在/usr/local/nginx/sbin 目录下执行 ./nginx -s reload
```

<img src="Nginx%E7%AC%94%E8%AE%B0.assets/image-20210628213431632.png" alt="image-20210628213431632" style="zoom:80%;" />

### 1.4 nginx配置文件

```bash
...              #全局块

events {         #events块
   ...
}

http      #http块
{
    ...   #http全局块
    server        #server块
    { 
        ...       #server全局块
        location [PATTERN]   #location块
        {
            ...
        }
        location [PATTERN] 
        {
            ...
        }
    }
    server
    {
      ...
    }
    ...     #http全局块
}
```

- 第一部分：全局块

```bash
#user  nobody;  #运行Nginx服务器的用户（组）
worker_processes  1; # 允许生成的 worker process 数
#error_log  logs/error.log;   #
#error_log  logs/error.log  notice;
#error_log  logs/error.log  info;
#pid        logs/nginx.pid;
```

- 第二部分：events块

```bash
events {
    worker_connections  1024;
}
#events 块涉及的指令主要影响 Nginx 服务器与用户的网络连接，常用的设置包括是否开启对多 work process 下的网络连接进行序列化，是否允许同时接收多个网络连接，选取哪种事件驱动模型来处理连接请求，每个 word process 可以同时支持的最大连接数等。
# 这部分的配置对 Nginx 的性能影响较大，在实际中应该灵活配置。
```

- 第三部分：http块

```bash
http {
    include       mime.types;
    default_type  application/octet-stream;

    #log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
    #                  '$status $body_bytes_sent "$http_referer" '
    #                  '"$http_user_agent" "$http_x_forwarded_for"';

    #access_log  logs/access.log  main;

    sendfile        on;
    #tcp_nopush     on;

    #keepalive_timeout  0;
    keepalive_timeout  65;

    #gzip  on;

    server {
        listen       80;
        server_name  localhost;

        #charset koi8-r;

        #access_log  logs/host.access.log  main;

        location / {
            root   html;
            index  index.html index.htm;
        }

        #error_page  404              /404.html;

        # redirect server error pages to the static page /50x.html
        #
        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }
     
    }
    # 可以继续配置多个server实例
}
```

这算是 Nginx 服务器配置中最频繁的部分，代理、缓存和日志定义等绝大多数功能和第三方模块的配置都在这里。

需要注意的是：http 块也可以包括 **http 全局块**、**server 块**。

- - http 全局块

http 全局块配置的指令包括==文件引入、MIME-TYPE 定义、日志自定义、连接超时时间、单链接请求数上限==等。

- - server 块

这块和虚拟主机有密切关系，虚拟主机从用户角度看，和一台独立的硬件主机是完全一样的，该技术的产生是为了节省互联网服务器硬件成本。

==每个 http 块可以包括多个 server 块==，而每个 server 块就相当于一个虚拟主机。

而每个 server 块也分为全局 server 块，以及可以同时包含多个 locaton 块。

```bash
########### 每个指令必须有分号结束。#################
#user administrator administrators;  #配置用户或者组，默认为nobody nobody。
#worker_processes 2;  #允许生成的进程数，默认为1
#pid /nginx/pid/nginx.pid;   #指定nginx进程运行文件存放地址
error_log log/error.log debug;  #制定日志路径，级别。这个设置可以放入全局块，http块，server块，级别以此为：debug|info|notice|warn|error|crit|alert|emerg
events {
    accept_mutex on;   #设置网路连接序列化，防止惊群现象发生，默认为on
    multi_accept on;  #设置一个进程是否同时接受多个网络连接，默认为off
    #use epoll;      #事件驱动模型，select|poll|kqueue|epoll|resig|/dev/poll|eventport
    worker_connections  1024;    #最大连接数，默认为512
}
http {
    include       mime.types;   #文件扩展名与文件类型映射表
    default_type  application/octet-stream; #默认文件类型，默认为text/plain
    #access_log off; #取消服务日志    
    log_format myFormat '$remote_addr–$remote_user [$time_local] $request $status $body_bytes_sent $http_referer $http_user_agent $http_x_forwarded_for'; #自定义格式
    access_log log/access.log myFormat;  #combined为日志格式的默认值
    sendfile on;   #允许sendfile方式传输文件，默认为off，可以在http块，server块，location块。
    sendfile_max_chunk 100k;  #每个进程每次调用传输数量不能大于设定的值，默认为0，即不设上限。
    keepalive_timeout 65;  #连接超时时间，默认为75s，可以在http，server，location块。

    upstream mysvr {   
      server 127.0.0.1:7878;
      server 192.168.10.121:3333 backup;  #热备
    }
    error_page 404 https://www.baidu.com; #错误页
    server {
        keepalive_requests 120; #单连接请求上限次数。
        listen       4545;   #监听端口
        server_name  127.0.0.1;   #监听地址       
        location  ~*^.+$ {       #请求的url过滤，正则匹配，~为区分大小写，~*为不区分大小写。
           #root path;  #根目录
           #index vv.txt;  #设置默认页
           proxy_pass  http://mysvr;  #请求转向mysvr 定义的服务器列表
           deny 127.0.0.1;  #拒绝的ip
           allow 172.18.5.54; #允许的ip           
        } 
    }
}
```

## 二、反向代理

```bash
server {
        listen       80;
        server_name  192.168.205.123;    #nginx访问地址
        #charset koi8-r;
        #access_log  logs/host.access.log  main;
        location / {
            root   html;
            proxy_pass http://192.168.205.123:8080;    #转发到目标地址
            index  index.html index.htm;
        }
        #error_page  404              /404.html;
        # redirect server error pages to the static page /50x.html
        #
        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }
    }
```

访问nginx的访问地址`192.168.205.123:80`,则可访问`192.168.205.123:8080`

则直接192.168.205.123即可访问tomcat（默认8080端口）

```bash
#user  nobody;
worker_processes  1;
#error_log  logs/error.log;
#error_log  logs/error.log  notice;
#error_log  logs/error.log  info;
#pid        logs/nginx.pid;
events {
    worker_connections  1024;
}
http {
    include       mime.types;
    default_type  application/octet-stream;
    #log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
    #                  '$status $body_bytes_sent "$http_referer" '
    #                  '"$http_user_agent" "$http_x_forwarded_for"';
    #access_log  logs/access.log  main;
    sendfile        on;
    #tcp_nopush     on;
    #keepalive_timeout  0;
    keepalive_timeout  65;
    #gzip  on;
   
    # another virtual host using mix of IP-, name-, and port-based configuration
    # 在这里把注释去掉，添加一个server 端口监听9001
    server {
        listen       9001;   # 这里监听的是9001端口
        server_name  192.168.88.123;
    # 在下面 配置转发到不同的tomcat 中的内容 格式：~ /项目名/
    # 这里的edu和vod是tomcat中webapp的项目名，这里配置了两个tomcat，
    # 并根据不同的启动路径分别路由到不同的tomcat
        location ~ /edu/ {
            proxy_pass http://192.168.205.123:8001;
        }
        location ~ /vod/ {
            proxy_pass http://192.168.205.123:8002;
        }
    }

}
```

location指令说明

该指令用于匹配 URL。
语法如下：

1. === ：==用于不含正则表达式的 uri 前，要求请求字符串与 uri ==严格匹配==，如果匹配
   成功，就停止继续向下搜索并立即处理该请求。
2. ==~：==用于==表示 uri 包含正则表达式==，并且==区分大小写==。
3. ==~*：==用于表示 uri 包含正则表达式，并且==不区分大小写==。
4. ==^~：==用于不含正则表达式的 uri 前，要求 Nginx 服务器找到标识 uri 和请求字符串匹配度最高的 location 后，立即使用此 location 处理请求，而不再使用 location 块中的正则 uri 和请求字符串做匹配。

==注意：如果 uri 包含正则表达式，则必须要有 ~ 或者 ~* 标识。==

## 三、负载均衡

```bash
#user  nobody;
worker_processes  1;

events {
    worker_connections  1024;
}
http {
    include       mime.types;
    default_type  application/octet-stream;

    sendfile        on;


    # 加上upstream 
    # upstream 服务器名
    # 可以代理以下两个不同的服务器地址
    upstream myserver {
        server  192.168.88.132:8080; #参与负载均衡的服务器地址1 
        server  192.168.88.132:8081; #参与负载均衡的服务器地址2
    }
    server {
        listen       80;
        server_name  192.168.88.132;
        #charset koi8-r;
        #access_log  logs/host.access.log  main;
        location / {
            # 加上代理规则
            # 在上述的两个服务器地址中都可布置相同的项目，访问相同的资源时会有负载均衡的效果
            proxy_pass http://myserver;
            root   html;
            index  index.html index.htm;
        }
     
        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }
     
    }
}
```

nginx分配服务器策略

**第一种 轮询策略（默认）**：

每个请求按时间顺序逐一分配到不同的后端服务器，如果后端服务器 down 掉，能自动剔除。

**第二种 权重策略 weight**：

weight 代表权重默认为 1,权重越高被分配的客户端越多。

```bash
#例如：   

upstream myserver {        
server  192.168.88.132:8080 weight=5; #参与负载均衡的服务器地址1        
server  192.168.88.132:8081 weight=10; #参与负载均衡的服务器地址2   

}
# 如上，8080的权重为5 8081权重为10，则nginx 负载均衡分配时，分配的8081比例更大
```

**第三种 ip哈希策略 ip_hash**：

每个请求按访问 ip 的hash 结果分配，这样每个访客**固定访问**一个后端服务器，可以解决session问题。

```bash
#例如：    
upstream myserver {        
	ip_hash ;       
	server  192.168.88.132:8080;#参与负载均衡的服务器地址1        
	server  192.168.88.132:8081; #参与负载均衡的服务器地址2   
    
 }
 # 如上，每个请求按访问 ip 的hash 结果分配，即：某一个用户访问该服务器，根据该用户的ip地址分配一个ip_hash，并随机分配到端口为8080或者8081的服务器，该用户这个ip下次再访问服务器时，仍然默认访问上次分配的服务器，而不会再次随机分配。
```

**第四种 fair**（第三方）：

按后端服务器的响应时间来分配请求，响应时间短的优先分配。

```bash
#例如：    
upstream myserver {        
	server  192.168.88.132:8080; #参与负载均衡的服务器地址1        
	server  192.168.88.132:8081; #参与负载均衡的服务器地址2        
	fair ;   
}
# 如上，访问该服务器时，通过判断 8080 或者 8081 服务器响应的时间，来进行分配访问，
# 谁的响应时间短，优先访问谁。
```

## 四、动静分离

Nginx 动静分离简单来说就是把动态跟静态请求分开，不能理解成只是单纯的把动态页面和静态页面物理分离。严格意义上说应该是==动态请求跟静态请求分开==，可以理解成使用 **Nginx 处理静态页面**，**Tomcat 处理动态页面**。动静分离从目前实现角度来讲大致分为两种，**一种是纯粹把静态文件独立成单独的域名，放在独立的服务器上，也是目前主流推崇的方案**；**另外一种方法就是动态跟静态文件混合在一起发布，通过 nginx 来分开**。

通过location 指定不同的后缀名实现不同的请求转发。通过 expires 参数设置，可以使浏览器缓存过期时间，减少与服务器之前的请求和流量。具体 **Expires 定义：是给一个资源设定一个过期时间，也就是说无需去服务端验证，直接通过浏览器自身确认是否过期即可，所以不会产生额外的流量**。此种方法非常适合不经常变动的资源。（如果经常更新的文件，不建议使用 Expires 来缓存），我这里设置 3d，表示在这 3 天之内访问这个 URL，发送一个请求，比对服务器该文件最后更新时间没有变化，则不会从服务器抓取，返回状态码304，如果有修改，则直接从服务器重新下载，返回状态码 200。

```bash
#user  nobody;
worker_processes  1;
#error_log  logs/error.log;
#error_log  logs/error.log  notice;
#error_log  logs/error.log  info;
#pid        logs/nginx.pid;
events {
    worker_connections  1024;
}
http {
    include       mime.types;
    default_type  application/octet-stream;

    sendfile        on;

    keepalive_timeout  65;

        server {
        listen       80;
        server_name  192.168.88.132;
        #charset koi8-r;
        #access_log  logs/host.access.log  main;
        #配置静态资源访问
        location /www/ {
            root   /staticresource/;
            index  index.html index.htm;
        }
        location /image/{
            root /staticresource/;
            autoindex on;
        }
        #error_page  404              /404.html;
        # redirect server error pages to the static page /50x.html
        #
        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }

 }

}
```

