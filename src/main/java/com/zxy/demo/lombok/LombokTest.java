package com.zxy.demo.lombok;

/**
 * https://projectlombok.org/
 */
public class LombokTest {
    public static void main(String[] args) {
        User user = new User();
        user.setId(8);
        System.out.println(user.getId());
    }
}
