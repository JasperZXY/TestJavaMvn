package com.jasper.mvntest.NoSQL.redis.cluster;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisClusterConnectionHandler;
import redis.clients.jedis.JedisClusterInfoCache;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.util.JedisClusterCRC16;

public class TestRedisCluster {
    static String IP = "172.19.108.127";
    
    public static void main1(String[] args) {
        System.out.println(JedisClusterCRC16.getCRC16("foo231") % 16384);
        System.out.println(JedisClusterCRC16.getCRC16("foo232") % 16384);
        System.out.println(JedisClusterCRC16.getCRC16("foo233") % 16384);
        Jedis jedis = new Jedis(IP, 7004);
        System.out.println(jedis.get("foo232"));
    }
    
    public static void main2(String[] args) {
        Set<HostAndPort> nodeSet = new HashSet<>();
        nodeSet.add(new HostAndPort(IP, 7000));
        nodeSet.add(new HostAndPort(IP, 7001));
        nodeSet.add(new HostAndPort(IP, 7002));
        nodeSet.add(new HostAndPort(IP, 7003));
        nodeSet.add(new HostAndPort(IP, 7004));
        nodeSet.add(new HostAndPort(IP, 7005));
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxWaitMillis(20);
        config.setMaxTotal(16384);
        JedisCluster jedisCluster = new JedisCluster(nodeSet, 10, 2, config);
        try {
            Field handlerField = Class.forName("redis.clients.jedis.JedisCluster").getDeclaredField("connectionHandler");
            handlerField.setAccessible(true);
            JedisClusterConnectionHandler handler = (JedisClusterConnectionHandler) handlerField.get(jedisCluster);
            System.out.println(handler);
            
            Field cacheField = Class.forName("redis.clients.jedis.JedisClusterConnectionHandler").getDeclaredField("cache");
            cacheField.setAccessible(true);
            JedisClusterInfoCache cache = (JedisClusterInfoCache) cacheField.get(handler);
            System.out.println(cache);
            
            Field slotsField = Class.forName("redis.clients.jedis.JedisClusterInfoCache").getDeclaredField("slots");
            slotsField.setAccessible(true);
            Map<Integer, JedisPool> slots = (Map<Integer, JedisPool>) slotsField.get(cache);
            
            Field nodesField = Class.forName("redis.clients.jedis.JedisClusterInfoCache").getDeclaredField("nodes");
            nodesField.setAccessible(true);
            Map<String, JedisPool> nodes = (Map<String, JedisPool>) nodesField.get(cache);
            
            String key = "foo1";
            
            Jedis jedis = new Jedis(IP, 7005);
            System.out.println(jedis.get(key));
            
            
            JedisPool jedisPool = slots.get(JedisClusterCRC16.getCRC16(key) % 16384);
            Field internalPoolField = Class.forName("redis.clients.util.Pool").getDeclaredField("internalPool");
            internalPoolField.setAccessible(true);
            GenericObjectPool<Jedis> internalPool = (GenericObjectPool<Jedis>) internalPoolField.get(jedisPool);
            System.out.println(internalPool);
            
            Field factoryField = Class.forName("org.apache.commons.pool2.impl.GenericObjectPool").getDeclaredField("factory");
            factoryField.setAccessible(true);
            PooledObjectFactory<Jedis> factory = (PooledObjectFactory<Jedis>) factoryField.get(internalPool);
            System.out.println(factory);
            
            System.out.println(jedisPool);
            int success = 0;
            int fail = 0;
            Set<JedisPool> jedisPools = new HashSet<>(slots.values());
            System.out.println(jedisPools.size());
            for (JedisPool pool : jedisPools) {
                try {
                    pool.getResource();
                    success ++;
                } catch(Exception e) {
                    fail ++;
                }
            }
            System.out.println("success=" + success + " fail=" + fail);
            
            System.out.println(nodes.keySet());
            
            jedisPools = new HashSet<>(nodes.values());
            System.out.println(jedisPools.size());
            for (JedisPool pool : jedisPools) {
                try {
                    pool.getResource();
                    success ++;
                } catch(Exception e) {
                    fail ++;
                }
            }
            System.out.println("success=" + success + " fail=" + fail);
            
//            int i=0;
//            while (true) {
//                try {
//                    i ++;
//                    System.out.println("i=" + i);
//                    jedis = jedisPool.getResource();
//                    System.out.println(jedis.get(key));
//                } catch(Exception e) {
////                    e.printStackTrace();
//                }
//            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("resource")
    public static void main(String[] args) {
        Set<HostAndPort> nodes = new HashSet<>();
        nodes.add(new HostAndPort(IP, 7000));
        nodes.add(new HostAndPort(IP, 7001));
        nodes.add(new HostAndPort(IP, 7002));
        nodes.add(new HostAndPort(IP, 7003));
        nodes.add(new HostAndPort(IP, 7004));
        nodes.add(new HostAndPort(IP, 7005));
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxWaitMillis(20);
        config.setMaxTotal(16384);
        JedisCluster jedisCluster = new JedisCluster(nodes, 10, 2, config);
        int i = 0;
        while (true) {
            System.out.println(i);
            try {
                long curTime = System.currentTimeMillis();
                jedisCluster.set("foo" + i, String.valueOf(i));
                System.out.println(jedisCluster.get("foo" + i));
                i ++;
                System.out.println(System.currentTimeMillis() - curTime);
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        jedisCluster.set("hello", "world3");
//        System.out.println(jedisCluster.get("hello"));
    }

}
