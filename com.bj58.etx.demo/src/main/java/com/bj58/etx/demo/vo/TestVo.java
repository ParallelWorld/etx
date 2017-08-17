package com.bj58.etx.demo.vo;

import com.bj58.etx.api.vo.IEtxVo;

public class TestVo implements IEtxVo{

	private boolean success;
	
	@Override
	public boolean isSuccess() {
		return success;
	}

	@Override
	public void setSuccess(boolean success) {
		this.success = success;
	}
}
