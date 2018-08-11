package com.zxy.demo.guava.cache;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

public class TestCache {
    private final static Cache<String, Object> cache = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(1, TimeUnit.SECONDS)
            .removalListener(new RemovalListener<String, Object>() {
                @Override
                public void onRemoval(RemovalNotification<String, Object> notification) {
                    System.out.println("RemovalListener:" + notification);
                }
            })
            .build();
    
    public static void main(String[] args) {
        cache.put("1", "abc");
        cache.put("1", "abc1");
        System.out.println(cache.getIfPresent("2"));
        Object obj = cache.getIfPresent("1");
        System.out.println(obj);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(obj);
        System.out.println(cache.getIfPresent("1"));
        System.out.println("==============");
        try {
            obj = cache.get("ab", new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    return getDataInDB("abc");
                }
            });
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println(obj);
        System.out.println(cache.getIfPresent("ab"));
        System.out.println(cache.getIfPresent("abc"));
    }
    
    public static Object getDataInDB(String key) {
        System.out.println("getDataInDB");
        return key.length() + ":" + key;
    }

}
