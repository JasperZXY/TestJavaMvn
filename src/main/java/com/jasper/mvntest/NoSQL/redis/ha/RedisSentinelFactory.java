package com.jasper.mvntest.NoSQL.redis.ha;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jasper.mvntest.NoSQL.redis.csbase.CsRedisRuntimeException;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class RedisSentinelFactory extends JedisSentinelPool {
    /** 在这个时间里ping通的可以加入slave pool中 */
    private static final float PING_ACCESS_TIME = 0.5f;
    /** 在PING_ACCESS_TIME时间里没有ping通的找一个时间最小的，但不超过ACCEPT_TIME */
    private static final float ACCEPT_TIME = 2.0f;
    private volatile HostAndPort currentHostMaster;
    private ScheduledExecutorService scheduledService;
    private CopyOnWriteArrayList<JedisPool> jedisSlavePools;
    private List<HostAndPort> slaveHostAndPorts;
    private JedisPool jedisMasterPool;
    private AtomicInteger curSlavePoolIndex = new AtomicInteger(0);
    private Set<String> sentinels = null;
    private String masterName;

    public RedisSentinelFactory(String masterName, Set<String> sentinels, JedisPoolConfig config) {
        super(masterName, sentinels, config);
        this.sentinels = sentinels;
        this.masterName = masterName;
        init();
    }

    public RedisSentinelFactory(String masterName, Set<String> sentinels) {
        this(masterName, sentinels, new JedisPoolConfig());
    }

    public void init() {
        jedisSlavePools = new CopyOnWriteArrayList<JedisPool>();
        slaveHostAndPorts = Collections.emptyList();
        currentHostMaster = this.getCurrentHostMaster();
        if (currentHostMaster == null) {
            throw new CsRedisRuntimeException("can't get master");
        }
        jedisMasterPool = new JedisPool(poolConfig, currentHostMaster.getHost(), currentHostMaster.getPort());
        initSlavePool(getSlaveHostAndPort());
        for (String sentinel : sentinels) {
            String[] strings = sentinel.split(":");
            if (strings.length < 2) {
                //TODO 先这样处理
                throw new RuntimeException("sentinels error:" + sentinel);
            } else {
                new SentinelListener(masterName, strings[0], Integer.valueOf(strings[1])).start();
            }
        }
        scheduledService = Executors.newSingleThreadScheduledExecutor();
        scheduledService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                HostAndPort hostAndPort = getCurrentHostMaster();
                if (!hostAndPort.equals(currentHostMaster)) {
                    log.info("currentHostMaster" + currentHostMaster);
                    currentHostMaster = hostAndPort;
                    jedisMasterPool = new JedisPool(poolConfig, currentHostMaster.getHost(), currentHostMaster.getPort());
                }
                initSlavePool(getSlaveHostAndPort());
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    @SuppressWarnings("resource")
    private List<HostAndPort> getSlaveHostAndPort() {
        Pattern p = Pattern.compile("slave\\d+:ip=(.*),port=(\\d*),");
        List<HostAndPort> list = new ArrayList<HostAndPort>();
        LinkedList<HostAndPort> listSearch = new LinkedList<HostAndPort>();
        listSearch.add(currentHostMaster);
        while (listSearch.size() > 0) {
            HostAndPort hostAndPort = listSearch.pollFirst();
            Jedis jedis = new Jedis(hostAndPort.getHost(), hostAndPort.getPort());
            String info = jedis.info();
            if (info == null || "".equals(info)) {
                continue;
            }
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
        List<HostAndPort> newHostAndPorts = new ArrayList<HostAndPort>();
        float curPingTime = ACCEPT_TIME;
        HostAndPort curHostAndPort = null;
        for (HostAndPort hostAndPort : list) {
            float time = OsUtil.getReachTime(hostAndPort.getHost());
            log.info("hostAndPort:" + hostAndPort + " time:" + time);
            if (time <= PING_ACCESS_TIME) {
                newHostAndPorts.add(hostAndPort);
            } else if (time < curPingTime) {
                curHostAndPort = hostAndPort;
                curPingTime = time;
            }
        }
        if (curHostAndPort != null && newHostAndPorts.isEmpty()) {
            newHostAndPorts.add(curHostAndPort);
        }

        if (slaveHostAndPorts.size() <= 0) {
            for (int i = 0; i < newHostAndPorts.size(); i++) {
                HostAndPort hostAndPort = newHostAndPorts.get(i);
                jedisSlavePools.add(new JedisPool(poolConfig, hostAndPort.getHost(), hostAndPort.getPort()));
            }
            slaveHostAndPorts = newHostAndPorts;
        } else {
            // 删除没有的
            for (int i = 0; i < slaveHostAndPorts.size(); i++) {
                HostAndPort hostAndPort = slaveHostAndPorts.get(i);
                if (!newHostAndPorts.contains(hostAndPort)) {
                    jedisSlavePools.remove(i);
                    slaveHostAndPorts.remove(i);
                }
            }
            // 添加新节点
            for (int i = 0; i < newHostAndPorts.size(); i++) {
                HostAndPort hostAndPort = newHostAndPorts.get(i);
                if (!slaveHostAndPorts.contains(hostAndPort)) {
                    jedisSlavePools.add(new JedisPool(poolConfig, hostAndPort.getHost(), hostAndPort.getPort()));
                    slaveHostAndPorts.add(hostAndPort);
                }
            }
        }
        log.info("slave:" + list);
        log.info("init: master:" + currentHostMaster + " slave:" + jedisSlavePools);
    }

    public JedisPool getMasterPool() {
        return jedisMasterPool;
    }

    // 暂时用这种方式返回slave
    public JedisPool getSlavePool() {
        if (jedisSlavePools == null || jedisSlavePools.isEmpty()) {
            return getMasterPool();
        }
        return jedisSlavePools.get(curSlavePoolIndex.getAndIncrement() % jedisSlavePools.size());
    }

    protected class SentinelListener extends Thread {
        protected String masterName;
        protected String host;
        protected int port;
        protected long subscribeRetryWaitTimeMillis = 5000;
        protected Jedis j;
        protected AtomicBoolean running = new AtomicBoolean(false);

        protected SentinelListener() {
        }

        public SentinelListener(String masterName, String host, int port) {
            this.masterName = masterName;
            this.host = host;
            this.port = port;
        }

        public SentinelListener(String masterName, String host, int port, long subscribeRetryWaitTimeMillis) {
            this(masterName, host, port);
            this.subscribeRetryWaitTimeMillis = subscribeRetryWaitTimeMillis;
        }

        public void run() {

            running.set(true);

            while (running.get()) {

                j = new Jedis(host, port);

                try {
                    j.subscribe(new JedisPubSubAdapter() {
                        @Override
                        public void onMessage(String channel, String message) {
                            log.fine("Sentinel " + host + ":" + port + " published: " + message + ".");

                            String[] switchMasterMsg = message.split(" ");
                            //[sentinel, 172.19.108.127:26520, 172.19.108.127, 26520, @, master_2, 172.19.108.127, 6510]
                            log.info("+sentinel-->" + Arrays.asList(switchMasterMsg));

//                            if (switchMasterMsg.length > 3) {
//
//                                if (masterName.equals(switchMasterMsg[0])) {
//                                    initPool(toHostAndPort(Arrays.asList(switchMasterMsg[3], switchMasterMsg[4])));
//                                } else {
//                                    log.fine("Ignoring message on +switch-master for master name " + switchMasterMsg[0] + ", our master name is "
//                                            + masterName);
//                                }
//
//                            } else {
//                                log.severe("Invalid message received on Sentinel " + host + ":" + port + " on channel +switch-master: " + message);
//                            }
                        }
                    }, "+sentinel");

                } catch (JedisConnectionException e) {
                    if (running.get()) {
                        log.severe("Lost connection to Sentinel at " + host + ":" + port + ". Sleeping 5000ms and retrying.");
                        try {
                            Thread.sleep(subscribeRetryWaitTimeMillis);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    } else {
                        log.fine("Unsubscribing from Sentinel at " + host + ":" + port);
                    }
                }
            }
        }

        public void shutdown() {
            try {
                log.fine("Shutting down listener on " + host + ":" + port);
                running.set(false);
                // This isn't good, the Jedis object is not thread safe
                j.disconnect();
            } catch (Exception e) {
                log.severe("Caught exception while shutting down: " + e.getMessage());
            }
        }
    }

}
