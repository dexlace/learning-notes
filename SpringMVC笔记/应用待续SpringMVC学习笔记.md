# SpringMVC学习笔记  
## 一、SpringMVC介绍  


## 二、SpringMVC架构流程图  
![](%E5%BA%94%E7%94%A8%E5%BE%85%E7%BB%ADSpringMVC%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190725200556146_24168.png )  
## 三、SpringMVC入门  
### 3.1 项目结构  
![](%E5%BA%94%E7%94%A8%E5%BE%85%E7%BB%ADSpringMVC%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190725201254634_17982.png )  
### 3.2 配置前端控制器DispatcherServlet  
![](%E5%BA%94%E7%94%A8%E5%BE%85%E7%BB%ADSpringMVC%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190725203206380_1885.png )  
### 3.3 处理器映射器HandlerMapping配置及其详解  
#### 3.3.1 非注解方式配置  
![](%E5%BA%94%E7%94%A8%E5%BE%85%E7%BB%ADSpringMVC%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190725205643271_31744.png )  
#### 3.3.2 注解方式配置  
![](%E5%BA%94%E7%94%A8%E5%BE%85%E7%BB%ADSpringMVC%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190725210127176_14947.png )  
### 3.4 处理器Handler配置及其详解  
#### 3.4.1 非注解方式配置  
![](%E5%BA%94%E7%94%A8%E5%BE%85%E7%BB%ADSpringMVC%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190725220538894_20443.png )  
#### 3.4.2 注解方式配置  
![](%E5%BA%94%E7%94%A8%E5%BE%85%E7%BB%ADSpringMVC%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190725221649540_14906.png )  
### 3.5 处理器适配器配置及其详解  
#### 3.5.1 非注解方式配置  
![](%E5%BA%94%E7%94%A8%E5%BE%85%E7%BB%ADSpringMVC%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190725225305384_1467.png )  
#### 3.5.2 注解方式配置  
![](%E5%BA%94%E7%94%A8%E5%BE%85%E7%BB%ADSpringMVC%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190725230226307_12418.png )  
### 3.6 视图解析器ViewResolver及其详解  
![](%E5%BA%94%E7%94%A8%E5%BE%85%E7%BB%ADSpringMVC%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190725230814905_24881.png )  
### 3.7 书写Handler  
![](%E5%BA%94%E7%94%A8%E5%BE%85%E7%BB%ADSpringMVC%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190725233125456_21226.png )  
#### 3.7.1 对应于非注解方式之实现controller接口  
![](%E5%BA%94%E7%94%A8%E5%BE%85%E7%BB%ADSpringMVC%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190725233909902_6094.png )  
#### 3.7.2 对应于非注解方式之实现HttpRequestHandler接口  
![](%E5%BA%94%E7%94%A8%E5%BE%85%E7%BB%ADSpringMVC%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190725232935171_22188.png )  
#### 3.7.3 对应于注解方式的Handler  
![](%E5%BA%94%E7%94%A8%E5%BE%85%E7%BB%ADSpringMVC%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190725235346890_15857.png )  
### 3.8 测试  
略，自然很容易懂在做啥  
### 3.9 简化开发方式  
![](%E5%BA%94%E7%94%A8%E5%BE%85%E7%BB%ADSpringMVC%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190726001704586_199.png )  
Handler开发就用对应的注解开发方式，对应于3.7.3  

