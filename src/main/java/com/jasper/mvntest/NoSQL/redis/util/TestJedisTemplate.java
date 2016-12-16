package com.jasper.mvntest.NoSQL.redis.util;

import redis.clients.jedis.JedisPool;

public class TestJedisTemplate {
    public static void main(String[] args) {
        JedisPool jedisPool = new JedisPool("192.168.20.171", 6379);
        JedisTemplate jedisTemplate = new JedisTemplate(jedisPool);

        System.out.println("ping:" + jedisTemplate.ping());

        System.out.println("get:" + jedisTemplate.get("test"));
    }

}
