package com.bj58.etx.demo.componet;

import com.bj58.etx.api.componet.IEtxTCCComponet;
import com.bj58.etx.api.context.IEtxContext;
import com.bj58.etx.demo.business.VirtualInvoker;
import com.bj58.etx.demo.dto.TestDto;

public class TCC2Componet implements IEtxTCCComponet {

	@Override
	public boolean doTry(IEtxContext ctx) throws Exception {
		return VirtualInvoker.doBiz(ctx);
	}

	@Override
	public boolean doConfirm(IEtxContext ctx) throws Exception {
		TestDto dto = ctx.getDto();
		System.out.println("I get dto=" + dto);
		return VirtualInvoker.doBiz(ctx);
	}

	@Override
	public boolean doCancel(IEtxContext ctx) throws Exception {
		return VirtualInvoker.doBiz(ctx);
	}
}
