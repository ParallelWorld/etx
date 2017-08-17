package com.bj58.etx.core.async;

import java.util.List;
import java.util.Stack;
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
import com.bj58.etx.api.enums.EtxAsyncLogStateEnum;
import com.bj58.etx.api.enums.EtxSyncLogStateEnum;
import com.bj58.etx.api.enums.EtxTXStateEnum;
import com.bj58.etx.api.idempotent.IEtxQueryCheck;
import com.bj58.etx.core.cache.EtxAnnotationCache;
import com.bj58.etx.core.cache.EtxClassCache;
import com.bj58.etx.core.invoke.ComponetInvoker;
import com.bj58.etx.core.runtime.EtxRuntime;
import com.bj58.etx.core.util.EtxDaoUtil;
 
public class ProcessAsyncWorker {

	private static Log logger = LogFactory.getLog(ProcessAsyncWorker.class);
	private static ExecutorService fixedThreadPool = Executors.newFixedThreadPool(EtxRuntime.processThreadCount);

	/**
	 * 异步回滚（只执行一遍，未成功的留给task）
	 */
	public static void roolbackFromDB(final long txId, final IEtxContext ctx) throws Exception {
		fixedThreadPool.submit(new Runnable() {
			
			@Override
			public void run() {
				try {
					
					List<EtxSyncLog> logs = EtxDaoUtil.getPendingSyncLogList(txId);
					
					if (logs == null || logs.size() == 0) {
						// 如果没有需要执行的明细，则把事务组记录改为已结束
						EtxDaoUtil.updateTx(txId, EtxTXStateEnum.FINISH);
						return;
					}

					
					logger.info("------回滚事务组的同步组件,txId=" + txId);
					for (EtxSyncLog log : logs) {
						IEtxSyncComponet c = EtxClassCache.getInstance(log.getComponet());
						if(c==null){
							continue;
						}
						boolean b = ComponetInvoker.invokeCancel(c, ctx);
						if (b) {
							EtxDaoUtil.updateSyncLogState(txId,log.getId(), EtxSyncLogStateEnum.CANCEL_SUCCESS);
						} else {
							EtxDaoUtil.updateSyncLogState(txId,log.getId(), EtxSyncLogStateEnum.CANCEL_ERROR);
						}
					}
				} catch (Exception e) {
					logger.error("", e);
				}
			}
		});
	}
	
	/**
	 * 异步回滚
	 */
	public static void roolbackInProcess(final Stack<IEtxSyncComponet> stack, final IEtxContext ctx) throws Exception {
		fixedThreadPool.submit(new Runnable() {
			
			@Override
			public void run() {
				while (!stack.isEmpty()) {
					IEtxSyncComponet c = stack.pop();
					try {
						int repeat = 1;
						int count = 0;
						EtxRetry a = EtxAnnotationCache.getRetry(c);
						if (a != null) {
							repeat = a.repeat();
						}

						boolean b = false;

						while (count <= repeat) {
							b = ComponetInvoker.invokeCancel(c, ctx);
							if (b) {
								break;
							} else {
								boolean isAreadySuccess = true;
								if (a != null) {
									IEtxQueryCheck check = EtxClassCache.getInstance(a.condition());
									if (check != null) {
										isAreadySuccess = check.isAreadySuccess(ctx);
									}
								}

								// 如果幂等查询接口发现执行成功，则执行下一条
								if (isAreadySuccess) {
									break;
								}
								count++;
							}
						}
					} catch (Exception e) {
						logger.error("", e);
					}
				}
			}
		});
	}
	
	
	/**
	 * 执行异步任务
	 */
	public static void invokeAsyncInProcess(final List<IEtxAsyncComponet> asyncList, final IEtxContext ctx) {
		fixedThreadPool.submit(new Runnable() {
			@Override
			public void run() {
				for (IEtxAsyncComponet c : asyncList) {
					try {
						int repeat = 1;
						int count = 0;
						EtxRetry a = EtxAnnotationCache.getRetry(c);
						if (a != null) {
							repeat = a.repeat();
						}

						boolean b = false;

						while (count <= repeat) {
							b = ComponetInvoker.invokeAsyncService(c, ctx);
							if (b) {
								break;
							} else {
								boolean isAreadySuccess = true;
								if (a != null) {
									IEtxQueryCheck check = EtxClassCache.getInstance(a.condition());
									if (check != null) {
										isAreadySuccess = check.isAreadySuccess(ctx);
									}
								}

								// 如果幂等查询接口发现执行成功，则执行下一条
								if (isAreadySuccess) {
									break;
								}
								count++;
							}
						}
						
						// 最终执行失败会触发监控消息
						if (c instanceof IEtxMonitorAsyncComponet) {
							IEtxMonitorAsyncComponet mac = (IEtxMonitorAsyncComponet) c;
							ComponetInvoker.invokeAbsolutelyError(mac, ctx);
						}

					} catch (Exception e) {
						logger.error("", e);
					}
				}
			}
		});
	}
	
	/**
	 * 执行异步任务（只执行一遍，未成功的交给task）
	 */
	public static void invokeAsyncFromDB(long txId, final IEtxContext ctx) throws Exception {
		List<EtxAsyncLog> list = EtxDaoUtil.getPendingAsyncLogList(txId);

		if (list == null || list.size() == 0) {
			// 如果没有需要执行的明细，则把事务组记录改为已结束
			EtxDaoUtil.updateTx(txId, EtxTXStateEnum.FINISH);
			return;
		}

		logger.info("------执行事务组的异步组件,txId=" + txId);
		for (EtxAsyncLog log : list) {
			try {
				long logId = log.getId();
				String componetName = log.getComponet();
				IEtxAsyncComponet c = EtxClassCache.getInstance(componetName);
				if (c == null) {
					continue;
				}
				boolean b = ComponetInvoker.invokeAsyncService(c, ctx);
				if (b) {
					EtxDaoUtil.updateAsyncLogState(txId,logId, EtxAsyncLogStateEnum.SUCCESS);
				} else {
					EtxDaoUtil.updateAsyncLogState(txId,logId, EtxAsyncLogStateEnum.ERROR);
				}
			} catch (Exception e) {
				logger.error("", e);
			}
		}
	}
}
