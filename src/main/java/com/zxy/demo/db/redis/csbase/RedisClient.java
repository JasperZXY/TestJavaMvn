package com.zxy.demo.db.redis.csbase;

import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Transaction;

/**
 * 基于jedis封装了,redis的一些常用工具类。
 *
 */
public class RedisClient {
	
 
	private RedisClientFactory factory;
	
	public RedisClientFactory getFactory() {
		return factory;
	}

	public void setFactory(RedisClientFactory factory) {
		this.factory = factory;
	}
	/**
	 * redis客户端构造器
	 * @param factory
	 * 		redis工厂类 {@link RedisClientFactory}
	 */
	public RedisClient(RedisClientFactory factory) {
		super();
		this.factory = factory;
	}
	
	public RedisClient() {
		
	}

	/**
	 * 从redisMasterPool中随机获取pool
	 * @return
	 * 		Salve的jedis资源池
	 */
	public JedisPool getJedisMasterPool() {
		if(factory == null){
			throw new IllegalArgumentException("Initial a redisClient should first init a RedisClientFactory object," +
					" but the factory not be null!");
		}
		return factory.getMasterPool();
	}
	/**
	 * 从redisSlavePool中随机获取pool
	 * @return
	 * 		Master的jedis资源池
	 */	
	public JedisPool getJedisSlavePool() {
		if(factory == null){
			throw new IllegalArgumentException("Initial a redisClient should first init a RedisClientFactory object," +
					" but the factory not be null!");
		}
		return factory.getSlavePool();
	}
	
	/**
	 * 执行set操作，然后释放client连接
	 * </br>如果要多次操作，请使用原生的Jedis, 可以使用 getJedisMasterPool  getJedisSlavePool 获取pool后，再获取redis连接
	 * </br>并在调用完成后，需调用pool的returnResource方法释放该连接
	 * @param dbIndex  
	 * 		redis db index
	 * @param key
	 * 		set的key值
	 * @param value
	 * 		key对应的value值
	 * @return
	 * 		返回被设置的值 String类型
	 */
	public String setAndReturn(int dbIndex, final String key, String value){
		Jedis jedis = null;
		JedisPool jedisPool = null;
		try{
			jedisPool = getJedisMasterPool();
			jedis = jedisPool.getResource();
			
			//如果为0,则不需通信表明select db0
			if(dbIndex != 0){
				jedis.select(dbIndex);
			}
			return jedis.set(key, value);
		}catch(Exception e){
			jedisPool.returnBrokenResource(jedis);
			jedis = null;
			factory.init();
			throw new CsRedisRuntimeException("jedis set fail", e);
		}finally{
		    if(jedis != null){
		        jedisPool.returnResource(jedis);
		    }
		}
	}
	
	/**
	 * 执行set操作，然后释放client连接
	 * </br>如果要多次操作，请使用原生的Jedis, 可以使用 getJedisMasterPool  getJedisSlavePool 获取pool后，再获取redis连接
	 * </br>并在调用完成后，需调用pool的returnResource方法释放该连接
	 * @param key
	 * 		set的key值
	 * @param value
	 * 		key对应的value值
	 * @return
	 * 		设置成功则返回被设置值value
	 */	
	public String setAndReturn(final String key, String value){
		return setAndReturn(0, key, value);
	}
	
	/**
	 * 执行set操作，然后释放client连接
	 * </br>如果要多次操作，请使用原生的Jedis, 可以使用 getJedisMasterPool  getJedisSlavePool 获取pool后，再获取redis连接
	 * </br>并在调用完成后，需调用pool的returnResource方法释放该连接
	  * @param key
	 * 		set的key值
	 * @param value
	 * 		key对应的value值
	 * @param dbIndex 
	 * 		redis db index
	 * @param seconds 
			有效时间
	 * @return
	 * 		设置成功则返回被设置值value
	 */
	public String setAndReturn(int dbIndex, final String key, String value, int seconds){
		Jedis jedis = null;
		JedisPool jedisPool = null;
		try{
			jedisPool = getJedisMasterPool();
			jedis = jedisPool.getResource();

			//如果为0,则不需通信表明select db0
			if(dbIndex != 0){
				jedis.select(dbIndex);
			}
			return jedis.setex(key, seconds, value);
		}catch(Exception e){
			jedisPool.returnBrokenResource(jedis);
			jedis = null;
			factory.init();
			throw new CsRedisRuntimeException("jedis set fail", e);
		}finally{
		    if(jedis != null){
                jedisPool.returnResource(jedis);
            }
		}
	}
	
