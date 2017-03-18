package com.jasper.mvntest.protostuff;

import java.util.Arrays;
import java.util.Date;

/**
 * Created by Jasper on 2017/3/5.
 */
public class Test {
    public static void main(String[] args) {
        User user = new User();
        user.setId(7);
        user.setName("张三");
        user.setBirthday(new Date());
        System.out.println(user);

        byte[] bytes = ProtostuffUtil.serializer(user);
        System.out.println(Arrays.toString(bytes));
        System.out.println(bytes.length);

        User userDeserializer = ProtostuffUtil.deserializer(bytes, User.class);
        System.out.println(userDeserializer);
    }
}
