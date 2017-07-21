package com.bj58.zhaopin.jianli.etx.api.db;


/**
 * 事务组信息
 */
public class EtxTX {
	
	//事务组Id
	private long id;
	
	//状态
	private int state;
	
	//添加时间
	private long addTime;
	
	//修改时间
	private long modifyTime;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public long getAddTime() {
		return addTime;
	}

	public void setAddTime(long addTime) {
		this.addTime = addTime;
	}

	public long getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(long modifyTime) {
		this.modifyTime = modifyTime;
	}
	
	
}