	/**
	 * 执行set操作，然后释放client连接
	 * </br>如果要多次操作，请使用原生的Jedis, 可以使用 getJedisMasterPool  getJedisSlavePool 获取pool后，再获取redis连接
	 * </br>并在调用完成后，需调用pool的returnResource方法释放该连接
	  * @param key
	 * 		set的key值
	 * @param value
	 * 		key对应的value值
	 * @param seconds  
	 * 		有效时间
	 * @return
	 * 		设置成功则返回被设置值value
	 */
	public String setAndReturn(final String key, String value, int seconds){
		return setAndReturn(0, key, value, seconds);
	}
	
	/**
	 * 执行set操作，然后释放client连接
	 * </br>如果要多次操作，请使用原生的Jedis, 可以使用 getJedisMasterPool  getJedisSlavePool 获取pool后，再获取redis连接
	 * </br>并在调用完成后，需调用pool的returnResource方法释放该连接
	  * @param key
	 * 		set的key值
	 * @param value
	 * 		key对应的value值
	 * @param dbIndex 
	 * 		redis db index
	 * @param seconds  
	 * 		有效时间
	 * @return
	 * 		设置成功则返回被设置值value
	 */
	public String setAndReturn(int dbIndex, final byte[] key, byte[] value, int seconds){
		Jedis jedis = null;
		JedisPool jedisPool = null;
		try{
		    jedisPool = getJedisMasterPool();
			jedis = jedisPool.getResource();

			if(dbIndex != 0){
				jedis.select(dbIndex);
			}
			return jedis.setex(key, seconds, value);
		}catch(Exception e){
			jedisPool.returnBrokenResource(jedis);
			jedis = null;
			factory.init();
			throw new CsRedisRuntimeException("jedis set fail", e);
		}finally{
		    if(jedis != null){
                jedisPool.returnResource(jedis);
            }
		}
	}
	
	/**
	 * 执行set操作，然后释放client连接
	 * </br>如果要多次操作，请使用原生的Jedis, 可以使用 getJedisMasterPool  getJedisSlavePool 获取pool后，再获取redis连接
	 * </br>并在调用完成后，需调用pool的returnResource方法释放该连接
	 * @param key
	 * 		set的key值
	 * @param value
	 * 		key对应的value值
	 * @param seconds  
	 * 		有效时间
	 * @return
	 * 		设置成功则返回被设置值value
	 */
	public String setAndReturn(final byte[] key, byte[] value, int seconds){
		return setAndReturn(0, key, value, seconds);
	}
	
	/**
	 * 获取info信息
	 * @return 
	 * 		String类型，当前连接服务器的服务器信息
	 */
	public String infoAndReturn(){
		Jedis jedis = null;
		JedisPool jedisPool = null;
		try{
			jedisPool = getJedisMasterPool();
			jedis = jedisPool.getResource();
			
			return jedis.info();
		}catch(Exception e){
			jedisPool.returnBrokenResource(jedis);
			jedis = null;
			factory.init();
			throw new CsRedisRuntimeException("jedis info fail", e);
		}finally{
		    if(jedis != null){
                jedisPool.returnResource(jedis);
            }
		}
	}
	
