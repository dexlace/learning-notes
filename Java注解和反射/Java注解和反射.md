# Java注解和反射

## 一、注解

### 1.1 什么是注解

注解就是程序能够理解的“注释”

### 1.2 内置注解

内置注解就是我们的jdk所带的一些注解。常用的三个注解：

- **@Override**
- **@Deprecated**：这个注解我们应该也不会陌生，我们可能看不到这个注解，但是我们肯定在使用一些方法时会出现横线。==表示废弃==，这个注释可以修辞方法，属性，类，表示不鼓励程序员使用这样的元素，通常是因为他很危险或有更好的选择。
- **@SuperWarnings**：主要是==用来抑制警告信息的==，我们在写程序时，可能会报很多黄线的警告，但是不影响运行，我们就可以用这个注解来抑制隐藏它。与前俩个注解不同的是我们必须==给注解参数==才能正确使用他。经常用==@SuperWarnings("all")==

<img src="Java%E6%B3%A8%E8%A7%A3%E5%92%8C%E5%8F%8D%E5%B0%84.assets/image-20210707131052884.png" alt="image-20210707131052884" style="zoom:80%;" />

### 1.3 元注解

指的是修饰注解的注解

- **==@Target==**：表明该注解可以应用的java元素类型

<img src="Java%E6%B3%A8%E8%A7%A3%E5%92%8C%E5%8F%8D%E5%B0%84.assets/image-20210707153650107.png" alt="image-20210707153650107" style="zoom: 67%;" />

- **==@Retention==**:描述注解的生命周期

<img src="Java%E6%B3%A8%E8%A7%A3%E5%92%8C%E5%8F%8D%E5%B0%84.assets/image-20210707154020090.png" alt="image-20210707154020090" style="zoom:80%;" />

- ==**@Document**==
   表明该注解标记的元素可以被Javadoc 或类似的工具文档化
- ==**@Inherited**==
   表明使用了@Inherited注解的注解，所==标记的类的子类也会拥有这个注解==

### 1.4 自定义注解

```java
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface myannotation1 {

    String name(); 
    
    int age(); 
    
    String[] hobbies(); 
    
    String address() default "中国";
}
```

定义==注解类型元素==时需要注意如下几点：

**1.访问修饰符必须为public，不写默认为public；**

**2.该元素的类型只能是==基本数据类型、String、Class、枚举类型、注解类型（体现了注解的嵌套效果）以及上述类型的一位数组；==**

**3.该元素的名称一般定义为名词，如果注解中==只有一个元素==，请把名字起为==value==（后面使用会带来便利操作）；**

**4.==()==不是定义方法参数的地方，也不能在括号中定义任何参数，仅仅==只是一个特殊的语法==；**

**5.default代表默认值，值必须和第2点定义的类型一致；**

6.如果没有默认值，代表后续使用注解时必须给该类型元素赋值。**

<img src="Java%E6%B3%A8%E8%A7%A3%E5%92%8C%E5%8F%8D%E5%B0%84.assets/image-20210707161052748.png" alt="image-20210707161052748" style="zoom:67%;" />

<img src="Java%E6%B3%A8%E8%A7%A3%E5%92%8C%E5%8F%8D%E5%B0%84.assets/image-20210707161119201.png" alt="image-20210707161119201" style="zoom:80%;" />

<img src="Java%E6%B3%A8%E8%A7%A3%E5%92%8C%E5%8F%8D%E5%B0%84.assets/image-20210707161143883.png" alt="image-20210707161143883" style="zoom:80%;" />