package com.bj58.etx.core;

import com.bj58.etx.api.componet.IEtxAsyncComponet;
import com.bj58.etx.api.componet.IEtxComponet;
import com.bj58.etx.api.componet.IEtxSyncComponet;
import com.bj58.etx.api.componet.IEtxTCCComponet;
import com.bj58.etx.api.context.IEtxContext;
import com.bj58.etx.api.dto.IEtxDto;
import com.bj58.etx.api.enums.EtxRunMode;
import com.bj58.etx.api.enums.EtxSyncLogStateEnum;
import com.bj58.etx.api.enums.EtxTXStateEnum;
import com.bj58.etx.api.exception.EtxException;
import com.bj58.etx.api.vo.IEtxVo;
import com.bj58.etx.core.async.AsyncWorker;
import com.bj58.etx.core.cache.EtxClassCache;
import com.bj58.etx.core.heatbeat.EtxDBListener;
import com.bj58.etx.core.invoke.ComponetInvoker;
import com.bj58.etx.core.util.EtxDaoUtil;
import com.bj58.etx.core.util.EtxMonitorUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class EtxService {

	/**
	 * 事务组id，只有在非PROCESS模式下有效，即大于0
	 */
	private long txId;

	private IEtxContext ctx;
	private List<IEtxTCCComponet> tccList = new ArrayList<IEtxTCCComponet>();
	private List<IEtxSyncComponet> syncList = new ArrayList<IEtxSyncComponet>();
	private List<IEtxAsyncComponet> asyncList = new ArrayList<IEtxAsyncComponet>();
	private Stack<IEtxSyncComponet> stack = new Stack<IEtxSyncComponet>();
	// binLog的执行时间
	private long binLogNanos = 0;

	private static EtxDBListener listener = null;
	private static Log logger = LogFactory.getLog(EtxService.class);

	public EtxService(IEtxContext ctx) {
		this.ctx = ctx;

		if (listener == null) {
			synchronized (EtxService.class) {
				if (listener == null) {
					listener = new EtxDBListener();
					listener.setDaemon(true);
					listener.start();
					logger.info("------------DBListener 启动监听...");
				}
			}
		}
	}

	public EtxService addComponet(Class<? extends IEtxComponet> clazz) {
		IEtxComponet c = EtxClassCache.getInstance(clazz);
		if (c instanceof IEtxSyncComponet) {
			syncList.add((IEtxSyncComponet) c);
			if (c instanceof IEtxTCCComponet) {
				tccList.add((IEtxTCCComponet) c);
			}
		} else if (c instanceof IEtxAsyncComponet) {
			asyncList.add((IEtxAsyncComponet) c);
		}

		if ((syncList.size() + asyncList.size()) > 63) {
			throw new EtxException("componet must less than 64");
		}
		return this;
	}

	public EtxService setFlowType(String flowType) {
		ctx.setFlowType(flowType);
		return this;
	}

	public EtxService setRunMode(int mode) {
		if (EtxRunMode.byCode(mode) != EtxRunMode.PROCESS) {
			if (!listener.dbAlive()) {
				logger.warn("------------检测到db失效，强制修改运行模式为：" + EtxRunMode.PROCESS.getMsg());
				mode = EtxRunMode.PROCESS.getCode();
			}
		}
		ctx.setRunMode(mode);
		return this;
	}

	public EtxService setDto(Class<? extends IEtxDto> clazz) {
		ctx.setDto(clazz);
		return this;
	}

	public <D extends IEtxDto> EtxService setDto(D d) {
		ctx.setDto(d);
		return this;
	}

	public <V extends IEtxVo> EtxService setVo(V v) {
		ctx.setVo(v);
		return this;
	}

	public EtxService setVo(Class<? extends IEtxVo> clazz) {
		ctx.setVo(clazz);
		return this;
	}

	public boolean invoke(Object... params) {

		long invokeBegin = System.currentTimeMillis();
		EtxMonitorUtil.txTotal();
		init(params);
		try {
			
			// 1.插入事务记录
			if (ctx.getRunMode() != EtxRunMode.PROCESS.getCode()) {
				long begin = System.nanoTime();
				txId = EtxDaoUtil.insertTx(ctx.getFlowType());
				long end = System.nanoTime();
				binLogNanos = binLogNanos + (end - begin);
				logger.info("------事务组记录插入成功,txId=" + txId + ",flowType=" + ctx.getFlowType());
			}
			
			// 2.执行所有同步组件
			boolean result = invokeSyncComponents();

			if (result) {
				if (txId > 0) {
					
					long begin = System.nanoTime();
					// 3.插入异步日志
					commitAsyncComponents();
					// 4.标记事务组状态为同步成功
					EtxDaoUtil.updateTx(txId, EtxTXStateEnum.SYNCSUCCESS);
					long end = System.nanoTime();
					binLogNanos = binLogNanos + (end - begin);
					logger.info("------修改事务组状态为SYNCSUCCESS,txId=" + txId + ",flowType=" + ctx.getFlowType());
				}

				EtxMonitorUtil.syncSuccess();
				
				// 5.执行所有异步组件
				invokeAsyncComponents();
				
				long invokeEnd = System.currentTimeMillis();
				logger.info("------BinLog执行时间=" + (binLogNanos / 1000 / 1000) + "ms,txId=" + txId + ",flowType=" + ctx.getFlowType());
				logger.info("------Etx总执行时间=" + (invokeEnd - invokeBegin) + "ms,txId=" + txId + ",flowType=" + ctx.getFlowType());
				return true;
			} else {
				rollback();
				EtxMonitorUtil.syncFail();
				return false;
			}
		} catch (Throwable e) {
			logger.error("etx invoke error", e);
			throw new EtxException(e);
		}
	}

	private void init(Object... params) {
		if (ctx.getFlowType() == null || ctx.getFlowType().equals("")) {
			throw new EtxException("flowType 不能为空!");
		}

		if (EtxRunMode.byCode(ctx.getRunMode()) == null) {
			throw new EtxException("runMode 错误");
		}

		ctx.initParams(params);
	}

	/**
	 * 提交所有同步组件
	 */
	private boolean invokeSyncComponents() {
		// 1.执行所有tcc的try
		for (IEtxTCCComponet c : tccList) {
			boolean b = ComponetInvoker.invokeTry(c, ctx);
			if (!b) {
				return false;
			}
		}

		// 2.执行所有sync组件的confirm
		for (IEtxSyncComponet c : syncList) {
			boolean b = invokeConfirm(c);
			if (!b) {
				return false;
			}
		}

		if (EtxRunMode.byCode(ctx.getRunMode()) != EtxRunMode.BINLOG) {
			stack.clear();
		}

		return true;
	}

	/**
	 * 提交所有异步组件
	 */
	private void commitAsyncComponents() throws Exception {
		if (txId > 0) {
			// 写入异步组件日志
			for (IEtxAsyncComponet c : asyncList) {
				EtxDaoUtil.insertAsyncLog(c, ctx, txId);
			}
		}
	}
	
	/**
	 * 提交所有异步组件
	 */
	private void invokeAsyncComponents() throws Exception {
		if (txId > 0) {
			// 异步执行所有任务
			AsyncWorker.invokeAsyncFromDB(txId, ctx);
		} else {
			// 进程模式下用线程池执行异步任务
			AsyncWorker.invokeAsyncInProcess(asyncList, ctx);
		}
	}

	private void rollback() throws Exception {
		if (EtxRunMode.byCode(ctx.getRunMode()) == EtxRunMode.BINLOG) {
			logger.error("------事务执行失败,txId=" + txId + ",flowType=" + ctx.getFlowType());
			AsyncWorker.roolbackFromDB(txId, ctx);
		} else {
			AsyncWorker.roolbackInProcess(stack, ctx);
		}
	}

	/**
	 * 执行所有confirm
	 */
	private boolean invokeConfirm(IEtxSyncComponet c) {
		try {
			long logId = 0;
			// 1. binLog模式先记录日志(默认成功)
			if (EtxRunMode.byCode(ctx.getRunMode()) == EtxRunMode.BINLOG) {
				long begin = System.nanoTime();
				logId = EtxDaoUtil.insertSyncLog(c, ctx, txId, EtxSyncLogStateEnum.CONFIRM_SUCCESS);
				long end = System.nanoTime();
				binLogNanos = binLogNanos + (end - begin);
			} else {
				stack.push(c);
			}
			// 2.执行业务方法
			boolean b = ComponetInvoker.invokeConfirm(c, ctx);
			
			if (logId > 0) {
				if (!b) {
					// 3.执行失败修改组件状态
					long begin = System.nanoTime();
					EtxDaoUtil.updateSyncLogState(txId, logId, EtxSyncLogStateEnum.CONFIRM_ERROR);
					long end = System.nanoTime();
					binLogNanos = binLogNanos + (end - begin);
				}
			}
			return b;
		} catch (Throwable e) {
			logger.error("", e);
			return false;
		}
	}
}
