package com.dexlace.kafka.admin;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.internals.Topic;

import java.sql.SQLOutput;
import java.util.*;

/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/5/26
 */
public class AdminSample {


    public final static String TOPIC_NAME="judy";

    public static void main(String[] args) throws Exception {
//        System.out.println("adminClient:"+AdminSample.adminClient());
        createTopic();
//        topicList();
//        delTopics();
        describeTopics();
 //       describeConfig();
    }



    public static void createTopic(){
        AdminClient adminClient=adminClient();
        // 主题名，分区数，副本集数
        NewTopic newTopic=new NewTopic(TOPIC_NAME,3,(short)3);
        CreateTopicsResult topics = adminClient.createTopics(Arrays.asList(newTopic));
        System.out.println("topics: " + topics);
    }


    public static AdminClient adminClient() {
        Properties properties = new Properties();
        properties.setProperty(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.205.100:9092,192.168.205.101:9092,192.168.205.102:9092");
        AdminClient adminClient = AdminClient.create(properties);

        return adminClient;
    }

    /**
     * 获取topic列表
     */
    public static void topicList() throws Exception{
        AdminClient adminClient = adminClient();
        // 是否查看internal选项
        ListTopicsOptions options = new ListTopicsOptions();
        options.listInternal(true);
//        ListTopicsResult listTopicsResult = adminClient.listTopics();
        ListTopicsResult listTopicsResult = adminClient.listTopics(options);
        Set<String> names = listTopicsResult.names().get();

        names.stream().forEach(System.out::println);

    }

    /*
     删除Topic
    */
    public static void delTopics() throws Exception {
        AdminClient adminClient = adminClient();
        DeleteTopicsResult deleteTopicsResult = adminClient.deleteTopics(Arrays.asList(TOPIC_NAME));
        deleteTopicsResult.all().get();
    }



    /*
       描述Topic

       name ：myfirst ,
       desc: (name=myfirst,
       internal=false,
       partitions=
       (partition=0, leader=kafka101:9092 (id: 1 rack: null),replicas=kafka101:9092 (id: 1 rack: null), kafka102:9092 (id: 2 rack: null), isr=kafka101:9092 (id: 1 rack: null), kafka102:9092 (id: 2 rack: null)),
       (partition=1, leader=kafka102:9092 (id: 2 rack: null), replicas=kafka102:9092 (id: 2 rack: null), kafka100:9092 (id: 0 rack: null), isr=kafka102:9092 (id: 2 rack: null), kafka100:9092 (id: 0 rack: null)),
       (partition=2, leader=kafka100:9092 (id: 0 rack: null), replicas=kafka100:9092 (id: 0 rack: null), kafka102:9092 (id: 2 rack: null), isr=kafka100:9092 (id: 0 rack: null), kafka102:9092 (id: 2 rack: null)))


    */
    public static void describeTopics() throws Exception {
        AdminClient adminClient = adminClient();
        DescribeTopicsResult describeTopicsResult = adminClient.describeTopics(Arrays.asList(TOPIC_NAME));
        Map<String, TopicDescription> stringTopicDescriptionMap = describeTopicsResult.all().get();
        Set<Map.Entry<String, TopicDescription>> entries = stringTopicDescriptionMap.entrySet();
        entries.stream().forEach((entry)->{
            System.out.println("name ："+entry.getKey()+" , desc: "+ entry.getValue());
        });
    }


    /**
     * 配置信息的描述
     * @throws Exception
     */
    public static void describeConfig() throws Exception{
        AdminClient adminClient = adminClient();
        // TODO 这里做一个预留，集群时会讲到
//        ConfigResource configResource = new ConfigResource(ConfigResource.Type.BROKER, TOPIC_NAME);

        ConfigResource configResource = new ConfigResource(ConfigResource.Type.TOPIC, TOPIC_NAME);
        DescribeConfigsResult describeConfigsResult = adminClient.describeConfigs(Arrays.asList(configResource));
        Map<ConfigResource, Config> configResourceConfigMap = describeConfigsResult.all().get();
        configResourceConfigMap.entrySet().stream().forEach((entry)->{
            System.out.println("configResource : "+entry.getKey()+" , Config : "+entry.getValue());
        });
    }


}
