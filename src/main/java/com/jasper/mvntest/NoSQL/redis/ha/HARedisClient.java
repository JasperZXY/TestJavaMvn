package com.jasper.mvntest.NoSQL.redis.ha;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import com.jasper.mvntest.NoSQL.redis.csbase.RedisClient;

public class HARedisClient extends RedisClient {
    private RedisSentinelFactory redisSentinelFactory;
    private int retries = 3;

    public RedisSentinelFactory getRedisSentinelFactory() {
        return redisSentinelFactory;
    }

    public void setRedisSentinelFactory(RedisSentinelFactory redisSentinelFactory) {
        this.redisSentinelFactory = redisSentinelFactory;
    }
    
    /**
     * 
     * @param read
     * @param isThrowException 是否出错抛异常，true则抛异常，false则返回null
     * @return
     */
    public <T> T executeRead(ExecuteRedis read, boolean isThrowException) {
        JedisPool jedisPool = redisSentinelFactory.getSlavePool();
        Jedis jedis = null;
        T ret = null;
        JedisConnectionException exception = null;
        try {
            jedis = jedisPool.getResource();
            ret = read.execute(jedis);
            return ret;
        } catch(JedisConnectionException connectionException) {
            returnBrokenResource(jedisPool, jedis);
            try {
                ret = executeReadByMaster(read, isThrowException);
            } catch(JedisConnectionException exception2) {
                exception = exception2;
            }
        } finally {
            returnResource(jedisPool, jedis);
        }
        if (isThrowException && exception != null) {
            throw exception;
        }
        return ret;
    }
    
    private <T> T executeReadByMaster(ExecuteRedis read, boolean isThrowException) {
        JedisPool jedisPool = redisSentinelFactory.getMasterPool();
        Jedis jedis = null;
        T ret = null;
        JedisConnectionException exception = null;
        try {
            jedis = jedisPool.getResource();
            ret = read.execute(jedis);
            return ret;
        } catch(JedisConnectionException connectionException) {
            returnBrokenResource(jedisPool, jedis);
            exception = connectionException;
        } finally {
            returnResource(jedisPool, jedis);
        }
        if (isThrowException) {
            throw exception;
        }
        return null;
    }
    
    /**
     * @param write
     * @param isThrowException 是否出错抛异常，true则抛异常，false则返回null
     * @return
     */
    public <T> T executeWrite(ExecuteRedis write, boolean isThrowException) {
        JedisPool jedisPool = redisSentinelFactory.getMasterPool();
        Jedis jedis = null;
        T ret = null;
        JedisConnectionException exception = null;
        for (int i=0; i<retries; i++) {
            try {
                jedis = jedisPool.getResource();
                ret = write.execute(jedis);
                return ret;
            } catch(JedisConnectionException connectionException) {
                returnBrokenResource(jedisPool, jedis);
                exception = connectionException;
            } finally {
                returnResource(jedisPool, jedis);
            }
        }
        if (isThrowException) {
            throw exception;
        }
        return null;
    }
    
    private void returnBrokenResource(JedisPool jedisPool, Jedis jedis) {
        try {
            if (jedisPool != null) {
                jedisPool.returnBrokenResource(jedis);
            }
        } catch(Exception e) {
        }
    }
    
    private void returnResource(JedisPool jedisPool, Jedis jedis) {
        try {
            if (jedisPool != null) {
                jedisPool.returnResource(jedis);
            }
        } catch(Exception e) {
        }
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
    
    interface ExecuteRedis {
        <T> T execute(Jedis jedis);
    }
    

}
