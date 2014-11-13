package com.jasper.mvntest.NoSQL.redis.ha;

import redis.clients.jedis.JedisPool;

import com.jasper.mvntest.NoSQL.redis.csbase.RedisClient;

public class HARedisClient extends RedisClient {
    private RedisSentinelFactory redisSentinelFactory;

    public RedisSentinelFactory getRedisSentinelFactory() {
        return redisSentinelFactory;
    }

    public void setRedisSentinelFactory(RedisSentinelFactory redisSentinelFactory) {
        this.redisSentinelFactory = redisSentinelFactory;
    }
    
    /**
     * 从redisMasterPool中随机获取pool
     * @return
     *      Salve的jedis资源池
     */
    public JedisPool getJedisMasterPool() {
        if(redisSentinelFactory == null){
            throw new IllegalArgumentException("Initial a redisClient should first init a RedisClientFactory object," +
                    " but the factory not be null!");
        }
        return redisSentinelFactory.getMasterPool();
    }
    /**
     * 从redisSlavePool中随机获取pool
     * @return
     *      Master的jedis资源池
     */ 
    public JedisPool getJedisSlavePool() {
        if(redisSentinelFactory == null){
            throw new IllegalArgumentException("Initial a redisClient should first init a RedisClientFactory object," +
                    " but the factory not be null!");
        }
        return redisSentinelFactory.getSlavePool();
    }


}
