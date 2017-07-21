package com.bj58.zhaopin.jianli.etx.api.db;

/**
 * 执行明细
 * @author shencl
 */
public class EtxAsyncLog {
	
	//日志Id
	private long id;
	
	//事务组Id
	private long txId;
	
	//组件类名
	private String componet;
	
	//状态
	private String state;
	
	//添加时间
	private long addTime;
	
	//修改时间
	private long modifyTime;
	
	//上下文数据
	private byte[] data;
	
	//已执行次数
	private int count;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getTxId() {
		return txId;
	}

	public void setTxId(long txId) {
		this.txId = txId;
	}

	public String getComponet() {
		return componet;
	}

	public void setComponet(String componet) {
		this.componet = componet;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
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

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
}
