package com.bj58.zhaopin.jianli.etx.core.entity;

import com.bj58.sfft.utility.dao.annotation.Column;
import com.bj58.sfft.utility.dao.annotation.Id;
import com.bj58.sfft.utility.dao.annotation.Table;

/**
 * 执行明细
 * @author shencl
 */
@Table(name="t_async_log")
public class AsyncLogShadow {
	
	//日志Id
	@Id(insertable = true)
	private long id;
	
	//事务组Id
	@Column(name="tx_id")
	private long txId;
	
	//组件类名
	@Column(name="componet")
	private String componet;
	
	//状态
	@Column(name="state")
	private String state;
	
	//添加时间
	@Column(name="add_time")
	private long addTime;
	
	//修改时间
	@Column(name="modify_time")
	private long modifyTime;
	
	//上下文数据
	@Column(name="data")
	private byte[] data;
	
	//已执行次数
	@Column(name="count")
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
