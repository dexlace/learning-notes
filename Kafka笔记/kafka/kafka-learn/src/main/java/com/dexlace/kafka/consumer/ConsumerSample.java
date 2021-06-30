package com.dexlace.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;


import java.time.Duration;
import java.util.*;

public class ConsumerSample {
    private final static String TOPIC_NAME ="judy";

    public static void main(String[] args) {
//        helloworld();
        // 手动提交offset
//        commitedOffset();
        // 手动对每个Partition进行提交
       commitedOffsetWithPartition();
        // 手动订阅某个或某些分区，并提交offset
//        commitedOffsetWithPartition2();
        // 手动指定offset的起始位置，及手动提交offset
//        controlOffset();
        // 流量控制
        //controlPause();
    }
    /*
           工作里这种用法，有，但是不推荐
        */
    private static void helloworld(){
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "kafka100:9092,kafka101:9092,kafka102:9092");
        props.setProperty("group.id", "test");
        props.setProperty("enable.auto.commit", "true");
        props.setProperty("auto.commit.interval.ms", "1000");
        props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String,String> consumer = new KafkaConsumer(props);
        // 消费订阅哪一个Topic或者几个Topic
        consumer.subscribe(Arrays.asList(TOPIC_NAME));
        while (true) {
            System.out.println("hello sab");
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records)
                System.out.printf("patition = %d , offset = %d, key = %s, value = %s%n",
                        record.partition(),record.offset(), record.key(), record.value());
        }

    }


    /*
       手动提交offset
       因为消费到数据后一般有业务逻辑，如果耗时消费了，而已经批量消费了，非常不合理，所以手动提交
    */
    private static void commitedOffset() {
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "kafka100:9092,kafka101:9092,kafka102:9092");
        props.setProperty("group.id", "test");
        props.setProperty("enable.auto.commit", "false");//不自动提交

        props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer(props);
        // 消费订阅哪一个Topic或者几个Topic
        consumer.subscribe(Arrays.asList(TOPIC_NAME));
        while (true) {
            System.out.println("怎么会消费不到");
            ConsumerRecords<String, String> records = consumer.poll(10000);
            for (ConsumerRecord<String, String> record : records) {
                // 想把数据保存到数据库，成功就成功，不成功...
                // TODO record 2 db
                System.out.printf("patition = %d , offset = %d, key = %s, value = %s%n",
                        record.partition(), record.offset(), record.key(), record.value());
                // 如果失败，则回滚， 不要提交offset
            }

            // 如果成功，手动通知offset提交 也是批量 提交所有的
            consumer.commitAsync();
            System.out.println("怎么会消费不到操-----");
        }


    }


    /*
        手动提交offset,并且手动控制partition
     */
    private static void commitedOffsetWithPartition() {
        Properties props = new Properties();
        props.setProperty("bootstrap.servers",  "kafka100:9092,kafka101:9092,kafka102:9092");
        props.setProperty("group.id", "test");
        props.setProperty("enable.auto.commit", "false");


        props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer(props);
        // 消费订阅哪一个Topic或者几个Topic
        consumer.subscribe(Arrays.asList(TOPIC_NAME));
        while (true) {

            System.out.println("=============partition - "+ " start================");


            ConsumerRecords<String, String> records = consumer.poll(10000);
            // 每个partition单独处理
            for(TopicPartition partition : records.partitions()){
                System.out.println("=============partition - "+ partition +" start================");
                // 获取每个partition的数据
                List<ConsumerRecord<String, String>> pRecord = records.records(partition);
                for (ConsumerRecord<String, String> record : pRecord) {
                    System.out.printf("patition = %d , offset = %d, key = %s, value = %s%n",
                            record.partition(), record.offset(), record.key(), record.value());

                }
                // 得到分区的最后一次获取的offset
                long lastOffset = pRecord.get(pRecord.size() -1).offset();
                // 单个partition中的offset，并且进行提交
                Map<TopicPartition, OffsetAndMetadata> offset = new HashMap<>();
                // 注意这里提交的时下次消费的起始位置，所以+1
                offset.put(partition,new OffsetAndMetadata(lastOffset+1));
                // 提交offset 单独提交
                consumer.commitSync(offset);
                System.out.println("=============partition - "+ partition +" end================");
            }

            System.out.println("=============partition - "+ " stop================");
        }
    }

    /*
       手动提交offset,并且手动控制partition,更高级
    */
    private static void commitedOffsetWithPartition2() {
        Properties props = new Properties();
        props.setProperty("bootstrap.servers",  "kafka100:9092,kafka101:9092,kafka102:9092");
        props.setProperty("group.id", "test");
        props.setProperty("enable.auto.commit", "false");
        props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer(props);

        // jiangzh-topic - 0,1两个partition
        TopicPartition p0 = new TopicPartition(TOPIC_NAME, 0);
        TopicPartition p1 = new TopicPartition(TOPIC_NAME, 1);

        // 消费订阅哪一个Topic或者几个Topic
//        consumer.subscribe(Arrays.asList(TOPIC_NAME));

        // 消费订阅某个Topic的某个分区
        consumer.assign(Arrays.asList(p0,p1));

        while (true) {
            System.out.println("judy is here...");
            ConsumerRecords<String, String> records = consumer.poll(100);
            // 每个partition单独处理
            for(TopicPartition partition : records.partitions()){
                List<ConsumerRecord<String, String>> pRecord = records.records(partition);
                for (ConsumerRecord<String, String> record : pRecord) {
                    System.out.printf("patition = %d , offset = %d, key = %s, value = %s%n",
                            record.partition(), record.offset(), record.key(), record.value());

                }
                long lastOffset = pRecord.get(pRecord.size() -1).offset();
                // 单个partition中的offset，并且进行提交
                Map<TopicPartition, OffsetAndMetadata> offset = new HashMap<>();
                offset.put(partition,new OffsetAndMetadata(lastOffset+1));
                // 提交offset
                consumer.commitSync(offset);
                System.out.println("=============partition - "+ partition +" end================");
            }
            System.out.println("judy is gone...");
        }
    }


    /*
            手动指定offset的起始位置，及手动提交offset
         */
    private static void controlOffset() {
        Properties props = new Properties();
        props.setProperty("bootstrap.servers",  "kafka100:9092,kafka101:9092,kafka102:9092");
        props.setProperty("group.id", "test");
        props.setProperty("enable.auto.commit", "false");
        props.setProperty("auto.commit.interval.ms", "1000");
        props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer(props);

        // jiangzh-topic - 0,1两个partition
        TopicPartition p0 = new TopicPartition(TOPIC_NAME, 0);

        // 消费订阅某个Topic的某个分区
        consumer.assign(Arrays.asList(p0));

        while (true) {
            // 手动指定offset起始位置
            /*
                1、人为控制offset起始位置
                2、如果出现程序错误，重复消费一次
             */
            /*
                1、第一次从0消费【一般情况】
                2、比如一次消费了100条， offset置为101并且存入Redis
                3、每次poll之前，从redis中获取最新的offset位置
                4、每次从这个位置开始消费
             */
            consumer.seek(p0, 700);

            ConsumerRecords<String, String> records = consumer.poll(10000);
            // 每个partition单独处理
            for(TopicPartition partition : records.partitions()){
                List<ConsumerRecord<String, String>> pRecord = records.records(partition);
                for (ConsumerRecord<String, String> record : pRecord) {
                    System.err.printf("patition = %d , offset = %d, key = %s, value = %s%n",
                            record.partition(), record.offset(), record.key(), record.value());

                }
                long lastOffset = pRecord.get(pRecord.size() -1).offset();
                // 单个partition中的offset，并且进行提交
                Map<TopicPartition, OffsetAndMetadata> offset = new HashMap<>();
                offset.put(partition,new OffsetAndMetadata(lastOffset+1));
                // 提交offset
                consumer.commitSync(offset);
                System.out.println("=============partition - "+ partition +" end================");
            }
        }
    }


}