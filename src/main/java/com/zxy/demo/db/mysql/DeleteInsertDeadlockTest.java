package com.zxy.demo.db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * delete 与 insert 引起的死锁
 *
 建表语句
 CREATE TABLE `t_order_item` (
 `id` bigint(20) NOT NULL AUTO_INCREMENT,
 `order_no` varchar(64) NOT NULL DEFAULT '0',
 `item_id` bigint(20) NOT NULL DEFAULT '0',
 `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
 PRIMARY KEY (`id`),
 UNIQUE KEY `order_no` (`order_no`)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
 */
public class DeleteInsertDeadlockTest {

    private static String url = "jdbc:mysql://localhost:3306/demo";
    private static String user = "root";
    private static String password = "123456";

    private static int loop = 200;

    private static String sql1 = "DELETE FROM t_order_item WHERE order_no = 'zxy123'";
    private static String sql2 = "INSERT INTO t_order_item(order_no, item_id) VALUES('zxy123', 1001)";

    private static ExecutorService threadPool = Executors.newFixedThreadPool(8);
    private static ThreadLocal<Connection> connectionThreadLocal = ThreadLocal.withInitial(() -> {
        try {
            return getConn();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    });

    public static void main(String[] args) {
        for (int i=0; i<loop; i++) {
            threadPool.execute(() -> {
                Connection connection = connectionThreadLocal.get();
                try {
                    // 事务开启
                    connection.setAutoCommit(false);
                    PreparedStatement preparedStatement1 = connection.prepareStatement(sql1);
                    PreparedStatement preparedStatement2 = connection.prepareStatement(sql2);
                    preparedStatement1.executeUpdate();
                    preparedStatement2.executeUpdate();
                    connection.commit();
                    preparedStatement1.close();
                    preparedStatement2.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    try {
                        connection.rollback();
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }
            });
        }

        threadPool.shutdown();

        System.out.println("end");
    }

    public static Connection getConn() throws Exception{
        Connection connection = DriverManager.getConnection(url, user, password);
        return connection;
    }

}
