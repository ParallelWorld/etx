package com.bj58.zhaopin.jianli.etx.core;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.bj58.zhaopin.jianli.etx.api.EtxService;
import com.bj58.zhaopin.jianli.etx.api.EtxTaskService;
import com.bj58.zhaopin.jianli.etx.api.context.IEtxContext;
import com.bj58.zhaopin.jianli.etx.api.runtime.EtxRuntime;
import com.bj58.zhaopin.jianli.etx.core.context.SCFV3Context;
import com.bj58.zhaopin.jianli.etx.core.dao.MysqlDao;
import com.bj58.zhaopin.jianli.etx.core.serialize.SCFV3Serializer;

public class JianliEtx {

	private static EtxService etxService = null;
	private static EtxTaskService etxTaskService = null;

	/**
	 * 用etx的方式执行业务逻辑
	 */
	public static EtxService newEtxService() {

		if (etxService != null) {
			return etxService;
		}

		Lock lock = new ReentrantLock();
		try {
			lock.lock();
			if (etxService != null) {
				return etxService;
			}
			EtxRuntime.setDao(new MysqlDao());
			EtxRuntime.setSerializer(new SCFV3Serializer());
			IEtxContext ctx = new SCFV3Context();
			etxService = new EtxService(ctx);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}

		return etxService;
	}

	/**
	 * 创建etx task任务
	 */
	public static EtxTaskService newEtxTaskService() {

		if (etxTaskService != null) {
			return etxTaskService;
		}

		Lock lock = new ReentrantLock();
		try {
			lock.lock();
			if (etxTaskService != null) {
				return etxTaskService;
			}
			EtxRuntime.setDao(new MysqlDao());
			EtxRuntime.setSerializer(new SCFV3Serializer());
			EtxRuntime.setInterval(5000);
			etxTaskService = new EtxTaskService();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}

		return etxTaskService;
	}
}
