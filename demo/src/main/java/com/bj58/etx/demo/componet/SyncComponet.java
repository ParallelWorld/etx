package com.bj58.etx.demo.componet;

import com.bj58.etx.api.componet.IEtxSyncComponet;
import com.bj58.etx.api.context.IEtxContext;
import com.bj58.etx.demo.business.VirtualInvoker;


public class SyncComponet implements IEtxSyncComponet {

    @Override
    public boolean doConfirm(IEtxContext ctx) throws Exception {
        return VirtualInvoker.doBiz(ctx);
    }

    @Override
    public boolean doCancel(IEtxContext ctx) throws Exception {
        return VirtualInvoker.doBiz(ctx);
    }
}
