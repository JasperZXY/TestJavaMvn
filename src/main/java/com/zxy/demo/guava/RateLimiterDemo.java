package com.zxy.demo.guava;

import com.google.common.util.concurrent.RateLimiter;

import java.util.Date;

/**
 * 中文翻译文档 http://ifeve.com/guava-ratelimiter/
 *
 * 原理图 令牌桶的流控算法：http://ifeve.com/wp-content/uploads/2015/05/token_bucket.jpg
 * https://images2018.cnblogs.com/blog/874963/201807/874963-20180727184559491-371099708.png
 *
 * 用于高可用服务的限流
 */
public class RateLimiterDemo {
    private static final RateLimiter rateLimiter = RateLimiter.create(2);   // 1秒2个令牌

    public static void main(String[] args) {
        System.out.println("current rate : " + rateLimiter.getRate());

        new Thread(() -> {
            for (int i=0; i<20; i++) {
                if (rateLimiter.tryAcquire()) {
                    System.out.println(new Date() + ": get token to do something");
                }else {
                    System.out.println(new Date() + ": limit !!!");
                }

                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