	/**
	 * 执行get操作，然后释放client连接
	 * </br>如果要多次操作，请使用原生的Jedis, 可以使用 getJedisMasterPool  getJedisSlavePool 获取pool后，再获取redis连接
	 * </br>并在调用完成后，需调用pool的returnResource方法释放该连接
	 * @param dbIndex
	 * 		db的索引值
	 * @param key
	 * 		set的key值
	 * @return
	 * 		返回key对应的value值
	 */
	public String getAndReturn(int dbIndex, final String key){
		Jedis jedis = null;
		JedisPool jedisPool = null;
		try{
			jedisPool = getJedisSlavePool();
			jedis = jedisPool.getResource();
			
			if(dbIndex != 0){
				jedis.select(dbIndex);
			}
			return jedis.get(key);
		}catch(Exception e){
			jedisPool.returnBrokenResource(jedis);
			jedis = null;
			throw new CsRedisRuntimeException("jedis get fail", e);
		}finally{
		    if(jedis != null){
                jedisPool.returnResource(jedis);
            }
		}
	}
	
	/**
	 * 执行get操作，然后释放client连接
	 * </br>如果要多次操作，请使用原生的Jedis, 可以使用 getJedisMasterPool  getJedisSlavePool 获取pool后，再获取redis连接
	 * </br>并在调用完成后，需调用pool的returnResource方法释放该连接
	 * @param key
	 * 		set的key值
	 * @return
	 * 		返回key对应的value值
	 */
	public String getAndReturn(final String key){
		return getAndReturn(0, key);
	}
	
	/**
	 * 执行set操作，然后释放client连接
	 * </br>如果要多次操作，请使用原生的Jedis, 可以使用 getJedisMasterPool  getJedisSlavePool 获取pool后，再获取redis连接
	 * </br>并在调用完成后，需调用pool的returnResource方法释放该连接
	 * @param dbIndex 
	 * 		redis db index
	 * @param key
	 * 		set的key值
	 * @param value
	 * 		key对应的value值
	 * @return
	 * 		String类型, 返回key对应的value值
	 */
	public String setAndReturn(int dbIndex, final byte[] key, byte[] value){
		Jedis jedis = null;
		JedisPool jedisPool = null;
		try{
			jedisPool = getJedisMasterPool();
			jedis = jedisPool.getResource();
			
			if(dbIndex != 0){
				jedis.select(dbIndex);
			}
			return jedis.set(key, value);
		}catch(Exception e){
			jedisPool.returnBrokenResource(jedis);
			jedis = null;
			factory.init();
			throw new CsRedisRuntimeException("jedis set fail", e);
		}finally{
		    if(jedis != null){
                jedisPool.returnResource(jedis);
            }
		}
	}
	
	/**
	 * 执行set操作，然后释放client连接
	 * </br>如果要多次操作，请使用原生的Jedis, 可以使用 getJedisMasterPool  getJedisSlavePool 获取pool后，再获取redis连接
	 * </br>并在调用完成后，需调用pool的returnResource方法释放该连接
	  * @param key
	 * 		set的key值
	 * @param value
	 * 		key对应的value值
	 * @return
	 * 	    返回key对应的value值
	 */
	public String setAndReturn(final byte[] key, byte[] value){
		return setAndReturn(0, key, value);
	}
	
	/**
	 * 执行get操作，然后释放client连接
	 * </br>如果要多次操作，请使用原生的Jedis, 可以使用 getJedisMasterPool  getJedisSlavePool 获取pool后，再获取redis连接
	 * </br>并在调用完成后，需调用pool的returnResource方法释放该连接
	 * @param dbIndex
	 * 		db的索引值
	 * @param key
	 * 		set的key值
	 * @return
	 * 		返回key对应的value值,字节数组类型
	 */
	public byte[] getAndReturn(int dbIndex, final byte[] key){
		Jedis jedis = null;
		JedisPool jedisPool = null;
		try{
			jedisPool = getJedisSlavePool();
			jedis = jedisPool.getResource();
			
			if(dbIndex != 0){
				jedis.select(dbIndex);
			}
			return jedis.get(key);
		}catch(Exception e){
			jedisPool.returnBrokenResource(jedis);
			jedis = null;
			throw new CsRedisRuntimeException("jedis get fail", e);
		}finally{
		    if(jedis != null){
                jedisPool.returnResource(jedis);
            }
		}
	}
	
