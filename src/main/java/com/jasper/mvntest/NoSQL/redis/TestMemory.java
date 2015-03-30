package com.jasper.mvntest.NoSQL.redis;

import org.junit.Test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

/**
 * jedis内存使用情况，使用的redis内核是2.8.11
 * 调用前，内存使用情况为：526560(514.22K)
 * @author Jasper
 */
public class TestMemory {
    private static final int N = 10000;
    private static final Jedis jedis = new Jedis(RedisConstant.IP, RedisConstant.PORT);
//    private static final Jedis jedis = new Jedis("183.61.2.143", 6383);
    
    public static void main(String[] args) {
//        Jedis jedis = new Jedis(RedisConstant.IP, RedisConstant.PORT);
//        System.out.println(jedis.info("Memory"));
//        System.out.println(jedis.info());
        string_key10_value10();
    }

    /*
     * 1774912(1.69M)
     */
    public static void string_key10_value10() {
        for (int i=0; i<N; i++) {
            jedis.set(String.format("key%07d", i), String.format("key%07d", i));
        }
    }
    
    /*
     * 1775048(1.69M)
     */
    public static void string_key10_value20() {
        for (int i=0; i<N; i++) {
            jedis.set(String.format("key%07d", i), String.format("key%017d", i));
        }
    }
    
    /*
     * 1775184(1.69M)
     */
    public static void string_key20_value10() {
        for (int i=0; i<N; i++) {
            jedis.set(String.format("key%017d", i), String.format("key%07d", i));
        }
    }
    
    /*
     * 1775320(1.69M)
     */
    public static void string_key20_value20() {
        for (int i=0; i<N; i++) {
            jedis.set(String.format("key%017d", i), String.format("key%017d", i));
        }
    }
    
    /*
     * 2096000(2.00M)
     */
    public static void string_key32_value32() {
        for (int i=0; i<N; i++) {
            jedis.set(String.format("key%029d", i), String.format("key%029d", i));
        }
    }
    
    /*
     * 1936136(1.85M)
     */
    public static void string_key10_value32() {
        for (int i=0; i<N; i++) {
            jedis.set(String.format("key%07d", i), String.format("key%029d", i));
        }
    }
    
    /*
     * 1936408(1.85M)
     */
    public static void string_key32_value10() {
        for (int i=0; i<N; i++) {
            jedis.set(String.format("key%029d", i), String.format("key%07d", i));
        }
    }
    
    /*
     * 12756432(12.17M)
     */
    @Test
    public void string_key10_value10_sumx10() {
        for (int i=0; i<N*10; i++) {
            jedis.set(String.format("key%07d", i), String.format("key%07d", i));
        }
    }
    
    /*
     * 2211496(2.11M)
     */
    @Test
    public void string_key10_value10_ttl() {
        for (int i=0; i<N; i++) {
            jedis.setex(String.format("key%07d", i), 1000, String.format("key%07d", i));
        }
    }
    
    /*
     * 1936952(1.85M)
     */
    @Test
    public void hash_key10_field10_value10() {
        for (int i=0; i<N; i++) {
            jedis.hset(String.format("key%07d", i), String.format("field%05d", i), String.format("key%07d", i));
        }
    }
    
    /*
     * 2097088(2.00M)
     */
    @Test
    public void hash_key30_field10_value10() {
        for (int i=0; i<N; i++) {
            jedis.hset(String.format("key%027d", i), String.format("field%05d", i), String.format("key%07d", i));
        }
    }
    
    /*
     * 2097224(2.00M)
     */
    @Test
    public void hash_key10_field30_value10() {
        for (int i=0; i<N; i++) {
            jedis.hset(String.format("key%07d", i), String.format("field%025d", i), String.format("key%07d", i));
        }
    }
    
    /*
     * 2399336(2.29M)
     */
    @Test
    public void hash_key10_3field10_value10() {
        for (int i=0; i<N; i++) {
            for (int j=0; j<3; j++) {
                jedis.hset(String.format("key%07d", i), String.format("field%05d", j), String.format("key%07d", i));
            }
        }
    }
    
    /*
     * 3999064(3.81M)
     */
    @Test
    public void hash_key10_10field10_value10() {
        for (int i=0; i<N; i++) {
            for (int j=0; j<10; j++) {
                jedis.hset(String.format("key%07d", i), String.format("field%05d", j), String.format("key%07d", i));
            }
        }
    }
    
    /*
     * 9119200(8.70M)
     */
    @Test
    public void hash_key10_30field10_value10() {
        for (int i=0; i<N; i++) {
            for (int j=0; j<30; j++) {
                jedis.hset(String.format("key%07d", i), String.format("field%05d", j), String.format("key%07d", i));
            }
        }
    }
    
    /*
     * 1984112(1.89M)
     */
    @Test
    public void hash_key_field10_value10() {
        for (int i=0; i<N; i++) {
            jedis.hset("key", String.format("field%05d", i), String.format("key%07d", i));
        }
    }
    
