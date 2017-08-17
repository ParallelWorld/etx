package com.bj58.etx.boot.helper;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mysql.jdbc.Driver;

@SuppressWarnings("static-access")
public class MysqlHelper {

	private static Log log = LogFactory.getLog(MysqlHelper.class);
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

	public static void init(String path) {
		try {
			Properties p = new Properties();
			File file = new File(path);
			p.load(new FileInputStream(file));

			URL = p.getProperty("mysql.url");
			USERNAME = p.getProperty("mongo.username");
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
			try {
				if (conn != null) {
					conn.commit();
				}
			} finally {
				if (conn != null) {
					conn.close();
				}
			}
		} catch (SQLException e) {
			log.error("释放 connection失败", e);
		}
	}
}
