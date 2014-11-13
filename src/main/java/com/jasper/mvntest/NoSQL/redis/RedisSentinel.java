package com.jasper.mvntest.NoSQL.redis;

import java.util.HashSet;
import java.util.Set;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

public class RedisSentinel {
    public static void main(String[] args) {
        Set<String> sentinels = new HashSet<String>();
        sentinels.add("172.19.108.127:23679");
        sentinels.add("172.19.108.127:23680");
        JedisSentinelPool jedisSentinelPool = new JedisSentinelPool("mymaster", sentinels);
        
        Jedis jedis = jedisSentinelPool.getResource();
        while(true) {
            try {
                Thread.sleep(5000);
                jedis = jedisSentinelPool.getResource();
                System.out.println(jedisSentinelPool.getCurrentHostMaster());
                jedis.set("time", System.currentTimeMillis() + "");
                System.out.println(jedis.get("time"));
            } catch (Exception e) {
                e.printStackTrace();
                jedisSentinelPool.returnBrokenResource(jedis);
            } finally {
                jedisSentinelPool.returnResource(jedis);
            }
        }
//        
//        Jedis jedisSentinel1 = new Jedis("172.19.108.127", 23679);
//        System.out.println(jedisSentinel1.sentinelGetMasterAddrByName("mymaster"));
//        Jedis jedisSentinel2 = new Jedis("172.19.108.127", 23680);
//        System.out.println(jedisSentinel2.sentinelGetMasterAddrByName("mymaster"));
        
    }

}
