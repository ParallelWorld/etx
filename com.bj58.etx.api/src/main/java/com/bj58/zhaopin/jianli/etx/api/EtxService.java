package com.bj58.zhaopin.jianli.etx.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.bj58.zhaopin.jianli.etx.api.cache.EtxClassCache;
import com.bj58.zhaopin.jianli.etx.api.componet.IEtxAsyncComponet;
import com.bj58.zhaopin.jianli.etx.api.componet.IEtxComponet;
import com.bj58.zhaopin.jianli.etx.api.componet.IEtxSyncComponet;
import com.bj58.zhaopin.jianli.etx.api.componet.IEtxTCCComponet;
import com.bj58.zhaopin.jianli.etx.api.context.IEtxContext;
import com.bj58.zhaopin.jianli.etx.api.db.EtxSyncLog;
import com.bj58.zhaopin.jianli.etx.api.dto.IEtxDto;
import com.bj58.zhaopin.jianli.etx.api.enums.EtxSyncLogStateEnum;
import com.bj58.zhaopin.jianli.etx.api.enums.EtxTXStateEnum;
import com.bj58.zhaopin.jianli.etx.api.exception.EtxException;
import com.bj58.zhaopin.jianli.etx.api.util.EtxDaoUtil;
import com.bj58.zhaopin.jianli.etx.api.vo.IEtxVo;

public class EtxService {

	public EtxService(IEtxContext ctx) {
		this.ctx = ctx;
	}

	private IEtxContext ctx;
	private List<IEtxTCCComponet> tccList = new ArrayList<IEtxTCCComponet>();
	private List<IEtxSyncComponet> syncList = new ArrayList<IEtxSyncComponet>();
	private List<IEtxAsyncComponet> asyncList = new ArrayList<IEtxAsyncComponet>();
	private Stack<IEtxSyncComponet> stack = new Stack<IEtxSyncComponet>();
	Map<IEtxSyncComponet, Long> logIdMap = new HashMap<IEtxSyncComponet, Long>();

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
		return this;
	}

	public EtxService setBinLogMode(boolean mode) {
		ctx.setBinLogMode(mode);
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
			long txId = EtxDaoUtil.insertTx();

			boolean result = commitSyncComponets(txId);

			if (result) {
				// 写入异步组件日志
				for (IEtxAsyncComponet c : asyncList) {
					EtxDaoUtil.insertAsyncLog(c, ctx, txId);
				}
				return true;
			} else {
				roolbackSyncComponets(txId);
				return false;
			}
		} catch (Exception e) {
			throw new EtxException(e);
		}
	}

	/**
	 * ctx 初始化
	 */
	private void init(Object... params) {
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

		if (!ctx.isBinLogMode()) {
			stack.clear();
		}

		// 2.执行所有sync组件的confirm
		for (IEtxSyncComponet c : syncList) {
			boolean b = invokeConfirm(c, txId);
			if (!b) {
				return false;
			}
		}

		if (!ctx.isBinLogMode()) {
			stack.clear();
		}

		// 标记事务组状态为同步成功
		EtxDaoUtil.updateTx(txId, EtxTXStateEnum.SYNCSUCCESS);
		return true;
	}

	/**
	 * 回滚所有同步组件
	 */
	private void roolbackSyncComponets(long txId) throws Exception {
		if (ctx.isBinLogMode()) {
			List<EtxSyncLog> logs = EtxDaoUtil.getPendingSyncLogList(txId);
			if (logs != null) {
				for (EtxSyncLog log : logs) {
					boolean b = false;
					IEtxSyncComponet c = EtxClassCache.getInstance(log.getComponet());
					try {
						b = c.doConfirm(ctx);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (b) {
						EtxDaoUtil.updateSyncLogState(log.getId(), EtxSyncLogStateEnum.CANCEL_SUCCESS);
					} else {
						EtxDaoUtil.updateSyncLogState(log.getId(), EtxSyncLogStateEnum.CANCEL_ERROR);
					}
				}
			}
		} else {
			while (!stack.isEmpty()) {
				IEtxSyncComponet c = stack.pop();
				try {
					c.doConfirm(ctx);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 执行try
	 */
	private boolean invokeTry(IEtxTCCComponet c, long txId) throws Exception {

		long logId = 0;
		if (ctx.isBinLogMode()) {
			// 写入sync log
			logId = EtxDaoUtil.insertSyncLog(c, ctx, txId, EtxSyncLogStateEnum.TRY_SUCCESS);
			logIdMap.put(c, logId);
		}

		boolean b = false;
		try {
			b = c.doTry(ctx);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!b) {
			if (!ctx.isBinLogMode()) {
				stack.push(c);
			} else {
				EtxDaoUtil.updateSyncLogState(logId, EtxSyncLogStateEnum.TRY_ERROR);
			}
		}

		return b;
	}

	/**
	 * 执行所有confirm
	 */
	private boolean invokeConfirm(IEtxSyncComponet c, long txId) throws Exception {
		long logId = 0;
		if (logIdMap.get(c) != null) {
			logId = logIdMap.get(c).longValue();
		}
		if (ctx.isBinLogMode()) {
			if (logId == 0) {
				logId = EtxDaoUtil.insertSyncLog(c, ctx, txId, EtxSyncLogStateEnum.CONFIRM_SUCCESS);
				logIdMap.put(c, logId);
			} else {
				EtxDaoUtil.updateSyncLogState(logId, EtxSyncLogStateEnum.CONFIRM_SUCCESS);
			}
		}

		boolean b = false;
		try {
			b = c.doConfirm(ctx);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!b) {
			if (!ctx.isBinLogMode()) {
				stack.push(c);
			} else {
				EtxDaoUtil.updateSyncLogState(logId, EtxSyncLogStateEnum.CONFIRM_ERROR);
			}
		}
		return b;
	}
}
