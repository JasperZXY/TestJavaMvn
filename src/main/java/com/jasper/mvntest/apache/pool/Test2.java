package com.jasper.mvntest.apache.pool;

import org.apache.commons.pool.KeyedObjectPool;
import org.apache.commons.pool.impl.StackKeyedObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <a href="http://blog.csdn.net/kongxx/article/details/6607148">原文</a><br>
 * 运行测试类，可以看到在第一个循环里虽然循环了10次，一共要了10次MyConnection对象，
 * 每次请求的key都不一样，从conn_0到conn_9，由于使用的是
 * KeyedPoolableObjectFactory和KeyedObjectPool接口，
 * 所以返回的10个MyConnection对象实例每个都不一样，
 * 并且从日志可以看出makeObject方法被调用了10次。
 * 第二个循环里，虽然也是请求了10次MyConnection对象，
 * 但是由于每次的key都不一样，所以每次返回的都是同一个MyConnection对象实例，
 * 并且从日志可以看出makeObject方法只被调用了一次。
 *
 * @author Jasper
 */
public class Test2 {
    private static Logger logger = LoggerFactory.getLogger(Test2.class);

    public static void main(String[] args) throws Exception {
        MyConnectionKeyedPoolableObjectFactory factory = new MyConnectionKeyedPoolableObjectFactory();
        KeyedObjectPool pool = new StackKeyedObjectPool(factory);
        try {
            logger.info("================================================");
            for (int i = 0; i < 10; i++) {
                String key = "conn_" + i;
                MyConnection myConn = (MyConnection) pool.borrowObject(key);
                try {
                    myConn.print();
                } catch (Exception ex) {
                    pool.invalidateObject(key, myConn);
                } finally {
                    pool.returnObject(key, myConn);
                }
            }

            logger.info("================================================");
            for (int i = 0; i < 10; i++) {
                String key = "conn_xxx";
                MyConnection myConn = (MyConnection) pool.borrowObject(key);
                try {
                    myConn.print();
                } catch (Exception ex) {
                    pool.invalidateObject(key, myConn);
                } finally {
                    pool.returnObject(key, myConn);
                }
            }
        } finally {
            logger.info("Close Pool");
            try {
                pool.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}