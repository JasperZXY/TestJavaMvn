package com.zxy.demo.db.redis;

import redis.clients.jedis.Jedis;

public class SortedSetOp {
	public static void main(String[] args) {
		@SuppressWarnings("resource")
		Jedis jedis = new Jedis(RedisConstant.IP, RedisConstant.PORT);
		jedis.auth(RedisConstant.PASSWORD);
		jedis.select(1);

		System.out
				.println("======================zset==========================");
		// 清空数据
		System.out.println(jedis.flushDB());

		System.out.println("=============增=============");
		System.out.println("zset中添加元素element001："
				+ jedis.zadd("zset", 7.0, "element001"));
		System.out.println("zset中添加元素element002："
				+ jedis.zadd("zset", 8.0, "element002"));
		System.out.println("zset中添加元素element003："
				+ jedis.zadd("zset", 2.0, "element003"));
		System.out.println("zset中添加元素element004："
				+ jedis.zadd("zset", 3.0, "element004"));
		System.out.println("zset集合中的所有元素：" + jedis.zrange("zset", 0, -1));// 按照权重值排序
		System.out.println();

		System.out.println("=============删=============");
		System.out.println("zset中删除元素element002："
				+ jedis.zrem("zset", "element002"));
		System.out.println("zset集合中的所有元素：" + jedis.zrange("zset", 0, -1));
		System.out.println();

		System.out.println("=============改=============");
		System.out.println();
		System.out.println("zset中修改元素element003 Score+4.5："
				+ jedis.zincrby("zset", 4.5, "element003"));
		System.out.println("zset集合中的所有元素：" + jedis.zrange("zset", 0, -1));
		System.out.println();
		System.out.println("=============查=============");
		System.out.println("统计zset集合中的元素中个数：" + jedis.zcard("zset"));
		System.out.println("统计zset集合中权重某个范围内（1.0——5.0），元素的个数："
				+ jedis.zcount("zset", 1.0, 5.0));
		System.out.println("查看zset集合中element004的权重："
				+ jedis.zscore("zset", "element004"));
		System.out.println("查看下标1到2范围内的元素值：" + jedis.zrange("zset", 1, 2));

	}

}
