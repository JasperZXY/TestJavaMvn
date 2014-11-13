package com.jasper.mvntest.NoSQL.redis.ha;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.jasper.mvntest.NoSQL.redis.csbase.CsRedisRuntimeException;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

public class RedisSentinelFactory extends JedisSentinelPool {
    /**在这个时间里ping通的可以加入slave pool中*/
    private static final float PING_ACCESS_TIME = 0.5f;
    /**在PING_ACCESS_TIME时间里没有ping通的找一个时间最小的，但不超过ACCEPT_TIME*/
    private static final float ACCEPT_TIME = 2.0f;
    private volatile HostAndPort currentHostMaster;
    private ScheduledExecutorService scheduledService;
    private CopyOnWriteArrayList<JedisPool> jedisSlavePools;
    private JedisPool jedisMasterPool;
    private int curSlavePoolIndex;
    
    public RedisSentinelFactory(String masterName, Set<String> sentinels, JedisPoolConfig config) {
        super(masterName, sentinels, config);
        init();
    }
    
    public RedisSentinelFactory(String masterName, Set<String> sentinels) {
        this(masterName, sentinels, new JedisPoolConfig());
    }
    
    public void init() {
        currentHostMaster = this.getCurrentHostMaster();
        if (currentHostMaster == null) {
            throw new CsRedisRuntimeException("can't get master");
        }
        jedisMasterPool = new JedisPool(poolConfig, currentHostMaster.getHost(), currentHostMaster.getPort());
        initSlavePool(getSlaveHostAndPort());
        scheduledService = Executors.newSingleThreadScheduledExecutor();
        scheduledService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                HostAndPort hostAndPort = getCurrentHostMaster();
                if (! hostAndPort.equals(currentHostMaster)) {
                    currentHostMaster = hostAndPort;
                    jedisMasterPool = new JedisPool(poolConfig, currentHostMaster.getHost(), currentHostMaster.getPort());
                    initSlavePool(getSlaveHostAndPort());
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
    }
    
    //这里比较简单，只有一层，还需要考虑slave下面还有slave
    private List<HostAndPort> getSlaveHostAndPort() {
        Pattern p = Pattern.compile("slave\\d+:ip=(.*),port=(\\d*),");
        List<HostAndPort> list = new ArrayList<HostAndPort>();
        LinkedList<HostAndPort> listSearch = new LinkedList<HostAndPort>();
        listSearch.add(currentHostMaster);
        while (listSearch.size() > 0) {
            HostAndPort hostAndPort = listSearch.pollFirst();
            Jedis jedis = new Jedis(hostAndPort.getHost(), hostAndPort.getPort());
            String info = jedis.info();
            Matcher m = p.matcher(info);
            while (m.find()) {
                String ip = m.group(1);
                int port = Integer.valueOf(m.group(2));
                HostAndPort addHostAndPort = new HostAndPort(ip, port);
                list.add(addHostAndPort);
                listSearch.add(addHostAndPort);
            }
        }
        return list;
    }
    
    private void initSlavePool(List<HostAndPort> list) {
        CopyOnWriteArrayList<JedisPool> newSlavePools = new CopyOnWriteArrayList<JedisPool>();
        float curPingTime = ACCEPT_TIME;
        HostAndPort curHostAndPort = null;
        JedisPool curJedisPool = null;
        for (HostAndPort hostAndPort : list) {
            float time = OsUtil.getReachTime(hostAndPort.getHost());
            if (time <= PING_ACCESS_TIME) {
                JedisPool jedisPool = new JedisPool(poolConfig, hostAndPort.getHost(), hostAndPort.getPort());
                newSlavePools.add(jedisPool);
            } else if (time < curPingTime) {
                curHostAndPort = hostAndPort;
                curPingTime = time;
            }
        }
        if (newSlavePools.size() <= 0 && curHostAndPort != null) {
            curJedisPool = new JedisPool(poolConfig, curHostAndPort.getHost(), curHostAndPort.getPort());
            newSlavePools.add(curJedisPool);
        }
        jedisSlavePools = newSlavePools;
    }
    
    public JedisPool getMasterPool() {
        return jedisMasterPool;
    }
    
    //暂时用这种方式返回slave
    public JedisPool getSlavePool() {
        if (jedisSlavePools == null || jedisSlavePools.isEmpty()) {
            return getMasterPool();
        }
        curSlavePoolIndex = (curSlavePoolIndex + 1) % jedisSlavePools.size();
        return jedisSlavePools.get(curSlavePoolIndex);
    }
    
}
