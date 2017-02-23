package com.jasper.mvntest.apache.pool;

import org.apache.commons.pool.KeyedPoolableObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyConnectionKeyedPoolableObjectFactory implements KeyedPoolableObjectFactory {

    private static Logger logger = LoggerFactory.getLogger(MyConnectionKeyedPoolableObjectFactory.class);

    private static int count = 0;

    public Object makeObject(Object key) throws Exception {
        MyConnection myConn = new MyConnection(key.toString());
        logger.info("makeObject " + myConn.getName());
        myConn.connect();
        return myConn;
    }

    public void activateObject(Object key, Object obj) throws Exception {
        MyConnection myConn = (MyConnection) obj;
        logger.info("activateObject " + myConn.getName());
    }

    public void passivateObject(Object key, Object obj) throws Exception {
        MyConnection myConn = (MyConnection) obj;
        logger.info("passivateObject " + myConn.getName());
    }

    public boolean validateObject(Object key, Object obj) {
        MyConnection myConn = (MyConnection) obj;
        logger.info("validateObject " + myConn.getName());
        return myConn.isConnected();
    }

    public void destroyObject(Object key, Object obj) throws Exception {
        MyConnection myConn = (MyConnection) obj;
        logger.info("destroyObject " + myConn.getName());
        myConn.close();
    }
}