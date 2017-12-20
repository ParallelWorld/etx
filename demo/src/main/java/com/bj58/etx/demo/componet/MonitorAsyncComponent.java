package com.bj58.etx.demo.componet;

import com.bj58.etx.api.componet.IEtxMonitorAsyncComponent;
import com.bj58.etx.api.context.IEtxContext;
import com.bj58.etx.demo.business.VirtualInvoker;


public class MonitorAsyncComponent implements IEtxMonitorAsyncComponent {

	@Override
	public boolean doService(IEtxContext ctx) throws Exception {
		return VirtualInvoker.doBiz(ctx);
	}

	@Override
	public void onAbsolutelyError(IEtxContext ctx) throws Exception {
		System.out.println("添加报警信息....");
	}

}
