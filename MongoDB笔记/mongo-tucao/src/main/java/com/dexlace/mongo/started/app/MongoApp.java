//package com.dexlace.mongo.started.app;
//
//import com.dexlace.mongo.started.entity.Person;
//import com.mongodb.client.MongoClients;
//import org.springframework.data.mongodb.core.MongoOperations;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.query.Query;
//
//import static org.springframework.data.mongodb.core.query.Criteria.where;
///**
// * @Author: xiaogongbing
// * @Description:
// * @Date: 2021/5/6
// */
//public class MongoApp {
//    public static void main(String[] args) throws Exception {
//
//        MongoOperations mongoOps = new MongoTemplate(MongoClients.create("mongodb://192.168.205.118"), "database");
//        mongoOps.insert(new Person("Joe", 34));
//        mongoOps.findOne(new Query(where("name").is("Joe")), Person.class);
//
////        mongoOps.dropCollection("person");
//    }
//}