	/**
	 * 执行get操作，然后释放client连接
	 * </br>如果要多次操作，请使用原生的Jedis, 可以使用 getJedisMasterPool  getJedisSlavePool 获取pool后，再获取redis连接
	 * </br>并在调用完成后，需调用pool的returnResource方法释放该连接
	 * @param key
	 * 		set的key值
	 * @return
	 * 	    返回key对应的value值,字节数组类型
	 */
	public byte[] getAndReturn(final byte[] key){
		return getAndReturn(0,key);
	}
	
	
	
	
	/**
	 * 执行mset操作，然后释放client连接
	 * </br>如果要多次操作，请使用原生的Jedis, 可以使用 getJedisMasterPool  getJedisSlavePool 获取pool后，再获取redis连接
	 * </br>并在调用完成后，需调用pool的returnResource方法释放该连接
	 *@param dbIndex
	 *		db的索引值
	 * @param keysvalues
	 * 		set的key值
	 * @return  Status code reply Basically +OK as MSET can't fail
	 */
	public String msetAndReturn(int dbIndex, String... keysvalues){
		Jedis jedis = null;
		JedisPool jedisPool = null;
		try{
			jedisPool = getJedisMasterPool();
			jedis = jedisPool.getResource();
			
			if(dbIndex != 0){
				jedis.select(dbIndex);
			}
			return jedis.mset(keysvalues);
		}catch(Exception e){
			jedisPool.returnBrokenResource(jedis);
			jedis = null;
			factory.init();
			throw new CsRedisRuntimeException(e.getMessage(), e);
		}finally{
		    if(jedis != null){
                jedisPool.returnResource(jedis);
            }
		}
	}
	
	/**
	 * 执行mset操作，然后释放client连接
	 * </br>如果要多次操作，请使用原生的Jedis, 可以使用 getJedisMasterPool  getJedisSlavePool 获取pool后，再获取redis连接
	 * </br>并在调用完成后，需调用pool的returnResource方法释放该连接
	 * @param keysvalues
	 * 		set的key值
	 * @return  
	 * 		Status code reply Basically +OK as MSET can't fail
	 */
	public String msetAndReturn(String... keysvalues){
		return msetAndReturn(0, keysvalues);
	}
	
	/**
	 * 执行mget操作，然后释放client连接
	 * </br>如果要多次操作，请使用原生的Jedis, 可以使用 getJedisMasterPool  getJedisSlavePool 获取pool后，再获取redis连接
	 * </br>并在调用完成后，需调用pool的returnResource方法释放该连接
	 * @param dbIndex
	 * 		db的索引值
	 * @param keys
	 * 		set的keys,可以获取多个key的value值
	 * @return  
	 * 		根据keys获取的values,返回值为String型的List
	 */
	public List<String> mgetAndReturn(int dbIndex, String... keys){
		Jedis jedis = null;
		JedisPool jedisPool = null;
		try{
			  jedisPool = getJedisSlavePool();
			  jedis = jedisPool.getResource();
			
			if(dbIndex != 0){
				jedis.select(dbIndex);
			}
			return jedis.mget(keys);
		}catch(Exception e){
			jedisPool.returnBrokenResource(jedis);
			jedis = null;
			throw new CsRedisRuntimeException("jedis mget fail", e);
		}finally{
		    if(jedis != null){
                jedisPool.returnResource(jedis);
            }
		}
	}
	
	
	
