//package com.dexlace.mongo.started.config;
//
//import com.dexlace.mongo.MongoApplication;
//import com.dexlace.mongo.started.entity.Person;
//import com.mongodb.Mongo;
//import com.mongodb.client.MongoClient;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.mongodb.core.MongoOperations;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
//import org.springframework.data.mongodb.core.query.Query;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import static org.springframework.data.mongodb.core.query.Criteria.where;
//
///**
// * @Author: xiaogongbing
// * @Description:
// * @Date: 2021/5/6
// */
////SpringBoot的Junit集成测试
//@RunWith(SpringRunner.class)
////SpringBoot的测试环境初始化，参数：启动类
//@SpringBootTest(classes = MongoApplication.class)
//public class MongoConfigTest {
//    @Autowired
//    private MongoClient client;
//
//
//    // 对应mongoMongoClientFactoryBean
//    // 暂时不知如何使用
//    @Autowired
//    private MongoTemplate mongoTemplate;
//
//    @Test
//    public void testClient() {
//        MongoOperations mongoOps = new MongoTemplate(client, "database");
//        mongoOps.insert(new Person("Judy", 34));
//        mongoOps.findOne(new Query(where("name").is("Judy")), Person.class);
//    }
//
//    @Test
//    public void testTemplate() {
//
//        mongoTemplate.insert(new Person("WangWu", 68));
//        mongoTemplate.findOne(new Query(where("name").is("WangWu")), Person.class);
//    }
//
//}
