package com.jasper.mvntest.NoSQL.redis.ha;

import java.util.HashSet;
import java.util.Set;

import redis.clients.jedis.JedisPoolConfig;

public class SomeTest {
    public static void main(String[] args) {
        Set<String> sentinels = new HashSet<String>();
        sentinels.add("172.19.108.127:23679");
        sentinels.add("172.19.108.127:23680");
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(100);
        config.setMaxWaitMillis(50);
        RedisSentinelFactory redisSentinelFactory = new RedisSentinelFactory("mymaster", sentinels, config);
        System.out.println(redisSentinelFactory.getSlavePool());
        System.out.println("====");
//        System.out.println(redisSentinelPool.getSlaveHostAndPort());
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        redisSentinelPool.scheduledService.shutdownNow();
    }

}