	/**
	 * 执行mset操作，然后释放client连接
	 * </br>如果要多次操作，请使用原生的Jedis, 可以使用 getJedisMasterPool  getJedisSlavePool 获取pool后，再获取redis连接
	 * </br>并在调用完成后，需调用pool的returnResource方法释放该连接
	 * @param keys
	 * 		set的keys,可以获取多个key的value值
	 * @return  
	 * 		根据keys获取的values,返回值为String型的List
	 */
	public List<String> mgetAndReturn(String... keys){
		return mgetAndReturn(0, keys);
	}
	
	
	/**
     * 执行mset操作，然后释放client连接
     * </br>如果要多次操作，请使用原生的Jedis, 可以使用 getJedisMasterPool  getJedisSlavePool 获取pool后，再获取redis连接
     * </br>并在调用完成后，需调用pool的returnResource方法释放该连接
     * @param dbIndex
     * 		db的索引值
     * @param keysvalues
     * 		set的keys,可以获取多个key的value值
     * @return  
     * 		Status code reply Basically +OK as MSET can't fail
     */
    public String msetAndReturn(int dbIndex, byte[]... keysvalues){
        Jedis jedis = null;
        JedisPool jedisPool = null;
        try{
            jedisPool = getJedisMasterPool();
            jedis = jedisPool.getResource();
        	
            if(dbIndex != 0){
                jedis.select(dbIndex);
            }
            return jedis.mset(keysvalues);
        }catch(Exception e){
            jedisPool.returnBrokenResource(jedis);
            jedis = null;
            factory.init();
            throw new CsRedisRuntimeException("jedis mset fail", e);
        }finally{
            if(jedis != null){
                jedisPool.returnResource(jedis);
            }
        }
    }
    
    /**
     * 执行mset操作，然后释放client连接
     * </br>如果要多次操作，请使用原生的Jedis, 可以使用 getJedisMasterPool  getJedisSlavePool 获取pool后，再获取redis连接
     * </br>并在调用完成后，需调用pool的returnResource方法释放该连接
     * @param keysvalues
     * 		set的keys,可以获取多个key的value值
     * @return  
     * 		Status code reply Basically +OK as MSET can't fail
     */
    public String msetAndReturn(byte[]... keysvalues){
        return msetAndReturn(0, keysvalues);
    }
    
    /**
     * 执行mget操作，然后释放client连接
     * </br>如果要多次操作，请使用原生的Jedis, 可以使用 getJedisMasterPool  getJedisSlavePool 获取pool后，再获取redis连接
     * </br>并在调用完成后，需调用pool的returnResource方法释放该连接
     *@param dbIndex
     *		db的索引值
     * @param keys
     * 		set的keys,可以获取多个key的value值
     * @return  
     * 		根据keys获取的values,返回值为byte型的List
     */
    public List<byte[]> mgetAndReturn(int dbIndex, byte[]... keys){
        Jedis jedis = null;
        JedisPool jedisPool = null;
        try{
            jedisPool = getJedisMasterPool();
            jedis = jedisPool.getResource();
            
            if(dbIndex != 0){
                jedis.select(dbIndex);
            }
            return jedis.mget(keys);
        }catch(Exception e){
            jedisPool.returnBrokenResource(jedis);
            jedis = null;
            factory.init();
            throw new CsRedisRuntimeException("jedis mget fail", e);
        }finally{
            if(jedis != null){
                jedisPool.returnResource(jedis);
            }
        }
    }
    
    
    
    /**
     * 执行mset操作，然后释放client连接
     * </br>如果要多次操作，请使用原生的Jedis, 可以使用 getJedisMasterPool  getJedisSlavePool 获取pool后，再获取redis连接
     * </br>并在调用完成后，需调用pool的returnResource方法释放该连接
     * @param keys
     * 		set的keys,可以获取多个key的value值
     * @return  
     * 		根据keys获取的values,返回值为byte型的List
     */
    public List<byte[]> mgetAndReturn(byte[]... keys){
        return mgetAndReturn(0, keys);
    }
    
