package com.bj58.etx.api.componet;

import com.bj58.etx.api.context.IEtxContext;

/**
 * 可监控的异步组件
 *
 * @author shencl
 */
public interface IEtxMonitorAsyncComponent extends IEtxAsyncComponent {
    /**
     * 最终失败，回调方法
     */
    public void onAbsolutelyError(IEtxContext ctx) throws Exception;
}
