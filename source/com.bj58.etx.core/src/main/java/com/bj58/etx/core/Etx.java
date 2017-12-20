package com.bj58.etx.core;

import com.bj58.etx.api.context.IEtxContext;
import com.bj58.etx.api.enums.EtxRunMode;
import com.bj58.etx.core.context.EtxBaseContext;
import com.bj58.etx.core.service.EtxService;
import com.bj58.etx.core.service.EtxTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Etx {

    private static Logger log = LoggerFactory.getLogger(Etx.class);

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
