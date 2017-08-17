package com.bj58.etx.core;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bj58.etx.api.annotation.EtxRetry;
import com.bj58.etx.api.componet.IEtxAsyncComponet;
import com.bj58.etx.api.componet.IEtxMonitorAsyncComponet;
import com.bj58.etx.api.componet.IEtxSyncComponet;
import com.bj58.etx.api.context.IEtxContext;
import com.bj58.etx.api.db.EtxAsyncLog;
import com.bj58.etx.api.db.EtxSyncLog;
import com.bj58.etx.api.db.EtxTX;
import com.bj58.etx.api.enums.EtxAsyncLogStateEnum;
import com.bj58.etx.api.enums.EtxSyncLogStateEnum;
import com.bj58.etx.api.enums.EtxTXStateEnum;
import com.bj58.etx.api.exception.EtxException;
import com.bj58.etx.api.idempotent.IEtxQueryCheck;
import com.bj58.etx.api.serialize.IEtxSerializer;
import com.bj58.etx.core.cache.EtxAnnotationCache;
import com.bj58.etx.core.cache.EtxClassCache;
import com.bj58.etx.core.invoke.ComponetInvoker;
import com.bj58.etx.core.runtime.EtxRuntime;
import com.bj58.etx.core.util.EtxDaoUtil;

public class EtxTaskService {

	static ExecutorService taskThreadPool = Executors.newFixedThreadPool(EtxRuntime.taskThreadCount);
	static IEtxSerializer ser = EtxRuntime.serializer;
	private static Log logger = LogFactory.getLog(EtxTaskService.class);

	public void start() {
		logger.info("------etx task 启动....");
		for (;;) {
			try {
				Thread.sleep(EtxRuntime.taskLoopTxInterval);
			} catch (InterruptedException e) {
				logger.error("", e);
			}
			execute();
		}
	}

	/**
	 * 单次执行
	 */
	public void execute() {
		try {
			List<EtxTX> list1 = EtxDaoUtil.getPendingTxList(EtxTXStateEnum.ERROR, EtxRuntime.countForCancelOnce);
			int size1 = list1 == null ? 0 : list1.size();
			logger.info("------查询到同步事务组记录条数 = " + size1);
			if (list1 != null) {
				for (EtxTX tx : list1) {
					final long txId = tx.getId();
					taskThreadPool.execute(new Runnable() {
						@Override
						public void run() {
							try {
								handSyncLogs(txId);
							} catch (Exception e) {
								logger.error("", e);
							}
						}
					});
				}
			}

			List<EtxTX> list2 = EtxDaoUtil.getPendingTxList(EtxTXStateEnum.SYNCSUCCESS, EtxRuntime.countForDoAsyncOnce);
			int size2 = list2 == null ? 0 : list2.size();
			logger.info("------查询到异步事务组记录条数 = " + size2);

			if (list2 != null) {
				for (EtxTX tx : list2) {
					final long txId = tx.getId();
					taskThreadPool.execute(new Runnable() {
						@Override
						public void run() {
							try {
								handAsyncLogs(txId);
							} catch (Exception e) {
								logger.error("", e);
							}
						}
					});
				}
			}
		} catch (Exception e) {
			logger.error("", e);
			throw new EtxException(e);
		}
	}

