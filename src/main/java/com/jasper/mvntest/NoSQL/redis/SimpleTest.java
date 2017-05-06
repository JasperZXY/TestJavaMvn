package com.jasper.mvntest.NoSQL.redis;

import redis.clients.jedis.Jedis;

public class SimpleTest {
	public static void main(String[] args) {
		@SuppressWarnings("resource")
		Jedis jedis = new Jedis(RedisConstant.IP, RedisConstant.PORT);
		jedis.auth(RedisConstant.PASSWORD);
//		jedis.select(0);   //默认就是选择数据库0
		System.out.println(jedis.hget("car", "name"));
	}

}
