//package com.dexlace.mongo.started.config;
//
//import com.mongodb.ConnectionString;
//import com.mongodb.MongoClientSettings;
//import com.mongodb.MongoCredential;
//import com.mongodb.ServerAddress;
//import com.mongodb.client.MongoClient;
//import com.mongodb.client.MongoClients;
//import com.mongodb.connection.ClusterConnectionMode;
//import com.mongodb.connection.ClusterType;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.mongodb.core.MongoTemplate;
//
//import java.util.Arrays;
//
///**
// * @Author: xiaogongbing
// * @Description: 整个配置都是获取到MongoDB的连接信息
// * @Date: 2021/5/6
// */
//@Configuration
//public class MongoConfig {
//
//    /*
//     * Use the standard Mongo driver API to create a com.mongodb.client.MongoClient instance.
//     * 描述了一个客户端
////     */
////    @Bean
////    public MongoClient mongoClient() {
////
////        MongoCredential credential=MongoCredential.createCredential("root", "admin", "root".toCharArray());
////        MongoClientSettings setting = MongoClientSettings.builder()
////                .credential(credential)
////                .applyConnectionString(new ConnectionString("mongodb://192.168.205.118:27017")).build();
////        return MongoClients.create(setting);
////        //以下是没有认证的
//////        return MongoClients.create("mongodb://192.168.205.118:27017");
////    }
//
//
//
//
//
//    /*
//     * Use the standard Mongo driver API to create a com.mongodb.client.MongoClient instance.
//     * 描述了一个客户端  最好的用法
//    */
//
//    @Bean
//    public MongoClient mongoClient() {
//
//        MongoCredential credential=MongoCredential.createCredential("root", "admin", "root".toCharArray());
//        MongoClientSettings setting = MongoClientSettings.builder()
//                .credential(credential)
//                .applyToClusterSettings(builder ->
//                        builder.hosts(Arrays.asList(new ServerAddress("192.168.205.118", 27017)))
//                                .mode(ClusterConnectionMode.SINGLE)
//                                .requiredClusterType(ClusterType.STANDALONE)
//                ).build();
//        return MongoClients.create(setting);
//    }
//
//
//
//    @Bean
//    public MongoTemplate mongoTemplate() {
//        return new MongoTemplate(mongoClient(), "database");
//    }
//
//    /*
//     * Factory bean that creates the com.mongodb.client.MongoClient instance
//     * 这个方法也能返回一个client bean
//     * 可以直接使用mongo对象 但是这里的mongo对象不知如何使用 好像无法使用以注入mongoTemplate
//     * 就不用mongo类了
//     * 同样描述了一个客户端 不能同时注册
//     */
////    @Bean
////    public MongoClientFactoryBean mongo() {
////        MongoClientFactoryBean mongo = new MongoClientFactoryBean();
////        mongo.setHost("192.168.205.118");
////        mongo.setPort(27017);
////        return mongo;
////    }
//
//
//    /**
//     * 注入一个mongo的数据库连接信息
//     * 这个  需要原生
//     * <dependency>
//     * <groupId>org.springframework.data</groupId>
//     * <artifactId>spring-data-mongodb</artifactId>
//     * <version>3.2.0</version>
//     * </dependency>
//     * 这里不演示
//     *
//     * @return
//     */
////    @Bean
////    public MongoDatabaseFactory mongoDatabaseFactory() {
////        return new SimpleMongoClientDatabaseFactory(MongoClients.create("mongodb://192.168.205.118"), "database");
////    }
//
//}