	/**
	 * 执行smembers操作，然后释放client连接
	 * 返回集合 key 中的所有成员。不存在的 key 被视为空集合
	 * @param key 
	 * 		set的key值
	 * @return 
	 * 		默认db下,当前key对应的所有成员
	 */
    public Set<String> smembers(String key) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        try {
            jedisPool = getJedisSlavePool();
            jedis = jedisPool.getResource();

            return jedis.smembers(key);
        } catch (Exception e) {
            jedisPool.returnBrokenResource(jedis);
            jedis = null;
            factory.init();
            throw new CsRedisRuntimeException("jedis get fail", e);
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }

    /**
     * redis SADD 操作，向名为key的set中添加一个或者多个value
     * @param key 
     *      假如 key不存在，则创建一个只包含 member 元素作成员的集合。当 key 不是集合类型时，返回一个错误。
     * @param values
     * 		要存入的value
     * @return 
     * 		被添加到集合中的新元素的数量，不包括被忽略的元素。
     */
    public Long sadd(String key, String... values) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        try {
            jedisPool = getJedisMasterPool();
            jedis = jedisPool.getResource();
        	
            return jedis.sadd(key, values);
        } catch (Exception e) {
            jedisPool.returnBrokenResource(jedis);
            jedis = null;
            factory.init();
            throw new CsRedisRuntimeException("jedis set fail", e);
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }
    
    /**
     * redis的 SREM 操作，移除set(名称为key)中的一个或多个value
     * @param key
     * 		set的key值
     * @param values
     * 		要被删除的value值
     * @return
     * 		被删除的元素的个数
     */
    public Long srem(String key, String... values) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        try {
            jedisPool = getJedisMasterPool();
            jedis = jedisPool.getResource();

            return jedis.srem(key, values);
        } catch (Exception e) {
            jedisPool.returnBrokenResource(jedis);
            jedis = null;
            factory.init();
            throw new CsRedisRuntimeException("jedis set fail", e);
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }

    /**
     * Redis 的SCARD 操作，返回当前set中的value个数。
     * 集合的基数。
	 * 当 key 不存在时，返回 0 
     * @param key 
     * 		set的key值
     * @return Long 
     * 		set中这个key对应的value的个数
     */
    public Long scard(String key) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        try {
            jedisPool = getJedisSlavePool();
            jedis = jedisPool.getResource();

            return jedis.scard(key);
        } catch (Exception e) {
            jedisPool.returnBrokenResource(jedis);
            jedis = null;
            throw new CsRedisRuntimeException("jedis get fail", e);
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }
    /**
     * SISMEMBER key member 判断 member 元素是否集合 key 的成员。
     * 如果 member 元素是集合的成员，返回 1 。
     * 如果 member 元素不是集合的成员，或 key 不存在，返回 0 。
     * @param key
     * 		set的key值
     * @param value
     * 		被判断的字符串value
     * @return
     * 		boolean值
     */
    public Boolean sismember(String key, String value) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        try {
            jedisPool = getJedisSlavePool();
            jedis = jedisPool.getResource();
             
              return jedis.sismember(key, value);
        } catch (Exception e) {
            jedisPool.returnBrokenResource(jedis);
            jedis = null;
            throw new CsRedisRuntimeException("jedis get fail", e);
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }

    /******* Hash Operation **********/
    
