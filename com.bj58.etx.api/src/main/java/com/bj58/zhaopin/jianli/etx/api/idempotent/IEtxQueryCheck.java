package com.bj58.zhaopin.jianli.etx.api.idempotent;

import com.bj58.zhaopin.jianli.etx.api.context.IEtxContext;

public interface IEtxQueryCheck {
	
	public boolean isAreadySuccess(IEtxContext ctx);
}
