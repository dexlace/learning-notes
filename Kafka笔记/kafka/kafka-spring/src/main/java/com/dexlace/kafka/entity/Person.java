package com.dexlace.kafka.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: xiaogongbing
 * @Description:
 * @Date: 2021/6/4
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Person {

    @Override
    public String toString() {
        return "{" +
                "\"id\":\"" + id + "\""    // "\"id\":\""表达为"id":",整个表达为"id":"id值"
                + "," +
                "\"name\":\"" + name + "\""
                + "," +
                "\"age\":\"" + age + "\""
                + "}";
    }

    private int id;
    private String name;
    private int age;



}
