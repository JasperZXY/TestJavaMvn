package com.jasper.mvntest.NoSQL.redis.ha;

import java.util.HashSet;
import java.util.Set;

import com.jasper.mvntest.NoSQL.redis.ha.HARedisClient.ExecuteRedis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class SomeTest {

    static int i = 2518;
    
    public static void main1(String[] args) {
        Set<String> sentinels = new HashSet<String>();
        sentinels.add("172.19.108.127:26500");
        sentinels.add("172.19.108.127:26510");
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(100);
        config.setMaxWaitMillis(50);
        RedisSentinelFactory redisSentinelFactory = new RedisSentinelFactory("master_1", sentinels, config);
        System.out.println(redisSentinelFactory.getSlavePool());
        System.out.println("====");
        JedisPool masterJedisPool = null;
        JedisPool slaveJedisPool = null;
        Jedis masterJedis = null;
        Jedis slaveJedis = null;
        
        while (true) {
            try {
                Thread.sleep(2000);
                System.out.println(i);
                masterJedisPool = redisSentinelFactory.getMasterPool();
                slaveJedisPool = redisSentinelFactory.getSlavePool();
                masterJedis = masterJedisPool.getResource();
                masterJedis.set("foo" + i, Integer.toString(i));
                slaveJedis = slaveJedisPool.getResource();
                System.out.println(slaveJedis.get("foo" + i));
                i ++;
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    if (masterJedisPool != null) {
                        masterJedisPool.returnBrokenResource(masterJedis);
                    }
                    if (slaveJedisPool != null) {
                        slaveJedisPool.returnBrokenResource(slaveJedis);
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            } finally {
                try {
                    if (masterJedisPool != null) {
                        masterJedisPool.returnResource(masterJedis);
                    }
                    if (slaveJedisPool != null) {
                        slaveJedisPool.returnResource(slaveJedis);
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
//        System.out.println(redisSentinelPool.getSlaveHostAndPort());
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        redisSentinelPool.scheduledService.shutdownNow();
    }
    
    public static void main(String[] args) {
        Set<String> sentinels = new HashSet<String>();
        sentinels.add("172.19.108.127:26500");
        sentinels.add("172.19.108.127:26510");
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(100);
        config.setMaxWaitMillis(50);
        RedisSentinelFactory redisSentinelFactory = new RedisSentinelFactory("master_1", sentinels, config);
        
        HARedisClient client = new HARedisClient();
        client.setRedisSentinelFactory(redisSentinelFactory);
        
        String ret;
        while (true) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("i->" + i);
            ret = client.executeWrite(new ExecuteRedis() {
                @Override
                public  String execute(Jedis jedis) {
                    return jedis.set("foo" + i, Integer.toString(i));
                }
            }, false);
            System.out.println("set->" + ret);
            ret = client.executeRead(new ExecuteRedis() {
                @Override
                public String execute(Jedis jedis) {
                    return jedis.get("foo" + i);
                }
            }, false);
            System.out.println("get->" + ret);
            i ++;
            
            System.out.println("i->" + i);
            try {
                ret = client.executeWrite(new ExecuteRedis() {
                    @Override
                    public  String execute(Jedis jedis) {
                        return jedis.set("foo" + i, Integer.toString(i));
                    }
                }, true);
                System.out.println("true set->" + ret);
            } catch (Exception e) {
                System.out.println("error set :i->" + i);
            }
            try {
                ret = client.executeRead(new ExecuteRedis() {
                    @Override
                    public String execute(Jedis jedis) {
                        return jedis.get("foo" + i);
                    }
                }, false);
                System.out.println("true get->" + ret);
            } catch(Exception e) {
                System.out.println("error get : i->" + i);
            }
            i ++;
        }
    }

}
