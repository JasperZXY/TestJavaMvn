package com.jasper.mvntest.NoSQL;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * 测试new一个对象对资源的消耗及时间
 * @author Jasper
 */
public class Test {
    /**
     * N=500 ：结果是828、786
     * N=1000：结果是1621、1924
     * N=2000：结果是3532、3755
     */
    static final int N = 2000;
    JedisPool jedisPool = null;
    
    
    public Test() {
        jedisPool = new JedisPool("172.19.108.127", 6379);
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        Test test = new Test();
        final String key = "test";
        Jedis jedis = null;
        System.out.println("==========start===========");
        long curTime = 0;
        
        curTime = System.currentTimeMillis();
        for (int i=0; i<N; i++) {
            jedis = test.jedisPool.getResource();
            try {
                jedis.set(key, "test");
            } catch(Exception e) {
                test.jedisPool.returnBrokenResource(jedis);
            } finally {
                test.jedisPool.returnResource(jedis);
                jedis = null;
            }
            jedis = test.jedisPool.getResource();
            try {
                jedis.get(key);
            } catch(Exception e) {
                test.jedisPool.returnBrokenResource(jedis);
                e.printStackTrace();
            } finally {
                test.jedisPool.returnResource(jedis);
                jedis = null;
            }
        }
        System.out.println(System.currentTimeMillis() - curTime);
        
        System.gc();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        curTime = System.currentTimeMillis();
        for (int i=0; i<N; i++) {
            test.execute(new ExecuteRedis() {
                @Override
                public String execute(Jedis jedis) {
                    return jedis.set(key, "test");
                }
            });
            test.execute(new ExecuteRedis() {
                @Override
                public String execute(Jedis jedis) {
                    return jedis.get(key);
                }
            });
        }
        System.out.println(System.currentTimeMillis() - curTime);
        System.out.println("==========end===========");
    }
    
//    @SuppressWarnings("unchecked")
//    public String get(final String key) {
//        return execute(new ExecuteRedis() {
//            @Override
//            public String execute(Jedis jedis) {
//                return jedis.get(key);
//            }
//        });
//    }
    
    public <T> T execute(ExecuteRedis redis) {
        Jedis jedis = jedisPool.getResource();
        try {
            return redis.execute(jedis);
        } catch(Exception e) {
            e.printStackTrace();
            jedisPool.returnBrokenResource(jedis);
        } finally {
            jedisPool.returnResource(jedis);
            jedis = null;
        }
        return null;
    }
    
    interface ExecuteRedis {
        <T> T execute(Jedis jedis);
    }
    
}
