package com.bj58.etx.demo.dto;

import com.bj58.etx.api.dto.IEtxDto;

public class TestDto implements IEtxDto{
	private int dtoId;

	public int getDtoId() {
		return dtoId;
	}

	public void setDtoId(int dtoId) {
		this.dtoId = dtoId;
	}

	@Override
	public String toString() {
		return "TestDto [dtoId=" + dtoId + "]";
	}
	
	
	
}
