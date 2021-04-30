# Spring Security原理

## 一、结构总览

Spring Security所解决的问题就是==**安全访问控制**==，而安全访问控制功能其实就是==对所有进入系统的请求进行拦截==， 校验每个请求是否能够

访问它所期望的资源。根据前边知识的学习，可以通过Filter或AOP等技术来实现，Spring Security对Web资源的保护是==靠Filter实现的==，

所以从这个Filter来入手，逐步深入Spring Security原理。 当初始化Spring Security时，会创建一个名为 ==<font color=red>SpringSecurityFilterChain</font>== 的

Servlet过滤器，类型为 org.springframework.security.web.FilterChainProxy，它实现了javax.servlet.Filter，因此外部的请求会经过此 

类，下图是Spring Security过虑器链结构图： 

<img src="Spring%20Security%E7%AC%94%E8%AE%B0.assets/image-20210417215439983.png" alt="image-20210417215439983" style="zoom:67%;" />

FilterChainProxy是一个代理，真正起作用的是FilterChainProxy中SecurityFilterChain所包含的各个Filter，同时这些==Filter作为Bean被

Spring管理==，它们是Spring Security核心，各有各的职责，但他们并不直接处理用户的**认证**，也不直接处理用户的**授权**，而是把它们

交给了**==<font color=red>认证管理器（AuthenticationManager）</font>==**和**==<font color=red>决策管理器 （AccessDecisionManager）</font>==**进行处理

<img src="Spring%20Security%E7%AC%94%E8%AE%B0.assets/image-20210417220002796.png" alt="image-20210417220002796" style="zoom:80%;" />

## 二、认证流程





<img src="Spring%20Security%E7%AC%94%E8%AE%B0.assets/image-20210417220813598.png" alt="image-20210417220813598" style="zoom:50%;" />

### 2.1  **过滤器UsernamePasswordAuthenticationFilter**

继承自==**AbstractAuthenticationProcessingFilter**==

主要是过滤器的==doFilter方法==

```java
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		if (!requiresAuthentication(request, response)) {
            // 逻辑一 判断是否为post请求，不是则进入下一个过滤器
			chain.doFilter(request, response);
			return;
		}
        
        try{
                  // 逻辑二 尝试认证
                 Authentication	authResult = attemptAuthentication(request, response);
        }catch(...){
            // 逻辑三 认证失败
            unsuccessfulAuthentication(request, response, failed);
        }
        
   
        // 逻辑四 认证成功
        successfulAuthentication(request, response, chain, authResult);

	}
```

下面主要介绍一下==attemptAuthentication方法==，该方法在其子类中必须实现

####  attemptAuthentication

```java
public Authentication attemptAuthentication(HttpServletRequest request,
			HttpServletResponse response) throws AuthenticationException {  
        // 逻辑一、先判断是否是post请求
    
        // 逻辑二、获取用户名
        //        获取密码
    
        // 逻辑三、Authentication接口实现类
		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
				username, password);

        // 逻辑四、使用ProviderManager的authenticater()方法进行身份认证，还是返回一个Authentication接口
		return this.getAuthenticationManager().authenticate(authRequest);
	}

```

下面介绍一下==Authentication接口==和`this.getAuthenticationManager()`返回的`ProviderManager`

  #### successfulAuthentication

<img src="Spring%20Security%E7%AC%94%E8%AE%B0.assets/image-20210418001210443.png" alt="image-20210418001210443" style="zoom:67%;" />

#### unsuccessfulAuthentication

<img src="Spring%20Security%E7%AC%94%E8%AE%B0.assets/image-20210418001240521.png" alt="image-20210418001240521" style="zoom:80%;" />

### 2.2 Authentication接口

 **UsernamePasswordAuthenticationToken**==继承==**AbstractAuthenticationToken**

​                                        **AbstractAuthenticationToken**==实现了==**Authentication接口**

Authentication 接口的实现类用于==<font color=red>存储用户认证信息，查看该接口具体定义</font>==

<img src="Spring%20Security%E7%AC%94%E8%AE%B0.assets/image-20210418004901859.png" alt="image-20210418004901859" style="zoom:80%;" />

<img src="Spring%20Security%E7%AC%94%E8%AE%B0.assets/image-20210418004831946.png" alt="image-20210418004831946" style="zoom:80%;" />

### 2.3 AuthenticationManager及其实现类ProviderManager

**ProviderManager** ==<font color=red>实现了</font>== **AuthenticationManager** 

<img src="Spring%20Security%E7%AC%94%E8%AE%B0.assets/image-20210418012050964.png" alt="image-20210418012050964" style="zoom:80%;" />

![image-20210418004551526](Spring%20Security%E7%AC%94%E8%AE%B0.assets/image-20210418004551526.png)

![image-20210418004642067](Spring%20Security%E7%AC%94%E8%AE%B0.assets/image-20210418004642067.png)

### 2.4 UserDetailsService

 ![image-20210418011818602](Spring%20Security%E7%AC%94%E8%AE%B0.assets/image-20210418011818602.png)

![image-20210418011849606](Spring%20Security%E7%AC%94%E8%AE%B0.assets/image-20210418011849606.png)

<img src="Spring%20Security%E7%AC%94%E8%AE%B0.assets/image-20210418012525680.png" alt="image-20210418012525680" style="zoom:80%;" />

![image-20210418012546382](Spring%20Security%E7%AC%94%E8%AE%B0.assets/image-20210418012546382.png)

### 2.5 总结认证流程

<img src="Spring%20Security%E7%AC%94%E8%AE%B0.assets/image-20210417220405692.png" alt="image-20210417220405692" style="zoom:60%;" />

让我们仔细分析认证过程： 

1. 用户提交==用户名、密码被==SecurityFilterChain中的==**<font color=red>UsernamePasswordAuthenticationFilter 过滤器</font>**==获取到， 封装为请求Authentication，通常情况下是UsernamePasswordAuthenticationToken这个实现类。 

2. 然后过滤器将Authentication==**<font color=red>提交至认证管理器（AuthenticationManager）</font>**==进行认证 

3. 认证成功后， AuthenticationManager 身份管理器返回一个**被填充满了信息的**（包括上面提到的**权限信息， 身份信息，细节信息**，但密码通常会被移除）==Authentication 实例==。 

4. ==**<font color=red>SecurityContextHolder 安全上下文容器</font>**==将第3步填充了信息的 Authentication,通过SecurityContextHolder.getContext().setAuthentication(…)方法，设置到其中。 

可以看出==**<font color=red>AuthenticationManager接口（认证管理器）是认证相关的核心接口，也是发起认证的出发点，</font>**==

它 的实现类为==**<font color=red>ProviderManager</font>**==。

而Spring Security支持多种认证方式，因此==**<font color=red>ProviderManager维护着一个 List<AuthenticationProvider> 列表</font>**==，存放多种认证方

式，最终实际的认证工作是由 AuthenticationProvider完成的。

咱们知道==**<font color=red>web表单的对应的AuthenticationProvider实现类为 DaoAuthenticationProvider</font>**==，

它的内部又维护着一个==**<font color=red>UserDetailsService负责UserDetails的获取</font>**==。最终AuthenticationProvider将==UserDetails填充至Authentication。== 

