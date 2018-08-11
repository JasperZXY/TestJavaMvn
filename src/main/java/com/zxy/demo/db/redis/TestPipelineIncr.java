package com.zxy.demo.db.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

public class TestPipelineIncr {
    public static void main(String[] args) {
        JedisPool jedisPool = new JedisPool("172.19.103.92", 6379);
        Jedis jedis = jedisPool.getResource();
        String key = "list_pipeline_incr";
        if(jedis.exists(key)) {
            System.err.println("存在" + key);
        } else {
            Pipeline pipeline = jedis.pipelined();
            pipeline.incr(key);
            pipeline.incr(key);
            pipeline.incr(key);
            pipeline.sync();
            
            System.out.println(jedis.get(key));
            jedis.del(key);
        }
        jedisPool.returnResource(jedis);
    }

}
