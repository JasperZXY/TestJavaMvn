package com.zxy.demo.apache.pool;

import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.StackObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <a href="http://blog.csdn.net/kongxx/article/details/6607083">原文</a><br>
 * 运行测试类，可以看到在第一个循环里虽然循环了10次，
 * 一共要了10个MyConnection对象，
 * 但是每次返回的都是“conn_1”这个MyConnection对象实例，
 * 并且从日志可以看出，makeObject方法只被调用了一次，
 * 因此，除了第一次以外，后面的每次申请都是从pool里取出来的。
 * 而在第二个循环中，每次申请了两个MyConnection对象实例，
 * 从日志可以看到，在第二个循环里也只调用了一次makeObject方法，
 * 并且创建的是conn_2对象实例，这是由于conn_1这个对象已经在第一个循环中被创建了出来，
 * 此时只是直接拿出来使用了。这里为了好测试，没有在第二个循环中做异常处理，
 * 真实情况下应该像第一个循环里的代码类是，在borrowObject和使用pool中对象出现异常时要记得调用invalidateObject方法，并且归还pool中的对象。
 *
 * @author Jasper
 */
public class Test1 {
    private static Logger logger = LoggerFactory.getLogger(Test1.class);

    public static void main(String[] args) throws Exception {
        PoolableObjectFactory<MyConnection> factory = new MyConnectionPoolableObjectFactory();
        ObjectPool pool = new StackObjectPool(factory);
        try {
            logger.info("================================================");
            for (int i = 0; i < 10; i++) {
                logger.info("cur " + i);
                MyConnection myConn = (MyConnection) pool.borrowObject();
                try {
                    myConn.print();
                } catch (Exception ex) {
                    pool.invalidateObject(myConn);
                } finally {
                    pool.returnObject(myConn);
                }
            }

            logger.info("================================================");
            for (int i = 0; i < 10; i++) {
                MyConnection myConn1 = (MyConnection) pool.borrowObject();
                MyConnection myConn2 = (MyConnection) pool.borrowObject();
                myConn1.print();
                myConn2.print();
                pool.returnObject(myConn1);
                pool.returnObject(myConn2);
            }
        } finally {
            try {
                pool.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}