package com.bj58.etx.boot.helper;

import com.mysql.jdbc.Driver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

@SuppressWarnings("static-access")
public class MysqlHelper {

    private static Logger log = LoggerFactory.getLogger(MysqlHelper.class);
    public static MysqlHelper instance = null;
    private static String URL;
    private static String USERNAME;
    private static String PASSWORD;

    static {
        // 加载驱动
        try {
            Driver.class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            log.error("加载mysql驱动失败", e);
        }
    }

    static {
        String path = MongoHelper.class.getClassLoader().getResource("mysql.properties").getPath();

        try {
            Properties p = new Properties();
            File file = new File(path);
            p.load(new FileInputStream(file));

            URL = p.getProperty("mysql.url");
            USERNAME = p.getProperty("mysql.username");
            PASSWORD = p.getProperty("mysql.password");

        } catch (Exception e) {
            log.error("mysql init error", e);
        }
    }

    public static Connection getConn() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            log.error("获取mysql connection失败", e);
        }
        return conn;
    }

    public static void releaseConn(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            log.error("释放 connection失败", e);
        }
    }
}
