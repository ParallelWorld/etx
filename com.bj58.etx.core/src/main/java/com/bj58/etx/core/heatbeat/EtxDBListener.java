package com.bj58.etx.core.heatbeat;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bj58.etx.api.enums.EtxTXStateEnum;
import com.bj58.etx.core.runtime.EtxRuntime;
import com.bj58.etx.core.util.EtxDaoUtil;

/**
 * 监听DB状态
 * 
 * @author shencl
 */
public class EtxDBListener extends Thread {

	private static AtomicInteger counter = new AtomicInteger(0);
	private static volatile boolean alive = true;
	private static String HB_FLOW_TYPE = "HEAT_BEAT_FLOW_TYPE";
	private static Log log = LogFactory.getLog(EtxDBListener.class);

	/**
	 * 5分钟检测一次DB是否存活
	 */
	@Override
	public void run() {
		for (;;) {
			try {
				Thread.sleep(EtxRuntime.dbHeatBeatInterval);
			} catch (InterruptedException e) {
				//
			}

			alive = testAlive();
			if (alive) {
				counter.set(0);
			} else {
				log.warn("db is dead!");
			}
		}
	}

	/**
	 * 返回db是否存活
	 */
	public boolean dbAlive() {
		boolean result = (counter.intValue() <= EtxRuntime.dbMaxFailCount) && alive;
		
		if(!result){
			log.warn("counter.intValue:" + counter.intValue());
			log.warn("EtxRuntime.dbMaxFailCount:" + EtxRuntime.dbMaxFailCount);
			log.warn("db.alive:" + alive);
		}
		return result;
	}

	/**
	 * 错误自增
	 */
	public static void incFail() {
		counter.incrementAndGet();
	}

	/**
	 * 检测存活就是插入一条特殊的事务记录，如果能插入成功，则认为DB是存活的
	 */
	private boolean testAlive() {
		try {
			long txId = EtxDaoUtil.insertTx(HB_FLOW_TYPE,EtxTXStateEnum.FINISH);
			if (txId > 0) {
				return true;
			}
		} catch (Exception e) {
			log.error("", e);
		}
		return false;
	}

}
