package com.jasper.mvntest.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Date;
import java.util.Map;

/**
 * 测试结果
 * readValue 可以把一个string的json串转成对象
 * convertValue 可以从一个对象转成另一个对象，如map到object，object到map
 */
public class TestObjectMapper {
    public static void main(String[] args) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        User user = new User();
        user.setId(1L);
        user.setName("Jasper");
        user.setAge(18);
        user.birtyday = new Date();

        String userStr = objectMapper.writeValueAsString(user);

        System.out.println(userStr);

        User userFromString = objectMapper.readValue(userStr, User.class);
//        User userFromString = objectMapper.convertValue(userStr, User.class);   // 这个会报错
        System.out.println(userFromString);

        Map<String, Object> userMap = objectMapper.readValue(userStr, Map.class);
//        Map<String, Object> userMap = objectMapper.convertValue(userStr, Map.class);   // 这个会报错
        System.out.println(userMap);

        System.out.println(objectMapper.convertValue(user, Map.class));

        userMap.remove("id");
        System.out.println(objectMapper.convertValue(userMap, User.class));
        userMap.put("xxx", "我就测试一下加一个属性");
        try {
            System.out.println(objectMapper.convertValue(userMap, User.class));  // 这个会报错
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

class User {
    private Long id;
    private String name;
    private int age;
    public Date birtyday;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", birtyday=" + birtyday +
                '}';
    }
}
