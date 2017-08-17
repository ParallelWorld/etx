package com.bj58.etx.api.db;


/**
 * 同步执行log
 * @author shencl
 */

public class EtxSyncLog {

	// logId
	private long id;

	// 事务组Id
	private long txId;

	// 组件类名
	private String componet;

	// 状态
	private String state;

	// 添加时间
	private long addTime;

	// 修改时间
	private long modifyTime;

	// 上下文数据
	private byte[] data;
	
	// 回滚执行次数
	private int cancelCount;


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

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public int getCancelCount() {
		return cancelCount;
	}

	public void setCancelCount(int cancelCount) {
		this.cancelCount = cancelCount;
	}

	@Override
	public String toString() {
		return "EtxSyncLog [id=" + id + ", txId=" + txId + ", componet=" + componet + ", state=" + state + ", addTime=" + addTime + ", modifyTime=" + modifyTime + ", cancelCount="
				+ cancelCount + "]";
	}
}
