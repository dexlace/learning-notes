package com.dexlace.mongo.dao;

import com.dexlace.mongo.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Author: xiaogongbing
 * @Description: 通过继承MongoRepository接口 先继承
 * MongoRepository<T,TD>接口，其中T为仓库保存的bean类，TD为该bean的唯一标识的类型，一般为ObjectId。
 * 之后在service中注入该接口就可以使用，无需实现里面的方法，spring会根据定义的规则自动生成。
 * MongoRepository比mongoTemplate更灵活
 *
 *
 * Repository 接口是 Spring Data 的一个核心接口，它不提供任何方法，开发者需要在自己定义的接口中声明需要的方法
 * 与继承 Repository 等价的一种方式。@RepositoryDefinition 注解，并为其指定 domainClass 和 idClass 属性。
 * 目前我所了解 两种方式没有区别
 *
 * public interface Repository< T, ID extends Serializable> { } 仅仅是一个标识，表明任何继承它的均为仓库接口类
 * CrudRepository： 继承 Repository，实现了一组 CRUD 相关的方法
 * PagingAndSortingRepository： 继承 CrudRepository，实现了一组分页排序相关的方法
 * JpaRepository： 继承 PagingAndSortingRepository，实现一组 JPA 规范相关的方法
 * MongoRepository继承了PagingAndSortingRepository

 * @Date: 2021/5/6
 */
public interface CommentRepository extends MongoRepository<Comment,String> {

    //根据父id，查询子评论的分页列表 parentid是comment中的属性
     Page<Comment> findByParentid(String parentid, Pageable pageable);

}
