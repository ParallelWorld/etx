package com.bj58.etx.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
import com.bj58.etx.core.async.ProcessAsyncWorker;
import com.bj58.etx.core.cache.EtxClassCache;
import com.bj58.etx.core.heatbeat.EtxDBListener;
import com.bj58.etx.core.invoke.ComponetInvoker;
import com.bj58.etx.core.util.EtxDaoUtil;

public class EtxService {

	private IEtxContext ctx;
	private List<IEtxTCCComponet> tccList = new ArrayList<IEtxTCCComponet>();
	private List<IEtxSyncComponet> syncList = new ArrayList<IEtxSyncComponet>();
	private List<IEtxAsyncComponet> asyncList = new ArrayList<IEtxAsyncComponet>();
	private Stack<IEtxSyncComponet> stack = new Stack<IEtxSyncComponet>();
	Map<IEtxSyncComponet, Long> logIdMap = new HashMap<IEtxSyncComponet, Long>();
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
		init(params);
		try {
			long txId = 0;
			if (EtxRunMode.byCode(ctx.getRunMode()) != EtxRunMode.PROCESS) {
				txId = EtxDaoUtil.insertTx(ctx.getFlowType());
				logger.info("------事务组记录插入成功,txId=" + txId);

				// binLog模式先记录日志
				if (EtxRunMode.byCode(ctx.getRunMode()) == EtxRunMode.BINLOG) {
					for (IEtxSyncComponet c : syncList) {
						long logId = EtxDaoUtil.insertSyncLog(c, ctx, txId, EtxSyncLogStateEnum.TRY_SUCCESS);
						logIdMap.put(c, logId);
					}
				}

				// 写入异步组件日志
				for (IEtxAsyncComponet c : asyncList) {
					EtxDaoUtil.insertAsyncLog(c, ctx, txId);
				}
			}

			boolean result = commitSyncComponets(txId);

			if (result) {
				if (txId > 0) {
					// 标记事务组状态为同步成功
					EtxDaoUtil.updateTx(txId, EtxTXStateEnum.SYNCSUCCESS);
					// 异步执行所有任务
					ProcessAsyncWorker.invokeAsyncFromDB(txId, ctx);
				} else {
					// 进程模式下用线程池执行异步任务
					ProcessAsyncWorker.invokeAsyncInProcess(asyncList, ctx);
				}

				if (ctx.getVo() != null) {
					ctx.getVo().setSuccess(true);
				}

				return true;
			} else {
				if (EtxRunMode.byCode(ctx.getRunMode()) == EtxRunMode.BINLOG) {
					ProcessAsyncWorker.roolbackFromDB(txId, ctx);
				} else {
					ProcessAsyncWorker.roolbackInProcess(stack, ctx);
				}
				return false;
			}
		} catch (Exception e) {
			logger.error("", e);
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
	private boolean commitSyncComponets(long txId) throws Exception {

		// 1.执行所有tcc的try
		for (IEtxTCCComponet c : tccList) {
			boolean b = invokeTry(c, txId);
			if (!b) {
				return false;
			}
		}

		if (EtxRunMode.byCode(ctx.getRunMode()) != EtxRunMode.BINLOG) {
			stack.clear();
		}

		// 2.执行所有sync组件的confirm
		for (IEtxSyncComponet c : syncList) {
			boolean b = invokeConfirm(c, txId);
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
	 * 执行try
	 */
	private boolean invokeTry(IEtxTCCComponet c, long txId) throws Exception {

		boolean b = ComponetInvoker.invokeTry(c, ctx);

		Long logId = logIdMap.get(c);

		if (logId != null && logId > 0) {
			if (!b) {
				EtxDaoUtil.updateSyncLogState(txId, logId, EtxSyncLogStateEnum.TRY_ERROR);
			}
		} else {
			// 非binlog模式 执行成功，推入回滚栈中
			if (b) {
				stack.push(c);
			}
		}

		return b;
	}

	/**
	 * 执行所有confirm
	 */
	private boolean invokeConfirm(IEtxSyncComponet c, long txId) throws Exception {
		Long logId = logIdMap.get(c);

		if (logId != null && logId > 0) {
			EtxDaoUtil.updateSyncLogState(txId, logId, EtxSyncLogStateEnum.CONFIRM_SUCCESS);
		}

		boolean b = ComponetInvoker.invokeConfirm(c, ctx);

		if (logId > 0) {
			if (!b) {
				EtxDaoUtil.updateSyncLogState(txId, logId, EtxSyncLogStateEnum.CONFIRM_ERROR);
			}
		} else {
			// 非binlog模式 执行成功，推入回滚栈中
			if (b) {
				stack.push(c);
			}
		}
		return b;
	}
}
