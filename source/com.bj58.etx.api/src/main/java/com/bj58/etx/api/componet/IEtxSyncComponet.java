package com.bj58.etx.api.componet;

import com.bj58.etx.api.context.IEtxContext;

/**
 * etx同步执行组件
 *
 * @author shencl
 */
public interface IEtxSyncComponet extends IEtxComponent {

    /**
     * 确认操作
     */
    public boolean doConfirm(IEtxContext ctx) throws Exception;

    /**
     * 取消操作，要求无副作用
     */
    public boolean doCancel(IEtxContext ctx) throws Exception;
}
