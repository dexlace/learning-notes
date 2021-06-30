package com.dexlace.kafka.serialization;

import com.dexlace.kafka.entity.Person;
import com.dexlace.kafka.utils.ProtostuffUtils;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/6/4
 */
public class PersonCustomSerializer implements Serializer<Person> {


    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {


    }

    @Override
    public byte[] serialize(String topic, Person data) {

//        private int id;
//        private String name;
//        private int age;
/******************************************方法一***************************************************************/
//
//        // id name长度 name age
//        byte [] byteName=null;
//        int nameLength=0;
//
//        if (data==null){
//            return null;
//        }
//
//        if (data.getName()!=null){
//            try {
//                byteName=data.getName().getBytes("UTF-8");
//                nameLength=byteName.length;
//            } catch (Exception e) {
//                throw new SerializationException("error when serializing..."+e);
//            }
//        }
//
//
//        //我们要定义一个buffer用来存储id的值，id是int类型，需要4个字节。还要存储name的值的长度用int表示就够了，也是4个字节，
//        // name的值是字符串，长度就有值来决定，
//        // age同理
//        // 所以我们要创建的buffer的大小就是这些字节数加在一起：4+4+name的值的长度
//        ByteBuffer buffer = ByteBuffer.allocate(4+4+nameLength+4);
//        //put()执行完之后，buffer中的position会指向当前存入元素的下一个位置
//        buffer.putInt(data.getId());
//        //由于在读取buffer中的name时需要定义一个字节数组来存储读取出来的数据，但是定义的这个数组的长度无法得到，所以只能在存name的时候也把name的长度存到buffer中
//        buffer.putInt(nameLength);
//        buffer.put(byteName);
//        buffer.putInt(data.getAge());
//        return buffer.array();
        /******************************************方法二：使用工具类************************************************************/

        return ProtostuffUtils.serialize(data);

    }

    @Override
    public void close() {

    }
}
