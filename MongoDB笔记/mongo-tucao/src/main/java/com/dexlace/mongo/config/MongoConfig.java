package com.dexlace.mongo.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.connection.ClusterConnectionMode;
import com.mongodb.connection.ClusterType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;


import java.util.Arrays;

/**
 * @Author: xiaogongbing
 * @Description: 整个配置都是获取到MongoDB的连接信息
 * 请万万写一个配置类，千万不要默认配置
 * @Date: 2021/5/6
 */
@Configuration
public class MongoConfig {
    //    spring:
//    data:
//    mongodb:
//    username: root
//    password: root
//    host: 192.168.205.118
//    port: 27017
//    authentication-database: admin
//    database: database

//    @Value("${spring.data.mongodb.username}")
//    private String userName;
//    @Value("${spring.data.mongodb.password}")
//    private String password;
//    @Value("${spring.data.mongodb.host}")
//    private String host;
//    @Value("${spring.data.mongodb.port}")
//    private Integer port;
////    @Value("${spring.data.mongodb.authentication-database}")
////    private String authenticationDatabase;
//    @Value("${spring.data.mongodb.database}")  // 这个可以不注入，spring自己会去注入到template
//    private String database;


//    @Value("${spring.data.mongodb.uri}")
//    private String connectString;



    /*
     * Use the standard Mongo driver API to create a com.mongodb.client.MongoClient instance.
     * 描述了一个客户端
//     */
//    @Bean
//    public MongoClient mongoClient() {
//
//        MongoCredential credential=MongoCredential.createCredential("root", "admin", "root".toCharArray());
//        MongoClientSettings setting = MongoClientSettings.builder()
//                .credential(credential)
//                .applyConnectionString(new ConnectionString("mongodb://192.168.205.118:27017")).build();
//        return MongoClients.create(setting);
//        //以下是没有认证的
////        return MongoClients.create("mongodb://192.168.205.118:27017");
//    }





    /*
     * Use the standard Mongo driver API to create a com.mongodb.client.MongoClient instance.
     * 描述了一个客户端  最好的用法
     */

//    @Bean
//    public MongoClient mongoClient() {
//
//
//        // 这里的database是authentication-database，除非脑瘫，不然不会不和需要控制权限的数据库名不一样
//        MongoCredential credential = MongoCredential.createCredential(userName, database, password.toCharArray());
//        MongoClientSettings setting = MongoClientSettings.builder()
//                .credential(credential)
//                .applyToClusterSettings(builder ->
//                        builder.hosts(Arrays.asList(new ServerAddress(host, port)))
//                                .mode(ClusterConnectionMode.SINGLE)
//                                .requiredClusterType(ClusterType.STANDALONE)
//                ).build();
//        return MongoClients.create(setting);
//    }

    /**********************************************************************************************************/
    /******************以上是单实例的client配置方法，看起来比较复杂，其实不如connectString来的简单*********************/
    /*******************下面是connectString 实现副本集的连接******************************************************/

//    @Bean
//    public MongoClient mongoClient() {
//
//
//        return MongoClients.create(connectString);
//    }





//    @Bean
//    public MongoTemplate mongoTemplate() {
//        return new MongoTemplate(mongoClient(), database);
//    }




    /*
     * Factory bean that creates the com.mongodb.client.MongoClient instance
     * 这个方法也能返回一个client bean
     * 可以直接使用mongo对象 但是这里的mongo对象不知如何使用 好像无法使用以注入mongoTemplate
     * 就不用mongo类了
     * 同样描述了一个客户端 不能同时注册
     */
//    @Bean
//    public MongoClientFactoryBean mongo() {
//        MongoClientFactoryBean mongo = new MongoClientFactoryBean();
//        mongo.setHost("192.168.205.118");
//        mongo.setPort(27017);
//        return mongo;
//    }


    /**
     * 注入一个mongo的数据库连接信息
     * 这个  需要原生
     * <dependency>
     * <groupId>org.springframework.data</groupId>
     * <artifactId>spring-data-mongodb</artifactId>
     * <version>3.2.0</version>
     * </dependency>
     * 这里不演示
     *
     * @return
     */
//    @Bean
//    public MongoDatabaseFactory mongoDatabaseFactory() {
//        return new SimpleMongoClientDatabaseFactory(MongoClients.create("mongodb://192.168.205.118"), "database");
//    }

}
