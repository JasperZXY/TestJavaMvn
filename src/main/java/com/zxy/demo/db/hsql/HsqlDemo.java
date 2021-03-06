package com.zxy.demo.db.hsql;

import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;


/**
 * hsql的简单例子
 */
public class HsqlDemo {

    @Test
    public void test() throws Exception {
        // 加载HSQL DB的JDBC驱动
        Class.forName("org.hsqldb.jdbcDriver");
        // 在内存中建立数据库db，用户名跟密码为空
        Connection conn = DriverManager.getConnection("jdbc:hsqldb:mem:db");
        Statement stat = conn.createStatement();

        // 新建表
        stat.executeUpdate("create table user(id INTEGER GENERATED BY DEFAULT AS IDENTITY(start with 10,increment by 5), name VARCHAR(128), PRIMARY KEY (id))");

        // 插入数据
        String sqlForInsert = "INSERT INTO user (name) VALUES('zxy')";
        int insertResult = stat.executeUpdate(sqlForInsert);
        System.out.println("Insert " + insertResult);

        sqlForInsert = "INSERT INTO user (name) VALUES('jasper')";
        insertResult = stat.executeUpdate(sqlForInsert);
        System.out.println("Insert " + insertResult);

        // 查询数据
        PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM user");
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            String s = rs.getString(1) + " : " + rs.getString(2);
            System.out.println(s);
        }
        System.out.println("=====end=====");
    }

}