## 四、SpringMVC三大组件详解
对应于第三节中的介绍，不谈。  
## 五、SpringMVC整合Mybatis  
### 5.1 目录结构和导包 
![](%E5%BA%94%E7%94%A8%E5%BE%85%E7%BB%ADSpringMVC%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190726013627177_23991.png )  
### 5.2 Spring及SpringMVC方面  
#### 5.2.1 Spring容器需要随web启动  
![](%E5%BA%94%E7%94%A8%E5%BE%85%E7%BB%ADSpringMVC%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190726005153798_12271.png )  
#### 5.2.2 SpringMVC配置文件springmvc.xml  
![](%E5%BA%94%E7%94%A8%E5%BE%85%E7%BB%ADSpringMVC%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190726010701625_29122.png )  
#### 5.2.3 在web.xml配置SpringMVC的前端控制器  
![](%E5%BA%94%E7%94%A8%E5%BE%85%E7%BB%ADSpringMVC%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190726011104301_17057.png )  
### 5.3 Mybatis方面  
#### 5.3.1 sqlMapConfig.xml文件配置  
![](%E5%BA%94%E7%94%A8%E5%BE%85%E7%BB%ADSpringMVC%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190726012354044_24882.png )  
#### 5.3.2 各种mapper类及其映射文件  
![](%E5%BA%94%E7%94%A8%E5%BE%85%E7%BB%ADSpringMVC%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190726012609351_28169.png)  
#### 5.3.3 mybatis相关对象托付给spring管理  
![](%E5%BA%94%E7%94%A8%E5%BE%85%E7%BB%ADSpringMVC%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190726013245382_15549.png )  
### 5.4 其他方面  
比如开启事务，自己配就是，没啥好说的  
## 六、controller层参数绑定  
### 6.1 参数绑定过程  
![](%E5%BA%94%E7%94%A8%E5%BE%85%E7%BB%ADSpringMVC%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190726111241799_12837.png )  
### 6.2 默认支持的类型  
![](%E5%BA%94%E7%94%A8%E5%BE%85%E7%BB%ADSpringMVC%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190726113020662_16800.png )  
### 6.3 简单类型  
![](%E5%BA%94%E7%94%A8%E5%BE%85%E7%BB%ADSpringMVC%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190726132519887_27211.png )  
### 6.4 pojo类型  
![](%E5%BA%94%E7%94%A8%E5%BE%85%E7%BB%ADSpringMVC%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190726134409292_13284.png )  
### 6.5 包装类型pojo  
![](%E5%BA%94%E7%94%A8%E5%BE%85%E7%BB%ADSpringMVC%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190726135340903_29057.png )  
### 6.6 集合类型  
#### 6.6.1 数组类型  
![](%E5%BA%94%E7%94%A8%E5%BE%85%E7%BB%ADSpringMVC%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190726143807708_11227.png )  
#### 6.6.2  list绑定  
![](%E5%BA%94%E7%94%A8%E5%BE%85%E7%BB%ADSpringMVC%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190726154632267_28676.png )  
#### 6.6.3 map绑定  
![](%E5%BA%94%E7%94%A8%E5%BE%85%E7%BB%ADSpringMVC%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190726150329867_3062.png )  
## 七、controller层返回值类型  
### 7.1 ModelAndView  
![](%E5%BA%94%E7%94%A8%E5%BE%85%E7%BB%ADSpringMVC%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190726151214011_23473.png)  
### 7.2 String类型  
![](%E5%BA%94%E7%94%A8%E5%BE%85%E7%BB%ADSpringMVC%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190726151900835_13052.png )  
### 7.3 void类型  
![](%E5%BA%94%E7%94%A8%E5%BE%85%E7%BB%ADSpringMVC%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190726152134155_11480.png )  
## 八、@RequestMapping  
### 8.1 基础用法  
![](%E5%BA%94%E7%94%A8%E5%BE%85%E7%BB%ADSpringMVC%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190726153957949_1657.png )  
### 8.2 处理多个url  
![](%E5%BA%94%E7%94%A8%E5%BE%85%E7%BB%ADSpringMVC%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190726154233215_9922.png)  
### 8.3 与@RequestParam配合使用  
![](%E5%BA%94%E7%94%A8%E5%BE%85%E7%BB%ADSpringMVC%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190726155615988_16922.png )  
### 8.4 指定http请求的各种方法  
![](%E5%BA%94%E7%94%A8%E5%BE%85%E7%BB%ADSpringMVC%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190726154811770_18049.png )  
### 8.5 处理生产和消费者对象(待续)  
### 8.6 处理请求参数(待续)  
### 8.7 处理动态url(待续)  
## 九、SpringMVC其他说明  
### 9.1 乱码解决  
![](%E5%BA%94%E7%94%A8%E5%BE%85%E7%BB%ADSpringMVC%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190726162450304_21331.png )  
![](%E5%BA%94%E7%94%A8%E5%BE%85%E7%BB%ADSpringMVC%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190726162520498_20491.png )  

### 9.2 自定义参数绑定(以日期为例)  
![](%E5%BA%94%E7%94%A8%E5%BE%85%E7%BB%ADSpringMVC%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190726200941353_21355.png )  
### 9.3 SpringMVC异常处理  
![](%E5%BA%94%E7%94%A8%E5%BE%85%E7%BB%ADSpringMVC%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0.assets/20190726213105759_18197.png )  
### 9.4 上传图片  
### 9.5 json数据交互 
### 9.6 Restful风格开发  
### 9.7 拦截器  
### 9.8 Springmvc校验  
### 9.9 SpringMVC数据回显  
## 十、SpringMVC与Struts2的区别  