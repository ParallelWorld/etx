package com.bj58.zhaopin.jianli.etx.api.enums;

public enum EtxAsyncLogStateEnum {

	INIT("初始状态"), ERROR("失败"),SUCCESS("成功"), CLOSE("关闭");
	private String msg;

	private EtxAsyncLogStateEnum(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}

}
