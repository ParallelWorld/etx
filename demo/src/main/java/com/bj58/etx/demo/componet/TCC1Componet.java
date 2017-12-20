package com.bj58.etx.demo.componet;

import com.bj58.etx.api.componet.IEtxTCCComponent;
import com.bj58.etx.api.context.IEtxContext;
import com.bj58.etx.demo.business.VirtualInvoker;


public class TCC1Componet implements IEtxTCCComponent {

    @Override
    public boolean doTry(IEtxContext ctx) throws Exception {
        return VirtualInvoker.doBiz(ctx);
    }

    @Override
    public boolean doConfirm(IEtxContext ctx) throws Exception {
        Long testId = ctx.param(0);
        System.out.println("I get param testId=" + testId);
        return VirtualInvoker.doBiz(ctx);
    }

    @Override
    public boolean doCancel(IEtxContext ctx) throws Exception {
        return VirtualInvoker.doBiz(ctx);
    }
}
