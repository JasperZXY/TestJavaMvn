package com.zxy.demo.apache.pool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyConnection {

    private static Logger logger = LoggerFactory.getLogger(MyConnection.class);

    private String name;
    private boolean connected;

    public MyConnection(String name) {
        this.name = name;
    }

    public void connect() {
        this.connected = true;
        logger.info("connect " + name + ": " + connected);
    }

    public void close() {
        this.connected = false;
        logger.info("close " + name + ": " + connected);
    }

    public boolean isConnected() {
        return this.connected;
    }

    public String getName() {
        return this.name;
    }

    public void print() {
        logger.info("print " + this.name);
    }
}