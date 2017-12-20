package com.bj58.etx.boot.helper;

import com.bj58.etx.core.util.EtxDateTimeUtil;
import com.bj58.etx.core.util.EtxIdUtil;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions.Builder;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.lang.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

/**
 * mango db 客户端
 *
 * @author shencl
 */
public class MongoHelper {

    public static MongoClient client = null;
    private static Logger log = LoggerFactory.getLogger(MongoHelper.class);
    private static MongoClientURI MONGO_URI = null;
    private static String DBNAME = "mdb58_zpetx";
    // 分表间隔天数
    private static int PARTION_DAY = 15;
    // 聚合最近几张表
    public static int AGG_TABLE_COUNT = 2;
    private static String TX_TABLE_NAME = "t_tx_";
    private static String SYNCLOG_TABLE_NAME = "t_sync_log_";
    private static String ASYNCLOG_TABLE_NAME = "t_async_log_";

    public static void init(String path) {
        try {
            Properties p = new Properties();
            File file = new File(path);
            p.load(new FileInputStream(file));

            String url = p.getProperty("mongo.url");
            String minPoolSize = p.getProperty("mongo.minPoolSize");
            String maxPoolSize = p.getProperty("mongo.maxPoolSize");
            String maxWaitTime = p.getProperty("mongo.maxWaitTime");
            String connectTimeout = p.getProperty("mongo.connectTimeout");
            String socketTimeout = p.getProperty("mongo.socketTimeout");
            String partionDay = p.getProperty("mongo.partionDay");
            String aggTableCount = p.getProperty("mongo.aggTableCount");

            Builder build = new Builder();
            build.minConnectionsPerHost(Integer.valueOf(minPoolSize));
            build.connectionsPerHost(Integer.valueOf(maxPoolSize));
            build.connectTimeout(Integer.valueOf(connectTimeout));
            build.maxWaitTime(Integer.valueOf(maxWaitTime));
            build.socketTimeout(Integer.valueOf(socketTimeout));

            MONGO_URI = new MongoClientURI(url, build);

            if (StringUtils.isNotBlank(partionDay)) {
                PARTION_DAY = Integer.valueOf(partionDay);
            }

            if (StringUtils.isNotBlank(aggTableCount)) {
                AGG_TABLE_COUNT = Integer.valueOf(aggTableCount);
            }

            DBNAME = MONGO_URI.getDatabase();

        } catch (Exception e) {
            log.error("mongodb init error", e);
        }
    }

    public static MongoClient getClient() {
        if (client == null) {
            synchronized (MongoHelper.class) {
                if (client == null) {
                    client = new MongoClient(MONGO_URI);
                }
            }
        }
        return client;
    }

    public static MongoDatabase getDB() {
        return getClient().getDatabase(DBNAME);
    }

    /**
     * 获取当前的N个表
     */
    public static MongoCollection<Document> getLastTXTable(int n) {
        int dayMillSeconds = 1000 * 3600 * 24;
        long timestamp = System.currentTimeMillis() - (n * dayMillSeconds * PARTION_DAY);
        return getDB().getCollection(TX_TABLE_NAME + getTableDateByStamp(timestamp));
    }

    public static MongoCollection<Document> getTXTable(long txId) {
        return getDB().getCollection(TX_TABLE_NAME + getTableDate(txId));
    }

    public static MongoCollection<Document> getSyncLogTable(long txId) {
        return getDB().getCollection(SYNCLOG_TABLE_NAME + getTableDate(txId));
    }

    public static MongoCollection<Document> getAsyncLogTable(long txId) {
        return getDB().getCollection(ASYNCLOG_TABLE_NAME + getTableDate(txId));
    }

    /**
     * 获取表后缀
     */
    private static String getTableDate(long txId) {
        long time = EtxIdUtil.getTimestamp(txId);
        return getTableDateByStamp(time);
    }

    private static String getTableDateByStamp(long timestamp) {
        int diff = diff(timestamp);
        if (diff % PARTION_DAY == 0) {
            return EtxDateTimeUtil.getYYYYMMDD(new Date());
        } else {
            Calendar c = Calendar.getInstance();
            c.setTime(new Date(timestamp));
            c.add(Calendar.DATE, -(diff % PARTION_DAY));
            return EtxDateTimeUtil.getYYYYMMDD(c.getTime());
        }
    }

    /**
     * 求一个日期和基准日期间隔的天数
     */
    private static int diff(long timestamp) {
        long baseline = 1483200000000L; // 2017-01-01 00:00:00 为基准线
        long days = (timestamp - baseline) / (1000 * 60 * 60 * 24);
        return (int) days;
    }
}
