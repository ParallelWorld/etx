package com.bj58.zhaopin.jianli.etx.test.demo.dto;

import com.bj58.spat.scf.serializer.component.annotation.SCFMember;
import com.bj58.spat.scf.serializer.component.annotation.SCFSerializable;
import com.bj58.zhaopin.jianli.etx.api.dto.IEtxDto;

@SCFSerializable(name="com.bj58.zhaopin.jianli.etx.test.demo.dto.ResumeBuyDto")
public class ResumeBuyDto implements IEtxDto {
	
	@SCFMember(orderId=1)
	private long id;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	
}
