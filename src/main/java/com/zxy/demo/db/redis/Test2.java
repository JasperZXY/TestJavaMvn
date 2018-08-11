package com.zxy.demo.db.redis;

import java.text.DecimalFormat;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import redis.clients.jedis.Jedis;

public class Test2 {
    public static void main(String[] args) {
        m1();
    }

    public static void m1() {
        final DecimalFormat df = new DecimalFormat("00000000");

        final int n = 100;
        ExecutorService threadPool = Executors.newFixedThreadPool(n);
        final CountDownLatch countDownLatch = new CountDownLatch(n);

        @SuppressWarnings("resource")
        final Jedis jedis = new Jedis("172.19.108.118", 6381);
        System.out.println(jedis.info("memory"));

        for (int j = 0; j < n; j++) {
            final int count = 500000 / n;
            final int startId = j * count;
            threadPool.execute(new Runnable() {
                Jedis jedis_i = new Jedis("172.19.108.118", 6381);

                @Override
                public void run() {
                    for (int i = 0; i < count; i++) {
                        String id = df.format(i + startId);
                        jedis_i.hset("key_" + id, "ID", "1");
                        jedis_i.hset("key_" + id, "FG", "1");
                        jedis_i.hset("key_" + id, "TS", String.valueOf(System.currentTimeMillis()));
                        jedis_i.hset("key_" + id, "CNT", "3");
                    }
                    countDownLatch.countDown();
                }
            });
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("========================");
        System.out.println(jedis.info("memory"));

        threadPool.shutdown();
    }

}