    /**
     * 将哈希表 key 中的域 field 的值设为 value 。如果 key 不存在，一个新的哈希表被创建并进行 HSET 操作。
     * 如果域 field 已经存在于哈希表中，旧值将被覆盖。
     * @param key 
     * 		hash表的标记key
     * @param field
     * 		hash表中的存储对象的key
     * @param value
     * 		hash表中的存储对象的key对应的value
     * @return
     *       如果 field 是哈希表中的一个新建域，并且值设置成功，返回 1 。
     *       如果哈希表中域 field 已经存在且旧值已被新值覆盖，返回 0 。
     */
    public Long hset(String key, String field, String value) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        try {
              jedisPool = getJedisMasterPool();
              jedis = jedisPool.getResource();
              
              return jedis.hset(key, field, value);
        } catch (Exception e) {
            jedisPool.returnBrokenResource(jedis);
            jedis = null;
            factory.init();
            throw new CsRedisRuntimeException("jedis set fail", e);
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }
    /**
     * 同时将多个 field-value (域-值)对设置到哈希表 key 中。此命令会覆盖哈希表中已存在的域。
     * 如果 key 不存在，一个空哈希表被创建并执行 HMSET 操作。
     * @param key
     * 		hash表的标记key
     * @param value
     * 		多个 field-value (域-值)对
     * @return
     *      如果命令执行成功，返回 OK 。
	 *	         当 key 不是哈希表(hash)类型时，返回一个错误。
     */
    public String hmset(String key, Map<String, String> value) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        try {
        	
             jedisPool = getJedisMasterPool();
             jedis = jedisPool.getResource();
              return jedis.hmset(key, value);
        } catch (Exception e) {
            jedisPool.returnBrokenResource(jedis);
            jedis = null;
            factory.init();
            throw new CsRedisRuntimeException("jedis set fail", e);
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }
    
    /**
     * 返回哈希表 key 中给定域 field 的值。
     * @param key
     * 		hash表的标记key
     * @param field
     * 		hash表中的某个存储空间的标识key
     * @return
     *    给定域的值。当给定域不存在或是给定 key 不存在时，返回 nil 。
     */
    public String hget(String key, String field) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        try {
	          jedisPool = getJedisSlavePool();
	          jedis = jedisPool.getResource();
        	
              return jedis.hget(key, field);
        } catch (Exception e) {
            jedisPool.returnBrokenResource(jedis);
            jedis = null;
            throw new CsRedisRuntimeException("jedis get fail", e);
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }
    /**
     * 返回哈希表 key 中，一个或多个给定域的值。
     * 如果给定的域不存在于哈希表，那么返回一个 nil 值。
     * 因为不存在的 key 被当作一个空哈希表来处理，所以对一个不存在的 key 进行 HMGET 操作将返回一个只带有 nil 值的表。
     * @param key
     * 		hash表的标记key
     * @param fields
     * 		多个给定域的标识fields
     * @return
     *     一个包含多个给定域的关联值的表，表值的排列顺序和给定域参数的请求顺序一样
     */
    public List<String> hmget(String key, String... fields) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        try {
             jedisPool = getJedisSlavePool();
             jedis = jedisPool.getResource();

             return jedis.hmget(key, fields);
        } catch (Exception e) {
            jedisPool.returnBrokenResource(jedis);
            jedis = null;
            throw new CsRedisRuntimeException("jedis get fail", e);
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }

    /*** common operation ***/
    /**
     * 移除指定key和value,并返回删除个数
     * @param key
     * 		set中的key值
     * @return
     * 		Long类型,删除元素个数
     */
    public Long remove(String key) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        try {
             jedisPool = getJedisMasterPool();
             jedis = jedisPool.getResource();
            return jedis.del(key);
        } catch (Exception e) {
            jedisPool.returnBrokenResource(jedis);
            jedis = null;
            factory.init();
            throw new CsRedisRuntimeException("jedis set fail", e);
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }
    
