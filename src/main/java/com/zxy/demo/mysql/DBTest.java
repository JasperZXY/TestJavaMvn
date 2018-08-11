package com.zxy.demo.mysql;

import org.junit.Test;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * Created by zhongxianyao.
 * Time 2018/7/9 下午2:06
 * Desc 文件描述
 *
 *
CREATE TABLE `demo_user` (
 `id` int(11) NOT NULL AUTO_INCREMENT,
 `name` varchar(128) NOT NULL DEFAULT '' COMMENT '用户名',
 `gender` int(11) NOT NULL DEFAULT '0' COMMENT '性别',
 `age` int(4) NOT NULL DEFAULT '0' COMMENT '年龄',
 `birthday` date NOT NULL COMMENT '生日',
 `address` varchar(256) NOT NULL COMMENT '地址',
 PRIMARY KEY (`id`)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

 */
public class DBTest {

    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

    static final String DB_URL = "jdbc:mysql://localhost:3306/demo";

    // 数据库的用户名与密码，需要根据自己的设置
    static final String USER = "root";

    static final String PASS = "123456";


    @Test
    public void test() {
        Connection conn = null;
        Statement stmt = null;
        try {
            // 注册 JDBC 驱动
            Class.forName(JDBC_DRIVER);

            // 打开链接
            System.out.println("连接数据库...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // 执行查询
            System.out.println(" 实例化Statement对象...");
            stmt = conn.createStatement();
            String sql;
            sql = "SELECT id, name, birthday FROM demo_user";
            ResultSet rs = stmt.executeQuery(sql);

            // 展开结果集数据库
            while (rs.next()) {
                // 通过字段检索
                long id = rs.getLong("id");
                String name = rs.getString("name");
                Date birthday = rs.getDate("birthday");

                System.out.printf("ID:%s name:%s birthday:%s\n", id, name, birthday);
            }

            // 完成后关闭
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            // 处理 JDBC 错误
            se.printStackTrace();
        } catch (Exception e) {
            // 处理 Class.forName 错误
            e.printStackTrace();
        } finally {
            // 关闭资源
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException se2) {
                se2.printStackTrace();
            }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        System.out.println("end");
    }

}
