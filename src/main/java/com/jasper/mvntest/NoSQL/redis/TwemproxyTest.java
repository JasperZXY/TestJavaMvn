package com.jasper.mvntest.NoSQL.redis;

import java.net.InetSocketAddress;
import java.net.Socket;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.util.RedisInputStream;
import redis.clients.util.RedisOutputStream;

public class TwemproxyTest {
    public static void main(String[] args) {
        mm();
        JedisPoolConfig config = new JedisPoolConfig();
        config.setTestOnBorrow(false);
        config.setTestWhileIdle(false);
        JedisPool jedisPool = new JedisPool(config, "172.19.108.127", 6650);
        Jedis jedis = new Jedis("172.19.108.127", 6650);
        try {
            for (int i=0; i<100; i++) {
                jedis.set("foo" + i, Integer.toString(i));
                System.out.println(jedis.get("foo" + i));
            }
        } catch (Exception e) {
//            jedisPool.returnBrokenResource(jedis);
            e.printStackTrace();
        } finally {
//            if (jedis != null) {
//                jedisPool.returnResource(jedis);
//            }
        }
    }
    
    static void mm() {
        try {
       Socket socket = new Socket();
        // ->@wjw_add
        socket.setReuseAddress(true);
        socket.setKeepAlive(true); // Will monitor the TCP connection is
                       // valid
        socket.setTcpNoDelay(true); // Socket buffer Whetherclosed, to
                        // ensure timely delivery of data
        socket.setSoLinger(true, 0); // Control calls close () method,
                         // the underlying socket is closed
                         // immediately
        // <-@wjw_add

        socket.connect(new InetSocketAddress("172.19.108.127", 6650), 1000);
        socket.setSoTimeout(1000);
        RedisOutputStream outputStream = new RedisOutputStream(socket.getOutputStream());
        RedisInputStream inputStream = new RedisInputStream(socket.getInputStream());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    

}
