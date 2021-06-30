package com.dexlace.kafka.serialization;

import com.dexlace.kafka.entity.Person;
import com.dexlace.kafka.utils.ProtostuffUtils;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/6/4
 */
public class PersonCustomDeserializer implements Deserializer<Person> {


    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {



    }

    @Override
    public Person deserialize(String topic, byte[] data) {
//        int id;
//        int nameLength;
//        String name;
//        int age;
//        try{
//            if(data == null){
//                return null;
//            }
//            if(data.length < 12){
//                throw new SerializationException("Size of data received by IntegerDeserializer is shorter than expected...");
//            }
//            ByteBuffer buffer = ByteBuffer.wrap(data);//wrap可以把字节数组包装成缓冲区ByteBuffer
//            //get()从buffer中取出数据，每次取完之后position指向当前取出的元素的下一位，可以理解为按顺序依次读取
//            id = buffer.getInt();
//            nameLength = buffer.getInt();
//            /*
//             * 定义一个字节数组来存储字节数组类型的name，因为在读取字节数组数据的时候需要定义一个与读取的数组长度一致的数组，要想知道每个name的值的长度，就需要把这个长度存到buffer中，这样在读取的时候可以得到数组的长度方便定义数组
//             */
//            byte[] nameBytes = new byte[nameLength];
//            buffer.get(nameBytes);
//            name = new String(nameBytes, "UTF-8");
//            age=buffer.getInt();
//
//
//            return new Person(id,name, age);
//        }catch(Exception e){
//            throw new SerializationException("error when deserializing..."+e);
//        }
/**************************************************工具类**********************************************************/

       return ProtostuffUtils.deserialize(data,Person.class);


    }

    @Override
    public void close() {

    }
}
