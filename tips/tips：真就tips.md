# tips：真就tips

## 1、mybatis-plus的代码生成器

## 2、 mybatis-plus的分页插件

## 3、mybatis-plus的自定义ID生成器

## 4、mybatis-plus的wrapper





## 2、自定义starter

## 3、@PostConstruct注解

 被==@PostConstruct修饰的方法==会在==服务器加载Servle的时候运行==，并且只会被服务器执行一次。PostConstruct在构造函数之后执行，服务器加载Servlet -> ==servlet 构造函数的加载 -> postConstruct ->init==（init是在service 中的初始化方法. 创建service 时发生的事件.） ->==Service->destory->predestory->服务器卸载serlvet==

那么问题：spring中Constructor、@Autowired、@PostConstruct的顺序
==Constructor >> @Autowired >> @PostConstruct==

==@PostConstruct应用场景：==
如果想在生成对象时候==<font color=red>完成某些初始化操作</font>==，而偏偏==<font color=red>这些初始化操作又依赖于依赖注入</font>==，那么就==<font color=red>无法在构造函数中实现</font>==。为此，可以使用@PostConstruct注解一个方法来完成初始化，@PostConstruct注解的方法将会在依赖注入完成后被自动调用。

**这个==注解是由Java提供的==，它用来==修饰一个非静态的void方法==。它会在==服务器加载Servlet的时候运行，并且只运行一次==**

典型用例：

```java
@Component
public class SystemConstant {

    public static String surroundings;

    @Value("${spring.profiles.active}")
    public String environment;

    @PostConstruct
    public void initialize() {
        System.out.println("初始化环境...");
        surroundings = this.environment;
    }
}
```

这样的话我们可以拿到一个==全局的surroundings==,因为上述initialize()方法会加载一次。

再比如，我们每次创建Redis工具类，使用RedisTemplate操作，==每次使用Redis工具类只能先注入到容器然后再调用==，使用了这个注解就可以完美的解决这种尴尬的问题

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class RedisUtil {

    // 我们可以全局拿到这个工具类及其redisTemplates静态对象
    private static RedisTemplate<Object, Object> redisTemplates;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @PostConstruct
    public void initialize() {
        redisTemplates = this.redisTemplate;
    }

    /**
     * 添加元素
     *
     * @param key
     * @param value
     */
    public static void set(Object key, Object value) {

        if (key == null || value == null) {
            return;
        }
        redisTemplates.opsForValue().set(key, value);
    }
}
```

## 4、@ControllerAdvice



## 5、JAVA8新特性

## 6、实体类转换

## 7、CommandLineRunner

在使用SpringBoot构建项目时，我们通常有一些==预先数据的加载==。那么SpringBoot提供了一个简单的方式来实现==CommandLineRunner。==

CommandLineRunner是一个接口，我们需要时，只需实现该接口就行。如果==存在多个加载的数据，我们也可以使用@Order注解来排序==。
案例：
分别定义了<font color=red>一个数据加载类MyStartupRunner1,排序为2；以及另一个数据加载类MyStartupRunner2,排序为1。</font>

```java
@Component
@Order(value = 2)
public class MyStartupRunner1 implements CommandLineRunner{
@Override
public void run(String... strings) throws Exception {
    System.out.println(">>>>>>>>>>>>>>>服务启动执行，执行加载数据等操作 MyStartupRunner1 order 2 <<<<<<<<<<<<<");
    }
}

@Component
@Order(value = 1)
public class MyStartupRunner2 implements CommandLineRunner {
@Override
public void run(String... strings) throws Exception {
    System.out.println(">>>>>>>>>>>>>>>服务启动执行，执行加载数据等操作 MyStartupRunner2 order 1 <<<<<<<<<<<<<");
    }
}
```

那么order小的先执行

## 8、自定义注解

## 9、统一异常

## 10、统一响应

## 11、日志模板

## 12、反射

## 13、 缓存模板