	private void handSyncLogs(long txId) throws Exception {
		List<EtxSyncLog> list = EtxDaoUtil.getPendingSyncLogList(txId);
		int size = list == null ? 0 : list.size();
		if (size == 0) {
			// 如果没有需要执行的明细，则把事务组记录改为已结束
			EtxDaoUtil.updateTx(txId, EtxTXStateEnum.FINISH);
			return;
		}

		for (EtxSyncLog log : list) {
			long logId = log.getId();
			String componetName = log.getComponet();
			IEtxSyncComponet sync = EtxClassCache.getInstance(componetName);
			if (sync == null) {
				// 这个类可能是换名了，此条事务记录没有存在的意义的了
				EtxDaoUtil.updateSyncLogState(txId, logId, EtxSyncLogStateEnum.CLOSE);
				continue;
			}

			IEtxContext ctx = ser.deSerialize(log.getData());

			int count = 1;
			EtxRetry a = EtxAnnotationCache.getRetry(sync);
			boolean isAreadySuccess = false;

			if (a != null) {
				count = a.repeat();
				IEtxQueryCheck check = EtxClassCache.getInstance(a.condition());
				if (check != null) {
					isAreadySuccess = check.isAreadySuccess(ctx);
				}
			}

			// 如果幂等查询接口发现执行成功，或者执行次数已达到设定上限，则关闭该条log
			if (isAreadySuccess || log.getCancelCount() > count) {
				EtxDaoUtil.updateSyncLogState(txId, logId, EtxSyncLogStateEnum.CLOSE);
				continue;
			}

			logger.info("------获取到同步log,log.id=" + log.getId() + ",log.addtime=" + log.getAddTime() + ",log.count=" + log.getCancelCount() + ",repeat=" + count);
			boolean b = ComponetInvoker.invokeCancel(sync, ctx);
			if (b) {
				EtxDaoUtil.updateSyncLogState(txId, logId, EtxSyncLogStateEnum.CANCEL_SUCCESS);
			} else {
				EtxDaoUtil.updateSyncLogState(txId, logId, EtxSyncLogStateEnum.CANCEL_ERROR);
			}
		}

	}

	private void handAsyncLogs(long txId) throws Exception {
		List<EtxAsyncLog> list = EtxDaoUtil.getPendingAsyncLogList(txId);
		int size = list == null ? 0 : list.size();
		if (size == 0) {
			// 如果没有需要执行的明细，则把事务组记录改为已结束
			EtxDaoUtil.updateTx(txId, EtxTXStateEnum.FINISH);
			return;
		}

		for (EtxAsyncLog log : list) {
			long logId = log.getId();
			String componetName = log.getComponet();
			IEtxAsyncComponet async = EtxClassCache.getInstance(componetName);
			if (async == null) {
				// 这个类可能是换名了，此条事务记录没有存在的意义的了
				EtxDaoUtil.updateAsyncLogState(txId, logId, EtxAsyncLogStateEnum.CLOSE);
				continue;
			}

			IEtxContext ctx = ser.deSerialize(log.getData());

			int count = 1;
			EtxRetry a = EtxAnnotationCache.getRetry(async);
			boolean isAreadySuccess = true;
			if (a != null) {
				count = a.repeat();
				IEtxQueryCheck check = EtxClassCache.getInstance(a.condition());
				if (check != null) {
					isAreadySuccess = check.isAreadySuccess(ctx);
				}
			}

			// 如果幂等查询接口发现执行成功，则认为是成功
			if (isAreadySuccess) {
				EtxDaoUtil.updateAsyncLogState(txId, logId, EtxAsyncLogStateEnum.SUCCESS);
				continue;
			}

			// 如果执行次数达到上限，则关闭此条日志
			if (log.getCount() > count) {
				EtxDaoUtil.updateAsyncLogState(txId, logId, EtxAsyncLogStateEnum.CLOSE);
				if (async instanceof IEtxMonitorAsyncComponet) {
					IEtxMonitorAsyncComponet mac = (IEtxMonitorAsyncComponet) async;
					mac.onAbsolutelyError(ctx);
				}
				continue;
			}

			logger.info("------获取到异步log,log.id=" + log.getId() + ",log.addtime=" + log.getAddTime() + ",log.count=" + log.getCount() + ",repeat=" + count);
			boolean b = ComponetInvoker.invokeAsyncService(async, ctx);
			if (b) {
				EtxDaoUtil.updateAsyncLogState(txId, logId, EtxAsyncLogStateEnum.SUCCESS);
			} else {
				EtxDaoUtil.updateAsyncLogState(txId, logId, EtxAsyncLogStateEnum.ERROR);
			}
		}
	}
}
