package com.bj58.etx.api.idempotent;

import com.bj58.etx.api.context.IEtxContext;

public class EtxDefaultQueryCheck implements IEtxQueryCheck{

	@Override
	public boolean isAreadySuccess(IEtxContext ctx) {
		return false;
	}

}
