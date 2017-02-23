package com.jasper.mvntest.apache.pool;

import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <a href="http://blog.csdn.net/kongxx/article/details/6612760">原文</a><br>
 * 其中包含了三个方法，分别测试了三种情况；
 * <ul>
 * <li>
 * 类中包含了一个实现了Runnable接口的内部类，目的是为了启动几个线程来模拟的对连接类的使用，并且为了尽可能的真实，在run方法里sleep了10秒中；
 * </li>
 * <li>首先运行测试方法test1()可以看到，在循环10个线程申请Connection类时，
 * 前面5个可以很好的获取，但是后面5个线程就不能获取连接，并且抛出了异常， 这是由于“config.maxActive =
 * 5;”和“config.maxWait = 5 * 1000;”在起作用，
 * 由于配置了最大活动连接是5个，并且后续申请没有有效连接的等待时间是5秒，所以test1方法中后面五个线程在等了5秒后全部抛出异常，表明不能申请连接了。</li>
 * <li>下面运行test2()方法，在test2中把“config.maxWait = 20 * 1000;”改成了20秒，
 * 而我们程序中每个线程使用连接会用去10秒，所以后面五个线程在等待了10秒后就全部获取连接了，所以程序最后会运行成功。</li>
 * <li>再看test3()方法，其中把maxIdle和minIdle都改为0，就是在连接不用时立即真正归还连接，
 * 对于数据库连接来说就是关闭物理连接，而maxWait改为-1，就是如果没有申请到连接就永远等待，
 * 运行test3()方法，观察日志，可以看出程序在用户连接对象以后
 * ，会调用MyConnectionPoolableObjectFactory.destroyObject()
 * 和MyConnection.close()方法来销毁对象。所以如果是使用这样的配置，就相当于每次都是物理连接，用完后就关闭连接。
 * 当然这里是一个极端的例子，真实情况下不会把maxIdle和minIdle都设为0的。</li>
 * </ul>
 * 其实对于GenericObjectPool.Config类和GenericKeyedObjectPool.Config类还是有很多配置参数的，
 * 这里只是列出的最简单的几个常用的，具体可以参考官方文档
 *
 * @author Jasper
 */
public class Test3 {

    private static Logger logger = LoggerFactory.getLogger(Test3.class);

    public static void main(String[] args) {
        System.out.println("====================");
//        test1();
        test2();
//         test3();
    }

    private static void test1() {
        PoolableObjectFactory factory = new MyConnectionPoolableObjectFactory();
        GenericObjectPool.Config config = new GenericObjectPool.Config();
        config.lifo = false;
        config.maxActive = 5;
        config.maxIdle = 5;
        config.minIdle = 1;
        config.maxWait = 5 * 1000;

        ObjectPool pool = new GenericObjectPool(factory, config);
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(new MyTask(pool));
            thread.start();
        }
        // closePool(pool);
    }

    private static void test2() {
        PoolableObjectFactory factory = new MyConnectionPoolableObjectFactory();
        GenericObjectPool.Config config = new GenericObjectPool.Config();
        config.lifo = false;
        config.maxActive = 5;
        config.maxIdle = 5;
        config.minIdle = 1;
        config.maxWait = 20 * 1000;

        ObjectPool pool = new GenericObjectPool(factory, config);
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(new MyTask(pool));
            thread.start();
        }
        // closePool(pool);
    }

    private static void test3() {
        PoolableObjectFactory factory = new MyConnectionPoolableObjectFactory();
        GenericObjectPool.Config config = new GenericObjectPool.Config();
        config.lifo = false;
        config.maxActive = 5;
        config.maxIdle = 0;
        config.minIdle = 0;
        config.maxWait = -1;

        ObjectPool pool = new GenericObjectPool(factory, config);
        Thread thread = new Thread(new MyTask(pool));
        thread.start();

        try {
            Thread.sleep(60L * 1000L);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // closePool(pool);
    }

    private static void closePool(ObjectPool pool) {
        try {
            pool.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class MyTask implements Runnable {
        private ObjectPool pool;

        public MyTask(ObjectPool pool) {
            this.pool = pool;
        }

        public void run() {
            MyConnection myConn = null;
            try {
                myConn = (MyConnection) pool.borrowObject();
                try {
                    myConn.print();
                } catch (Exception ex) {
                    pool.invalidateObject(myConn);
                    myConn = null;
                }
                Thread.sleep(10L * 1000L);
            } catch (Exception ex) {
                logger.error("Cannot borrow connection from pool.", ex);
            } finally {
                if (myConn != null) {
                    try {
                        pool.returnObject(myConn);
                    } catch (Exception ex) {
                        logger.error("Cannot return connection from pool.", ex);
                    }
                }
            }
        }
    }
}