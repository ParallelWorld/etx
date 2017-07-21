package com.bj58.zhaopin.jianli.etx.core.entity;

import com.bj58.sfft.utility.dao.annotation.Column;
import com.bj58.sfft.utility.dao.annotation.Id;
import com.bj58.sfft.utility.dao.annotation.Table;

/**
 * 事务组信息
 */
@Table(name="t_tx")
public class TXShadow {
	
	//事务组Id
	@Id(insertable = true)
	private long id;
	
	//状态
	@Column(name="state")
	private int state;
	
	//添加时间
	@Column(name="add_time")
	private long addTime;
	
	//修改时间
	@Column(name="modify_time")
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
