package com.fms.utils;

import java.sql.Connection;
import java.sql.DriverManager;

public class JdbcUtils {

    //获取数据库连接
    public static Connection getCon() {
        //数据库连接名称
        String username = "root";
        //数据库连接密码
        String password = "Admin!123";
        String driver = "com.mysql.jdbc.Driver";
        //其中test为数据库名称
        String url = "jdbc:mysql://47.93.196.60:3306/mysql?prepStmtCacheSize=517&cachePrepStmts=true&autoReconnect=true&characterEncoding=utf-8";
        Connection conn = null;
        try {
            Class.forName(driver);
            conn = (Connection) DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;

    }
}
