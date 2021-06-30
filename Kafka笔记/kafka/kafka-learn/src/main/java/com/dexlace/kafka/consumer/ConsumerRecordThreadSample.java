package com.dexlace.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.*;

public class ConsumerRecordThreadSample {
    private final static String TOPIC_NAME = "judy";

    public static void main(String[] args) throws InterruptedException {

        String brokerList = "kafka100:9092,kafka101:9092,kafka102:9092";
        String groupId = "test";
        int workerNum = 5;

        CunsumerExecutor consumers = new CunsumerExecutor(brokerList, groupId, TOPIC_NAME);
        consumers.execute(workerNum);

        Thread.sleep(1000000);

        consumers.shutdown();

    }

    // Consumer处理执行器
    public static class CunsumerExecutor{
        /**
         * 只有一个消费者，执行很多事情
         */
        private final KafkaConsumer<String, String> consumer;
        /**
         * 线程池，一般由Executors工具类去创建对应的执行单元
         */
        private ExecutorService executors;

        /**
         * 初始化一个消费者
         * @param brokerList kafka服务器的broker list
         * @param groupId 消费者组组名
         * @param topic topic名称
         */
        public CunsumerExecutor(String brokerList, String groupId, String topic) {
            Properties props = new Properties();
            props.put("bootstrap.servers", brokerList);
            props.put("group.id", groupId);
            props.put("enable.auto.commit", "true");
            props.put("auto.commit.interval.ms", "1000");
            props.put("session.timeout.ms", "30000");
            props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            consumer = new KafkaConsumer<>(props);
            consumer.subscribe(Arrays.asList(topic));
        }

        /**
         * 执行的逻辑
         * @param workerNum 线程数
         */
        public void execute(int workerNum) {
            /**
             * 核心线程数等于最大线程数，线程空闲时间为0，使用数组阻塞队列，拒绝策略为只用调用者所在线程来运行任务
             */
            executors = new ThreadPoolExecutor(workerNum, workerNum, 0L, TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<>(1000), new ThreadPoolExecutor.CallerRunsPolicy());

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(200);
                for (final ConsumerRecord record : records) {
                    // 使用线程池不断的提交任务
                    executors.submit(new ConsumerRecordWorker(record));
                }
            }
        }

        public void shutdown() {
            if (consumer != null) {
                consumer.close();
            }
            if (executors != null) {
                executors.shutdown();
            }
            try {
                if (!executors.awaitTermination(10, TimeUnit.SECONDS)) {
                    System.out.println("Timeout.... Ignore for this case");
                }
            } catch (InterruptedException ignored) {
                System.out.println("Other thread interrupted this shutdown, ignore for this case.");
                Thread.currentThread().interrupt();
            }
        }


    }

    // 需要执行的逻辑
    // 一个worker对应一个record
    public static class ConsumerRecordWorker implements Runnable {

        private ConsumerRecord<String, String> record;

        public ConsumerRecordWorker(ConsumerRecord record) {
            this.record = record;
        }

        @Override
        public void run() {
            // 假如说数据入库操作
            System.out.println("Thread - "+ Thread.currentThread().getName());
            System.err.printf("patition = %d , offset = %d, key = %s, value = %s%n",
                    record.partition(), record.offset(), record.key(), record.value());
        }

    }
}
