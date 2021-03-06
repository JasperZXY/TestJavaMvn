package com.zxy.demo.db.mongo;

import java.net.UnknownHostException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;


public class MongoTest2 {
    
    static MongoClient mongoClient = null;
    static DBCollection dbCollection = null;
    
    public static void main(String[] args) {
        try {
            mongoClient = new MongoClient("127.0.0.1", 27017);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        DB db = mongoClient.getDB("test");
        dbCollection = db.getCollection("big");
        
        long curTime = System.currentTimeMillis();
        ExecutorService threadPool = Executors.newCachedThreadPool();
        int n = 50;
        final CountDownLatch latch = new CountDownLatch(n);
        
        final AtomicInteger count = new AtomicInteger(-1);
        for (int i = 0; i < n; i++) {
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    while (count.get() < 1_000_00) {
                        int a = count.getAndIncrement();
                        dbCollection.insert(new BasicDBObject("a", a));
                    }
                    latch.countDown();
                }
            });
        }

        threadPool.shutdown();
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(System.currentTimeMillis() - curTime);
        
    }

}
