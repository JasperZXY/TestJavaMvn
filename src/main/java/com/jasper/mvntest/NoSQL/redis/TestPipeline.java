package com.jasper.mvntest.NoSQL.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

public class TestPipeline {
    public static void main(String[] args) {
        JedisPool jedisPool = new JedisPool("172.19.103.92", 6379);
        Jedis jedis = jedisPool.getResource();
        String key = "list_pipeline";
        if(jedis.exists(key)) {
            System.err.println("存在" + key);
        } else {
            Pipeline pipeline = jedis.pipelined();
            pipeline.lpush(key, "1");
            pipeline.lpush(key, "2");
            pipeline.sync();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            pipeline.lpush(key, "4");
            pipeline.lpush(key, "5");
            pipeline.sync();
            
            System.out.println(jedis.lrange(key, 0, -1));
            jedis.del(key);
        }
        jedisPool.returnResource(jedis);
    }

}
