package com.bj58.etx.core;

import com.bj58.etx.api.context.IEtxContext;
import com.bj58.etx.api.db.IEtxDao;
import com.bj58.etx.api.enums.EtxRunMode;
import com.bj58.etx.api.exception.EtxException;
import com.bj58.etx.api.serialize.IEtxSerializer;
import com.bj58.etx.core.cache.EtxClassCache;
import com.bj58.etx.core.context.EtxBaseContext;
import com.bj58.etx.core.runtime.EtxRuntime;
import com.bj58.etx.core.service.EtxService;
import com.bj58.etx.core.service.EtxTaskService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

public class Etx {

    private static Logger log = LoggerFactory.getLogger(Etx.class);

    private static volatile AtomicBoolean isInit = new AtomicBoolean(false);

    /**
     * 初始化Etx运行环境
     */
    static {
        URL url = Etx.class.getClassLoader().getResource("etx.properties");
        if (url != null) {
            init(url.getPath());
        }
    }

    public static void init(String path) {
        if (!isInit.get()) {
            log.info("------初始化Etx环境,path=" + path);
            try {
                Properties p = new Properties();
                File file = new File(path);
                p.load(new FileInputStream(file));

                String daoClass = p.getProperty("etx.daoClass");
                String serializeClass = p.getProperty("etx.serializeClass");
                String dbHeatBeatInterval = p.getProperty("etx.dbHeatBeatInterval");
                String dbMaxFailCount = p.getProperty("etx.dbMaxFailCount");
                String processThreadCount = p.getProperty("etx.processThreadCount");
                String taskThreadCount = p.getProperty("etx.taskThreadCount");
                String taskLoopTxInterval = p.getProperty("etx.taskLoopTxInterval");
                String countForCancelOnce = p.getProperty("etx.countForCancelOnce");
                String countForDoAsyncOnce = p.getProperty("etx.countForDoAsyncOnce");

                if (StringUtils.isNotBlank(daoClass)) {
                    IEtxDao dao = EtxClassCache.getInstance(daoClass);
                    EtxRuntime.setDao(dao);
                }

                if (StringUtils.isNotBlank(serializeClass)) {
                    IEtxSerializer serializer = EtxClassCache.getInstance(serializeClass);
                    EtxRuntime.setSerializer(serializer);
                }

                if (StringUtils.isNotBlank(dbHeatBeatInterval)) {
                    EtxRuntime.setDbHeatBeatInterval(Integer.valueOf(dbHeatBeatInterval));
                }
                if (StringUtils.isNotBlank(dbMaxFailCount)) {
                    EtxRuntime.setDbMaxFailCount(Integer.valueOf(dbMaxFailCount));
                }
                if (StringUtils.isNotBlank(processThreadCount)) {
                    EtxRuntime.setProcessThreadCount(Integer.valueOf(processThreadCount));
                }
                if (StringUtils.isNotBlank(taskThreadCount)) {
                    EtxRuntime.setTaskThreadCount(Integer.valueOf(taskThreadCount));
                }
                if (StringUtils.isNotBlank(taskLoopTxInterval)) {
                    EtxRuntime.setTaskLoopTxInterval(Integer.valueOf(taskLoopTxInterval));
                }
                if (StringUtils.isNotBlank(countForCancelOnce)) {
                    EtxRuntime.setCountForCancelOnce(Integer.valueOf(countForCancelOnce));
                }
                if (StringUtils.isNotBlank(countForDoAsyncOnce)) {
                    EtxRuntime.setCountForDoAsyncOnce(Integer.valueOf(countForDoAsyncOnce));
                }

                isInit.set(true);
            } catch (Exception e) {
                log.error("EtxInit error", e);
                throw new EtxException(e);
            }
        }
    }

    /**
     * 用etx的方式执行业务逻辑
     */
    public static EtxService open() {
        try {
            IEtxContext ctx = new EtxBaseContext();
            EtxService etxService = new EtxService(ctx);
            etxService.setRunMode(EtxRunMode.BINLOG.getCode());
            return etxService;
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

    /**
     * 创建etx task任务
     */
    public static void startTask() {
        try {
            EtxTaskService etxTaskService = new EtxTaskService();
            etxTaskService.start();
        } catch (Exception e) {
            log.error("", e);
        }
    }
}
