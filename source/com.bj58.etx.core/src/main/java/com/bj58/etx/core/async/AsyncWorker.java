package com.bj58.etx.core.async;

import java.util.List;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.bj58.etx.api.componet.IEtxAsyncComponet;
import com.bj58.etx.api.componet.IEtxMonitorAsyncComponet;
import com.bj58.etx.api.componet.IEtxSyncComponet;
import com.bj58.etx.api.context.IEtxContext;
import com.bj58.etx.api.db.EtxAsyncLog;
import com.bj58.etx.api.db.EtxSyncLog;
import com.bj58.etx.api.enums.EtxAsyncLogStateEnum;
import com.bj58.etx.api.enums.EtxSyncLogStateEnum;
import com.bj58.etx.api.enums.EtxTXStateEnum;
import com.bj58.etx.core.cache.EtxClassCache;
import com.bj58.etx.core.invoke.ComponetInvoker;
import com.bj58.etx.core.runtime.EtxRuntime;
import com.bj58.etx.core.util.EtxDaoUtil;

public class AsyncWorker {

    private static Log logger = LogFactory.getLog(AsyncWorker.class);
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
                        logger.info("------无同步组件需要回滚,修改事务组状态为FINISH,txId=" + txId + ",flowType=" + ctx.getFlowType());
                        EtxDaoUtil.updateTx(txId, EtxTXStateEnum.FINISH);
                        return;
                    }

                    logger.info("------回滚事务组的同步组件,txId=" + txId + ",flowType=" + ctx.getFlowType());
                    int successCount = 0;
                    for (EtxSyncLog log : logs) {
                        IEtxSyncComponet c = EtxClassCache.getInstance(log.getComponet());
                        if (c == null) {
                            continue;
                        }
                        boolean b = ComponetInvoker.invokeCancel(c, ctx);
                        if (b) {
                            successCount++;
                            EtxDaoUtil.updateSyncLogState(txId, log.getId(), EtxSyncLogStateEnum.CANCEL_SUCCESS);
                        } else {
                            EtxDaoUtil.updateSyncLogState(txId, log.getId(), EtxSyncLogStateEnum.CANCEL_ERROR);
                        }
                    }
                    // 如果全部执行成功，则把事务组记录修改为已结束
                    if (successCount == logs.size()) {
                        logger.info("------全部同步组件回滚完毕,修改事务组状态为FINISH,txId=" + txId + ",flowType=" + ctx.getFlowType());
                        EtxDaoUtil.updateTx(txId, EtxTXStateEnum.FINISH);
                        return;
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
                    ComponetInvoker.invokeCancel(c, ctx, true);
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
                    boolean b = ComponetInvoker.invokeAsyncService(c, ctx, true);
                    // 最终执行失败会触发监控消息
                    if (!b && c instanceof IEtxMonitorAsyncComponet) {
                        IEtxMonitorAsyncComponet mac = (IEtxMonitorAsyncComponet) c;
                        ComponetInvoker.invokeAbsolutelyError(mac, ctx);
                    }
                }
            }
        });
    }

    /**
     * 执行异步任务（只执行一遍，未成功的交给task）
     */
    public static void invokeAsyncFromDB(final long txId, final IEtxContext ctx) throws Exception {
        fixedThreadPool.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    List<EtxAsyncLog> list = EtxDaoUtil.getPendingAsyncLogList(txId);

                    if (list == null || list.size() == 0) {
                        // 如果没有需要执行的明细，则把事务组记录改为已结束
                        EtxDaoUtil.updateTx(txId, EtxTXStateEnum.FINISH);
                        logger.info("------无异步组件需要执行,修改事务组状态为FINISH,txId=" + txId + ",flowType=" + ctx.getFlowType());
                        return;
                    }

                    logger.info("------执行事务组的异步组件,txId=" + txId + ",flowType=" + ctx.getFlowType());
                    int successCount = 0;
                    for (EtxAsyncLog log : list) {
                        long logId = log.getId();
                        IEtxAsyncComponet c = EtxClassCache.getInstance(log.getComponet());
                        if (c == null) {
                            continue;
                        }
                        boolean b = ComponetInvoker.invokeAsyncService(c, ctx);
                        if (b) {
                            successCount++;
                            EtxDaoUtil.updateAsyncLogState(txId, logId, EtxAsyncLogStateEnum.SUCCESS);
                        } else {
                            EtxDaoUtil.updateAsyncLogState(txId, logId, EtxAsyncLogStateEnum.ERROR);
                        }
                    }

                    // 如果全部执行成功，则把事务组记录修改为已结束
                    if (successCount == list.size()) {
                        EtxDaoUtil.updateTx(txId, EtxTXStateEnum.FINISH);
                        logger.info("------全部异步组件执行完毕,修改事务组状态为FINISH,txId=" + txId + ",flowType=" + ctx.getFlowType());
                    }
                } catch (Exception e) {
                    logger.error("", e);
                }
            }
        });
    }
}
