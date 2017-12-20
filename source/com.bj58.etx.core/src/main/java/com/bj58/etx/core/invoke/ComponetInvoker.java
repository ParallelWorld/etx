package com.bj58.etx.core.invoke;

import com.bj58.etx.api.annotation.EtxRetry;
import com.bj58.etx.api.componet.IEtxAsyncComponent;
import com.bj58.etx.api.componet.IEtxMonitorAsyncComponent;
import com.bj58.etx.api.componet.IEtxSyncComponet;
import com.bj58.etx.api.componet.IEtxTCCComponent;
import com.bj58.etx.api.context.IEtxContext;
import com.bj58.etx.api.idempotent.IEtxQueryCheck;
import com.bj58.etx.api.vo.IEtxVo;
import com.bj58.etx.core.cache.EtxAnnotationCache;
import com.bj58.etx.core.cache.EtxClassCache;
import com.bj58.etx.core.util.EtxMonitorUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.TimeUnit;

public class ComponetInvoker {

    private static Log logger = LogFactory.getLog(ComponetInvoker.class);

    public static boolean invokeTry(IEtxTCCComponent c, IEtxContext ctx) {
        boolean b = false;
        setVoResult(ctx, b);
        try {
            b = c.doTry(ctx);
            setVoResult(ctx, b);
        } catch (Throwable e) {
            logger.error("invoke compoent doTry error", e);
        }
        logger.info("------执行doTry,componet = " + c.getClass().getName() + ",ctx = " + ctx + ",result = " + b);
        if (!b) {
            EtxMonitorUtil.doTryError();
        }
        return b;
    }

    public static boolean invokeConfirm(IEtxSyncComponet c, IEtxContext ctx) {
        boolean b = false;
        setVoResult(ctx, b);
        try {
            EtxRetry retry = EtxAnnotationCache.getConfirmRetry(c);
            if (retry != null) {
                int count = 0;
                IEtxQueryCheck check = EtxClassCache.getInstance(retry.condition());
                while (!b && count < retry.repeat()) {
                    b = c.doConfirm(ctx);
                    if (check != null && check.isAreadySuccess(ctx)) {
                        b = true;
                        break;
                    }
                    Thread.sleep(retry.interval());
                    count++;
                }
            } else {
                b = c.doConfirm(ctx);
            }
            setVoResult(ctx, b);
        } catch (Throwable e) {
            logger.error("invoke componet doConfirm error", e);
        }

        logger.info("------执行doConfirm,componet = " + c.getClass().getName() + ",ctx = " + ctx + ",result = " + b);
        if (!b) {
            EtxMonitorUtil.doConfirmError();
        }
        return b;
    }

    public static boolean invokeCancel(IEtxSyncComponet c, IEtxContext ctx) {
        boolean b = false;
        setVoResult(ctx, b);
        try {
            b = c.doCancel(ctx);
            setVoResult(ctx, b);
        } catch (Throwable e) {
            logger.error("invoke componet doCancel error", e);
        }

        logger.info("------执行doCancel,componet = " + c.getClass().getName() + ",ctx = " + ctx + ",result = " + b);
        if (!b) {
            EtxMonitorUtil.doCancelError();
        }
        return b;
    }

    public static boolean invokeCancel(IEtxSyncComponet c, IEtxContext ctx, boolean willRetry) {
        boolean b = ComponetInvoker.invokeCancel(c, ctx);

        if (!willRetry) {
            return b;
        }

        EtxRetry retry = EtxAnnotationCache.getCancelRetry(c);
        if (retry == null) {
            return b;
        }

        IEtxQueryCheck check = EtxClassCache.getInstance(retry.condition());

        try {
            for (int i = 0; i < retry.repeat(); i++) {
                TimeUnit.MICROSECONDS.sleep(retry.interval());

                if (check != null && check.isAreadySuccess(ctx)) {
                    b = true;
                    break;
                }

                b = ComponetInvoker.invokeCancel(c, ctx);

                if (b) {
                    break;
                }
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        return b;
    }

    public static boolean invokeAsyncService(IEtxAsyncComponent c, IEtxContext ctx) {
        boolean b = false;
        setVoResult(ctx, b);
        try {
            b = c.doService(ctx);
            setVoResult(ctx, b);
        } catch (Throwable e) {
            logger.error("invoke componet doService error", e);
        }

        logger.info("------执行doService,componet = " + c.getClass().getName() + ",ctx=" + ctx + ",result = " + b);
        if (!b) {
            EtxMonitorUtil.doServiceError();
        }
        return b;
    }

    public static boolean invokeAsyncService(IEtxAsyncComponent c, IEtxContext ctx, boolean willRetry) {
        boolean b = ComponetInvoker.invokeAsyncService(c, ctx);

        if (!willRetry) {
            return b;
        }

        EtxRetry retry = EtxAnnotationCache.getServiceRetry(c);
        if (retry == null) {
            return b;
        }

        IEtxQueryCheck check = EtxClassCache.getInstance(retry.condition());

        try {
            for (int i = 0; i < retry.repeat(); i++) {
                TimeUnit.MICROSECONDS.sleep(retry.interval());

                if (check != null && check.isAreadySuccess(ctx)) {
                    b = true;
                    break;
                }

                b = ComponetInvoker.invokeAsyncService(c, ctx);

                if (b) {
                    break;
                }
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        return b;
    }

    public static void invokeAbsolutelyError(IEtxMonitorAsyncComponent c, IEtxContext ctx) {
        try {
            logger.error("------执行onAbsolutelyError,componet = " + c.getClass().getName() + ",ctx=" + ctx);
            c.onAbsolutelyError(ctx);
            EtxMonitorUtil.doAbsolutelyError();
        } catch (Throwable e) {
            logger.error("invoke componet onAbsolutelyError error", e);
        }
    }

    private static void setVoResult(IEtxContext ctx, boolean success) {
        IEtxVo vo = ctx.getVo();
        if (vo != null) {
            vo.setSuccess(success);
        }
    }
}
