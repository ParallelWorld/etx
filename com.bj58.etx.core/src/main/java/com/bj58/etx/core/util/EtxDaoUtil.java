package com.bj58.etx.core.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bj58.etx.api.componet.IEtxAsyncComponet;
import com.bj58.etx.api.componet.IEtxSyncComponet;
import com.bj58.etx.api.componet.IEtxTCCComponet;
import com.bj58.etx.api.context.IEtxContext;
import com.bj58.etx.api.db.EtxAsyncLog;
import com.bj58.etx.api.db.EtxSyncLog;
import com.bj58.etx.api.db.EtxTX;
import com.bj58.etx.api.db.IEtxDao;
import com.bj58.etx.api.enums.EtxAsyncLogStateEnum;
import com.bj58.etx.api.enums.EtxSyncLogStateEnum;
import com.bj58.etx.api.enums.EtxTXStateEnum;
import com.bj58.etx.api.serialize.IEtxSerializer;
import com.bj58.etx.core.cache.EtxClassCache;
import com.bj58.etx.core.heatbeat.EtxDBListener;
import com.bj58.etx.core.runtime.EtxRuntime;

public class EtxDaoUtil {

	private static IEtxDao dao = EtxRuntime.dao;
	private static IEtxSerializer ser = EtxRuntime.serializer;
	private static Log log = LogFactory.getLog(EtxDaoUtil.class);

	public static long insertTx(String flowType, EtxTXStateEnum state) throws Exception {
		try {
			long now = EtxDateTimeUtil.getFullTimestamp();
			EtxTX tx = new EtxTX();
			tx.setAddTime(now);
			tx.setModifyTime(now);
			tx.setFlowType(flowType);
			tx.setState(state.getCode());
			return dao.insertTx(tx);
		} catch (Exception e) {
			log.error("", e);
			EtxDBListener.incFail();
			throw e;
		}
	}

	public static long insertTx(String flowType) throws Exception {
		return insertTx(flowType, EtxTXStateEnum.ERROR);
	}

	public static void updateTx(long txId, EtxTXStateEnum state) throws Exception {
		EtxTX tx = dao.getTxById(txId);
		if (tx == null) {
			return;
		}
		tx.setModifyTime(EtxDateTimeUtil.getFullTimestamp());
		tx.setState(state.getCode());
		dao.updateTx(tx);
	}

	/**
	 * 获取全部待处理的事务组Log
	 */
	public static List<EtxTX> getPendingTxList(EtxTXStateEnum state, int pageSize) throws Exception {
		return dao.getTxList(state, pageSize);
	}

	/**
	 * 获取待处理的log列表
	 */
	public static List<EtxAsyncLog> getPendingAsyncLogList(long txId) throws Exception {
		List<EtxAsyncLog> list = dao.getAsyncLogList(txId);
		List<EtxAsyncLog> result = new ArrayList<EtxAsyncLog>();

		if (list != null && list.size() > 0) {
			for (EtxAsyncLog log : list) {
				if (EtxAsyncLogStateEnum.ERROR.name().equals(log.getState()) || EtxAsyncLogStateEnum.INIT.name().equals(log.getState())) {
					result.add(log);
				}
			}
		}
		return result;
	}

	/**
	 * 获取待处理的log列表
	 */
	public static List<EtxSyncLog> getPendingSyncLogList(long txId) throws Exception {
		List<EtxSyncLog> list = dao.getSyncLogList(txId);
		List<EtxSyncLog> result = new ArrayList<EtxSyncLog>();

		if (list != null && list.size() > 0) {
			for (EtxSyncLog log : list) {
				if (EtxSyncLogStateEnum.CANCEL_ERROR.name().equals(log.getState()) || EtxSyncLogStateEnum.TRY_SUCCESS.name().equals(log.getState())
						|| EtxSyncLogStateEnum.CONFIRM_SUCCESS.name().equals(log.getState())) {
					result.add(log);
				} else if (EtxSyncLogStateEnum.CONFIRM_ERROR.name().equals(log.getState())) {
					if (EtxClassCache.getInstance(log.getComponet()) instanceof IEtxTCCComponet) {
						result.add(log);
					}
				}
			}
		}
		return result;
	}

	public static long insertSyncLog(IEtxSyncComponet c, IEtxContext ctx, long txId, EtxSyncLogStateEnum state) throws Exception {
		long now = EtxDateTimeUtil.getFullTimestamp();
		EtxSyncLog log = new EtxSyncLog();
		log.setAddTime(now);
		log.setComponet(c.getClass().getName());
		log.setCancelCount(0);
		log.setState(state.name());
		log.setData(ser.serialize(ctx));
		log.setModifyTime(now);
		log.setTxId(txId);
		return dao.insertSyncLog(log);
	}

	public static void updateSyncLogState(long txId, long logId, EtxSyncLogStateEnum state) throws Exception {
		EtxSyncLog log = dao.getSyncLogById(txId, logId);
		if (log == null) {
			return;
		}

		if (EtxSyncLogStateEnum.CANCEL_SUCCESS == state || EtxSyncLogStateEnum.CANCEL_ERROR == state) {
			log.setCancelCount(log.getCancelCount() + 1);
		}

		log.setModifyTime(EtxDateTimeUtil.getFullTimestamp());
		log.setState(state.name());
		dao.updateSyncLog(log);
	}

	public static long insertAsyncLog(IEtxAsyncComponet c, IEtxContext ctx, long txId) throws Exception {
		long now = EtxDateTimeUtil.getFullTimestamp();
		EtxAsyncLog log = new EtxAsyncLog();
		log.setAddTime(now);
		log.setComponet(c.getClass().getName());
		log.setCount(0);
		log.setData(ser.serialize(ctx));
		log.setModifyTime(now);
		log.setState(EtxAsyncLogStateEnum.INIT.name());
		log.setTxId(txId);
		return dao.insertAsyncLog(log);
	}

	public static void updateAsyncLogState(long txId, long logId, EtxAsyncLogStateEnum state) throws Exception {
		EtxAsyncLog log = dao.getAsyncLogById(txId, logId);
		if (log == null) {
			return;
		}

		if (state != EtxAsyncLogStateEnum.CLOSE) {
			log.setCount(log.getCount() + 1);
		}

		log.setModifyTime(EtxDateTimeUtil.getFullTimestamp());
		log.setState(state.name());
		dao.updateAsyncLog(log);
	}
}
