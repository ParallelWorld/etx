package com.bj58.etx.boot;

import com.bj58.etx.api.context.IEtxContext;
import com.bj58.etx.api.enums.EtxRunMode;
import com.bj58.etx.boot.context.EtxBaseContext;
import com.bj58.etx.core.EtxService;
import com.bj58.etx.core.EtxTaskService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Etx {
	
	private static Log log = LogFactory.getLog(Etx.class);
	
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
			log.error("",e);
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
			log.error("",e);
		} 
	}
}