    /*
     * 2144384(2.05M)
     */
    @Test
    public void hash_key_field10_value30() {
        for (int i=0; i<N; i++) {
            jedis.hset("key", String.format("field%05d", i), String.format("key%027d", i));
        }
    }
    
    /*
     * 2163088(2.06M)
     */
    @Test
    public void hash_key_field30_value10() {
        for (int i=0; i<N; i++) {
            jedis.hset("key", String.format("field%025d", i), String.format("key%07d", i));
        }
    }
    
    /*
     * 14880504(14.19M)
     */
    @Test
    public void hash_key_field10_value10_sumx10() {
        for (int i=0; i<N*10; i++) {
            jedis.hset("key", String.format("field%05d", i), String.format("key%07d", i));
        }
    }
    
    /*
     * 3536544(3.37M)
     */
    @Test
    public void set_key10_value10() {
        for (int i=0; i<N; i++) {
            jedis.sadd(String.format("key%07d", i), String.format("key%07d", i));
        }
    }
    
    /*
     * 3856680(3.68M)
     */
    @Test
    public void set_key50_value10() {
        for (int i=0; i<N; i++) {
            jedis.sadd(String.format("key%047d", i), String.format("key%07d", i));
        }
    }
    
    /*
     * 1503840(1.43M)
     */
    @Test
    public void set_key_value10() {
        for (int i=0; i<N; i++) {
            jedis.sadd("key", String.format("key%07d", i));
        }
    }
    /*
     * 10080232(9.61M)
     */
    @Test
    public void set_key_value10_sumx10() {
        for (long i=0; i<10L*N; i++) {
            jedis.sadd("key", String.format("key%07d", i));
        }
    }
    
    /*
     * 3856816(3.68M)
     */
    @Test
    public void set_key10_value50() {
        for (int i=0; i<N; i++) {
            jedis.sadd(String.format("key%07d", i), String.format("key%047d", i));
        }
    }
    
    /*
     * 1759608(1.68M)
     */
    @Test
    public void zset_key10_member10() {
        for (int i=0; i<N; i++) {
            jedis.zadd(String.format("key%07d", i), i, String.format("member%04d", i));
        }
    }
    
    /*
     * 3039472(2.90M)
     */
    @Test
    public void zset_key10_10member10() {
        for (int i=0; i<N; i++) {
            for (int j=0; j<10; j++) {
                jedis.zadd(String.format("key%07d", i), j, String.format("member%04d", j));
            }
        }
    }
    
    /*
     * 5919744(5.65M)
     */
    @Test
    public void zset_key10_30member10() {
        for (int i=0; i<N; i++) {
            for (int j=0; j<30; j++) {
                jedis.zadd(String.format("key%07d", i), j, String.format("member%04d", j));
            }
        }
    }
    
    /*
     * 2042488(1.95M)
     */
    @Test
    public void zset_1key_10000member() {
        for (int i=0; i<N; i++) {
            jedis.zadd("key", i, String.format("key%07d", i));
        }
    }
    
    /*
     * 14888192(14.20M)
     */
    @Test
    public void zset_1key_100000member() {
        for (int i=0; i<10*N; i++) {
            jedis.zadd("key", i, String.format("key%07d", i));
        }
    }
    
    /*
     * 1757024(1.68M)
     */
    @Test
    public void list_key10_value10() {
        for (int i=0; i<N; i++) {
            jedis.lpush(String.format("key%07d", i), String.format("key%07d", i));
        }
    }
    
    /*
     * 1306368(1.25M)
     */
    @Test
    public void list_key_value10() {
        for (int i=0; i<N; i++) {
            jedis.lpush("key", String.format("key%07d", i));
        }
    }
    
    /*
     * 1467867(1.40M)
     */
    @Test
    public void list_key_value30() {
        for (int i=0; i<N; i++) {
            jedis.lpush("key", String.format("key%027d", i));
        }
    }
    
    /*
     * 8507048(8.11M)
     */
    @Test
    public void list_key_value10_sumx10() {
        for (long i=0; i<10L*N; i++) {
            jedis.lpush("key", String.format("key%07d", i));
        }
    }
    
    /**
     * 测试1kw
     * String   1254724856(1.17G)   value不变     934725800(891.42M)
     * Hash     1481836664(1.38G)
     * Set      1001837168(955.43M)
     * List     800510848(763.43M)
     * zset     1535174936(1.43G)
     */
    @Test
    public void sum_1kw() {
        Pipeline pipeline = jedis.pipelined();
        for (int i=1; i<=10000000; i++) {
//            pipeline.hset("key", String.format("a%09d", i), "99");
//            pipeline.sadd("key", String.format("a%09d", i));
//            pipeline.lpush("key", String.format("a%09d", i));
            pipeline.zadd("key", i, String.format("a%09d", i));
//            pipeline.set(String.format("a%09d", i), String.format("%010d", i));
//            pipeline.set(String.format("a%09d", i), "1234567890");
//            pipeline.set(String.format("a%09d", i), "99");
            if (i % 10000 == 0) {
                pipeline.sync();
                System.out.println("curIndex: " + i);
            }
        }
        System.out.println("end...........");
    }
    
}