    /**
     * 将 key 改名为 newkey 。当 key 和 newkey 相同，或者 key 不存在时，返回一个错误。
     * 当 newkey 已经存在时， RENAME 命令将覆盖旧值。
     * @param oldkey
     * 		原有的key
     * @param newkey
     * 		新命名的key
     * @return
     *    改名成功时提示 OK ，失败时候返回一个错误。
     */
    public String rename(String oldkey,String newkey){
    	
    	Jedis jedis = null;
        JedisPool jedisPool = null;
        try {
            jedisPool = getJedisMasterPool();
            jedis = jedisPool.getResource();
              
            return jedis.rename(oldkey, newkey);
        } catch (Exception e) {
            jedisPool.returnBrokenResource(jedis);
            jedis = null;
            factory.init();
            throw new CsRedisRuntimeException("jedis set fail", e);
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }
    
   /**
    * 判断当前key是否已存在
    * @param key
    * 		set中的key值
    * @return
    * 	存在则返回true 否则返回false
    */
    public boolean exists(String key) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        try {
            jedisPool = getJedisSlavePool();
            jedis = jedisPool.getResource();
	         
            return jedis.exists(key);
        } catch (Exception e) {
            jedisPool.returnBrokenResource(jedis);
            jedis = null;
            throw new CsRedisRuntimeException("jedis get fail", e);
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }

    /**
     * 使用客户端向 Redis 服务器发送一个 PING 通常用于测试与服务器的连接是否仍然生效，或者用于测量延迟值。
     * @return 
     * 		 如果服务器运作正常的话，会返回一个 PONG,否则抛出异常
     */
    public String ping(){
    	Jedis jedis = null;
        JedisPool jedisPool = null;
        try {
            jedisPool = getJedisSlavePool();
            jedis = jedisPool.getResource();
           
            return jedis.ping() ; 
        } catch (Exception e) {
            jedisPool.returnBrokenResource(jedis);
            jedis = null;
            throw new CsRedisRuntimeException("jedis get fail", e);
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }
    
    /*** advanced operation ***/
    /**
     * 监视一个(或多个) key ，如果在事务执行之前这个(或这些) key 被其他命令所改动，那么事务将被打断。
     * @param key
     * 		set中的key值
     */
    public void watch(String key) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        try {
            jedisPool = getJedisMasterPool();
            jedis = jedisPool.getResource();

            jedis.watch(key);
        } catch (Exception e) {
            jedisPool.returnBrokenResource(jedis);
            jedis = null;
            factory.init();
            throw new CsRedisRuntimeException("jedis set fail", e);
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }
    /**
     * 进行事务处理
     * @param transactionAction
     * 		事务对象
     * @return
     * 		事务提交后,返回内容List 
     */
    public List<Object> doTransaction(TransactionAction transactionAction) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        try {
            jedisPool = getJedisMasterPool();
            jedis = jedisPool.getResource();
        	
            Transaction transaction = jedis.multi();
            transactionAction.execute(transaction);
            return transaction.exec();
        } catch (Exception e) {
            jedisPool.returnBrokenResource(jedis);
            jedis = null;
            factory.init();
            throw new CsRedisRuntimeException("jedis set fail", e);
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }
    /**
     * 通过使用管道，执行相关操作
     * @param  piplineAction 
     * 		管道对象
     */
    public void doPipline(PiplineAction piplineAction) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        try {
            jedisPool = getJedisMasterPool();
            jedis = jedisPool.getResource();
            
            Pipeline pipline = jedis.pipelined();
            piplineAction.execute(pipline);
            pipline.sync();
        } catch (Exception e) {
            jedisPool.returnBrokenResource(jedis);
            jedis = null;
            factory.init();
            throw new CsRedisRuntimeException("jedis set fail", e);
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }
    /**
     * 
     * @param piplineAction
     * 		管道对象
     * @return
     * 		管道操作返回的结果,List集合
     */
    public List<Object> doPiplineAndReturn(PiplineAction piplineAction) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        try {
            jedisPool = getJedisMasterPool();
            jedis = jedisPool.getResource();
        	
            Pipeline pipline = jedis.pipelined();
            piplineAction.execute(pipline);
            return pipline.syncAndReturnAll();
        } catch (Exception e) {
            jedisPool.returnBrokenResource(jedis);
            jedis = null;
            factory.init();
            throw new CsRedisRuntimeException("jedis set fail", e);
        } finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }
	
    
    
    /**
     * redis中进行管道相关操作时，实现此接口
     * @author duowan-PC
     *
     */
    public interface PiplineAction {
        void execute(Pipeline pipline);
    }
    /**
     * 数据库操作事务对象接口,在redis中进行事务相关的操作时，实现此接口
     * @author duowan-PC
     *
     */
    public interface TransactionAction {
        void execute(Transaction transaction);
    }
	
}
