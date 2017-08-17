package com.bj58.etx.core.invoke;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bj58.etx.api.componet.IEtxAsyncComponet;
import com.bj58.etx.api.componet.IEtxMonitorAsyncComponet;
import com.bj58.etx.api.componet.IEtxSyncComponet;
import com.bj58.etx.api.componet.IEtxTCCComponet;
import com.bj58.etx.api.context.IEtxContext;

public class ComponetInvoker {

	private static Log logger = LogFactory.getLog(ComponetInvoker.class);

	public static boolean invokeTry(IEtxTCCComponet c, IEtxContext ctx) throws Exception {
		boolean b = c.doTry(ctx);
		logger.info("------执行doTry,c=" + c.getClass().getName() + ",ctx=" + ctx.toString() + ",result=" + b);
		return b;
	}

	public static boolean invokeConfirm(IEtxSyncComponet c, IEtxContext ctx) throws Exception {
		boolean b = c.doConfirm(ctx);
		logger.info("------执行doConfirm,c=" + c.getClass().getName() + ",ctx=" + ctx.toString() + ",result=" + b);
		return b;
	}

	public static boolean invokeCancel(IEtxSyncComponet c, IEtxContext ctx) {
		boolean b = false;
		try {
			b = c.doCancel(ctx);
			logger.info("------执行doCancel,c=" + c.getClass().getName() + ",ctx=" + ctx.toString() + ",result=" + b);
		} catch (Exception e) {
			logger.error("", e);
		}

		return b;
	}

	public static boolean invokeAsyncService(IEtxAsyncComponet c, IEtxContext ctx) {
		boolean b = false;
		try {
			b = c.doService(ctx);
			logger.info("------执行doService,c=" + c.getClass().getName() + ",ctx=" + ctx.toString() + ",result=" + b);
		} catch (Exception e) {
			logger.error("", e);
		}

		return b;
	}

	public static void invokeAbsolutelyError(IEtxMonitorAsyncComponet c, IEtxContext ctx) {
		try {
			c.onAbsolutelyError(ctx);
			logger.error("------执行onAbsolutelyError,c=" + c.getClass().getName() + ",ctx=" + ctx.toString());
		} catch (Exception e) {
			logger.error("", e);
		}
	}
}
