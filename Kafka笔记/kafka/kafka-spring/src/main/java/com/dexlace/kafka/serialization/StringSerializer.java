//package com.dexlace.kafka.serialization;
//
///**
// * @Author: xiaogongbing
// * @Description:   弄清String序列化器的原理以便定义自己的序列化器
// * String的序列化类是继承了Serializer接口，指定<String>泛型，然后实现的Serializer接口的configure()、serialize()、close()方法。
// * 代码重点的实现是在serialize()，可以看出这个方法将我们传入的String类型的数据，简单的通过data.getBytes()方法进行了序列化。
// *
// * String反序列化就是将字节数组转化为String对象
// * @Date: 2021/6/4
// */
//
//import org.apache.kafka.common.errors.SerializationException;
//import org.apache.kafka.common.serialization.Serializer;
//
//import java.io.UnsupportedEncodingException;
//import java.util.Map;
//
///**
// *  String encoding defaults to UTF8 and can be customized by setting the property key.serializer.encoding,
// *  value.serializer.encoding or serializer.encoding. The first two take precedence over the last.
// */
//public class StringSerializer implements Serializer<String> {
//    private String encoding = "UTF8";
//
//    @Override
//    public void configure(Map<String, ?> configs, boolean isKey) {
//        // 是key需要序列化还是value是要序列化  是则查找key的序列化器
//        String propertyName = isKey ? "key.serializer.encoding" : "value.serializer.encoding";
//        // 找到序列化对象
//        Object encodingValue = configs.get(propertyName);
//        // 找不到，就去获取通用序列化器
//        if (encodingValue == null)
//            encodingValue = configs.get("serializer.encoding");
//        // 判断序列化器是否是String类型
//        if (encodingValue instanceof String)
//            encoding = (String) encodingValue;
//    }
//
//    @Override
//    public byte[] serialize(String topic, String data) {
//        try {
//            if (data == null)
//                return null;
//            else
//                // 将data转化byte数组
//                return data.getBytes(encoding);
//        } catch (UnsupportedEncodingException e) {
//            throw new SerializationException("Error when serializing string to byte[] due to unsupported encoding " + encoding);
//        }
//    }
//
//    @Override
//    public void close() {
//        // nothing to do
//    }
//}
