package com.dexlace.kafka.producer;

import org.apache.kafka.clients.producer.*;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ProducerSample {

    private final static String TOPIC_NAME="judy";

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // Producer异步发送演示
//        producerSend();
        // Producer异步发送带回调函数
//        producerSendWithCallback();
        // Producer异步阻塞（同步）发送演示
//        producerSyncSend();

        // Producer异步发送带回调函数和Partition负载均衡
        producerSendWithCallbackAndPartition();
    }


    /*        一、Producer异步发送演示
   KafkaProducer：需要创建一个生产者对象，用来发送数据
   ProducerConfig：获取所需的一系列配置参数
   ProducerRecord：每条数据都要封装成一个 ProducerRecord 对象
    */
    public static void producerSend() {
        Properties properties = new Properties();
        /**
         * /kafka 集群，broker-list配置：bootstrap.servers
         */
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.205.100:9092,192.168.205.101:9092,192.168.205.102:9092");
        /**
         * 确认机制
         */
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        /**
         * 重试次数
         */
        properties.put(ProducerConfig.RETRIES_CONFIG, "0");
        /**
         * 批次大小
         */
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, "16384");
        /**
         * 等待时间：1ms
         */
        properties.put(ProducerConfig.LINGER_MS_CONFIG, "1");
        /**
         * 缓冲区大小
         */
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, "33554432");

        /**
         * 键的序列化
         */
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        /**
         * value的序列化
         */
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        // Producer的主对象
        Producer<String, String> producer = new KafkaProducer<>(properties);

        // 消息对象 - ProducerRecoder
        for (int i = 0; i < 100; i++) {
            ProducerRecord<String, String> record =
                    new ProducerRecord<>(TOPIC_NAME, "key-" + i, "value-" + i);

            producer.send(record);
        }

        // 所有的通道打开都需要关闭
        producer.close();
    }


    /*
        二、Producer异步发送带回调函数
   */
    public static void producerSendWithCallback() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.205.100:9092,192.168.205.101:9092,192.168.205.102:9092");
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        properties.put(ProducerConfig.RETRIES_CONFIG, "0");
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, "16384");
        properties.put(ProducerConfig.LINGER_MS_CONFIG, "1");
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, "33554432");

        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        // Producer的主对象
        Producer<String, String> producer = new KafkaProducer<>(properties);

        // 消息对象 - ProducerRecoder
        for (int i = 0; i < 100; i++) {
            ProducerRecord<String, String> record =
                    new ProducerRecord<>(TOPIC_NAME, "key-" + i, "value-" + i);

            producer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    if(null==e){
                        System.out.println(
                                "partition : " + recordMetadata.partition() + " , offset : " + recordMetadata.offset());
                    }

                }
            });
        }

        // 所有的通道打开都需要关闭
        producer.close();
    }




    /*  三、
           所谓的同步发送：其实没有同步发送
           Producer异步阻塞发送
        */
    public static void producerSyncSend() throws ExecutionException, InterruptedException {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.205.100:9092,192.168.205.101:9092,192.168.205.102:9092");
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        properties.put(ProducerConfig.RETRIES_CONFIG, "0");
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, "16384");
        properties.put(ProducerConfig.LINGER_MS_CONFIG, "1");
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, "33554432");

        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        // Producer的主对象
        Producer<String, String> producer = new KafkaProducer<>(properties);

        // 消息对象 - ProducerRecoder
        for (int i = 0; i < 10; i++) {
            String key = "key-" + i;
            ProducerRecord<String, String> record =
                    new ProducerRecord<>(TOPIC_NAME, key, "value-" + i);

            //由于 send 方法返回的是一个 Future 对象，根据 Futrue 对象的特点，我们也可以实现同步发送的效果，只需在调用 Future 对象的 get 方发即可。
            // get方法会一直等待下去直至broker将发送结果返回给producer程序。
            // 返回的时候要不返回发送结果，要么抛出异常由producer自行处理。
            // 如果成功，get将返回对应的RecordMetadata实例（包含了已发送消息的所有元数据消息），包含了topic、分区、位移
            Future<RecordMetadata> send = producer.send(record);
            RecordMetadata recordMetadata = send.get();
            System.out.println(key + "partition : " + recordMetadata.partition() + " , offset : " + recordMetadata.offset());
        }

        // 所有的通道打开都需要关闭
        producer.close();
    }






    /*
       四、
        Producer异步发送带回调函数和Partition负载均衡
     */
    public static void producerSendWithCallbackAndPartition() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.205.100:9092,192.168.205.101:9092,192.168.205.102:9092");
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        properties.put(ProducerConfig.RETRIES_CONFIG, "0");
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, "16384");
        properties.put(ProducerConfig.LINGER_MS_CONFIG, "1");
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, "33554432");

        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, "com.dexlace.kafka.producer.partition.SamplePartition");

        // Producer的主对象
        Producer<String, String> producer = new KafkaProducer<>(properties);

        // 消息对象 - ProducerRecoder
        for (int i = 0; i < 100; i++) {
            ProducerRecord<String, String> record =
                    new ProducerRecord<>(TOPIC_NAME, "key-" + i, "value-" + i);

            producer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    System.out.println(
                            "partition : " + recordMetadata.partition() + " , offset : " + recordMetadata.offset());
                }
            });
        }

        // 所有的通道打开都需要关闭
        producer.close();
    }



}


