# controller接收的参数和返回值

## 一、接收的参数

### 1.1 请求路径参数

#### 1.1.1 @PathVariable

获取==路径参数==。即`url/{id}`这种形式

#### 1.1.2@RequestParam

获取==查询参数==。即`url?param1=value1&param2=value2`这种形式

### 1.2 请求体参数

#### 1.2.1 @RequestBody

```java
package com.dexlace.controller;

import com.dexlace.entity.Person;
import org.springframework.web.bind.annotation.*;


/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/8/22
 */
@RestController
@RequestMapping("/hello")
public class HelloController {

    // 不可使用PostMapping
//    @RequestMapping("/var/{id}")
    @GetMapping("/var/{id}")
    public String helloVariable(@PathVariable(name = "id") String id) {
        return id;
    }


    // 不可使用PostMapping
//    @RequestMapping("/param")
    @GetMapping("/param")
    public String helloParam(@RequestParam(name = "name") String name) {

        return name;
    }

    // 不可使用getMapping
    @PostMapping(path = "/body")
    public String helloBody(@RequestBody Person person) {

        return person.getName();
    }

    // 不可使用getMapping
    // 不使用@RequestBody注解
    // 则不能以json串的格式提交 只能用form-data
    @PostMapping(path = "/body2")
    public String helloBody2(Person person) {

        return person.getName();
    }


    // @RequestBody和@RequestParam可以同时使用
    // 此时不能使用@PostMapping和@GetMapping
    @RequestMapping(path = "/bodyAndParam")
    public String helloBodyAndParam(@RequestBody Person person, @RequestParam("gender") String gender) {
        System.out.println(gender);

        return person.getName();
    }


    // 不能使用多个@RequestBody


    // 请求头
    // 注意可以去看看如何自定义请求头信息
    // 类似可以获取cookie信息
    @PostMapping("/header")
    public  void header(@RequestHeader(name = "User-Agent") String myHeader) {
        System.out.println("myHeader=" + myHeader);

    }


    // 返回值其实好说
    // 单独一个@Controller返回的是页面跳转路径
    // @Controller+@ResponseBody返回的是json
    // @RestController返回的是json


    // 分页参数 不难 会传参就行
    // @PathVariable可以与post请求共存


}

```



