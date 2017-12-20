package com.bj58.etx.demo.componet;

import com.bj58.etx.api.componet.IEtxAsyncComponent;
import com.bj58.etx.api.context.IEtxContext;
import com.bj58.etx.demo.business.VirtualInvoker;


public class AsyncComponet implements IEtxAsyncComponent {

	@Override
	public boolean doService(IEtxContext ctx) throws Exception {
		return VirtualInvoker.doBiz(ctx);
	}

}
