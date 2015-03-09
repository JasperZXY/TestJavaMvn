package com.jasper.mvntest.NoSQL.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

/**
 * 订阅接收消息
 * @author Jasper
 */
public class TestPubSub {
    
    @SuppressWarnings("resource")
    public static void main(String[] args) {
        final String channel = "channel";
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                Jedis jedis = new Jedis("183.61.2.143", 10000);
                for (int i=0; i<10; i++) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    jedis.publish(channel, "msg" + i);
                }
            }
        }).start();
        
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                Jedis jedis = new Jedis("183.61.2.143", 10000);
                jedis.subscribe(new JedisPubSub() {
                    
                    @Override
                    public void onUnsubscribe(String channel, int subscribedChannels) {
                        System.out.println("onUnsubscribe:" + channel + "-" + subscribedChannels);
                    }
                    
                    @Override
                    public void onSubscribe(String channel, int subscribedChannels) {
                        System.out.println("onSubscribe:" + channel + "-" + subscribedChannels);
                    }
                    
                    @Override
                    public void onPUnsubscribe(String pattern, int subscribedChannels) {
                        System.out.println("onPUnsubscribe:" + pattern + "-" + subscribedChannels);
                    }
                    
                    @Override
                    public void onPSubscribe(String pattern, int subscribedChannels) {
                        System.out.println("onPSubscribe:" + pattern + "-" + subscribedChannels);
                    }
                    
                    @Override
                    public void onPMessage(String pattern, String channel, String message) {
                        System.out.println("onPMessage:" + message);
                    }
                    
                    @Override
                    public void onMessage(String channel, String message) {
                        System.out.println("onMessage:" + message);
                    }
                }, channel);
            }
        }).start();
        
    }

}
