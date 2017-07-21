package com.bj58.zhaopin.jianli.etx.api;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.bj58.zhaopin.jianli.etx.api.annotation.EtxRetry;
import com.bj58.zhaopin.jianli.etx.api.cache.EtxAnnotationCache;
import com.bj58.zhaopin.jianli.etx.api.cache.EtxClassCache;
import com.bj58.zhaopin.jianli.etx.api.componet.IEtxAsyncComponet;
import com.bj58.zhaopin.jianli.etx.api.componet.IEtxSyncComponet;
import com.bj58.zhaopin.jianli.etx.api.context.IEtxContext;
import com.bj58.zhaopin.jianli.etx.api.db.EtxAsyncLog;
import com.bj58.zhaopin.jianli.etx.api.db.EtxSyncLog;
import com.bj58.zhaopin.jianli.etx.api.db.EtxTX;
import com.bj58.zhaopin.jianli.etx.api.enums.EtxAsyncLogStateEnum;
import com.bj58.zhaopin.jianli.etx.api.enums.EtxSyncLogStateEnum;
import com.bj58.zhaopin.jianli.etx.api.enums.EtxTXStateEnum;
import com.bj58.zhaopin.jianli.etx.api.exception.EtxException;
import com.bj58.zhaopin.jianli.etx.api.idempotent.IEtxQueryCheck;
import com.bj58.zhaopin.jianli.etx.api.runtime.EtxRuntime;
import com.bj58.zhaopin.jianli.etx.api.serialize.IEtxSerializer;
import com.bj58.zhaopin.jianli.etx.api.util.EtxDaoUtil;

public class EtxTaskService {

	static ExecutorService fixedThreadPool = Executors.newFixedThreadPool(8);
	static IEtxSerializer ser = EtxRuntime.serializer;

	public void start() {
		
		while (true) {
			try {
				
				Thread.sleep(EtxRuntime.interval);
				
				List<EtxTX> list1 = EtxDaoUtil.getPendingTxList(EtxTXStateEnum.ERROR, 10000);

				if (list1 != null) {
					for (EtxTX tx : list1) {
						final long txId = tx.getId();
						fixedThreadPool.execute(new Runnable() {
							@Override
							public void run() {
								try {
									handSyncLogs(txId);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
					}
				}

				List<EtxTX> list2 = EtxDaoUtil.getPendingTxList(EtxTXStateEnum.SYNCSUCCESS, 10000);
				if (list2 != null) {
					for (EtxTX tx : list2) {
						final long txId = tx.getId();
						fixedThreadPool.execute(new Runnable() {
							@Override
							public void run() {
								try {
									handAsyncLogs(txId);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
					}
				}
			} catch (Exception e) {
				throw new EtxException(e);
			}
		}
	}

	private void handSyncLogs(long txId) throws Exception {
		List<EtxSyncLog> logs = EtxDaoUtil.getPendingSyncLogList(txId);
		if (logs == null || logs.size() == 0) {
			// 如果没有需要执行的明细，则把事务组记录改为已结束
			EtxDaoUtil.updateTx(txId, EtxTXStateEnum.FINISH);
		}

		for (EtxSyncLog log : logs) {
			long logId = log.getId();
			String componetName = log.getComponet();
			IEtxSyncComponet sync = EtxClassCache.getInstance(componetName);
			if (sync == null) {
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
				EtxDaoUtil.updateSyncLogState(logId, EtxSyncLogStateEnum.CLOSE);
				continue;
			}

			try {
				boolean b = sync.doCancel(ctx);
				if (b) {
					EtxDaoUtil.updateSyncLogState(logId, EtxSyncLogStateEnum.CANCEL_SUCCESS);
				} else {
					EtxDaoUtil.updateSyncLogState(logId, EtxSyncLogStateEnum.CANCEL_ERROR);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private void handAsyncLogs(long txId) throws Exception {
		List<EtxAsyncLog> list = EtxDaoUtil.getPendingAsyncLogList(txId);

		if (list == null || list.size() == 0) {
			// 如果没有需要执行的明细，则把事务组记录改为已结束
			EtxDaoUtil.updateTx(txId, EtxTXStateEnum.FINISH);
		}

		for (EtxAsyncLog log : list) {
			long logId = log.getId();
			String componetName = log.getComponet();
			IEtxAsyncComponet async = EtxClassCache.getInstance(componetName);
			if (async == null) {
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

			// 如果幂等查询接口发现执行成功，或者执行次数已达到设定上限，则关闭该条明细
			if (isAreadySuccess || log.getCount() > count) {
				EtxDaoUtil.updateAsyncLogState(logId, EtxAsyncLogStateEnum.CLOSE);
				continue;
			}

			try {
				boolean b = async.doService(ctx);
				if (b) {
					EtxDaoUtil.updateAsyncLogState(logId, EtxAsyncLogStateEnum.SUCCESS);
				} else {
					EtxDaoUtil.updateAsyncLogState(logId, EtxAsyncLogStateEnum.ERROR);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
