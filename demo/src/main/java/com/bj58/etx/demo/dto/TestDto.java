package com.bj58.etx.demo.dto;

import com.bj58.etx.api.dto.IEtxDto;

import java.io.Serializable;

public class TestDto implements IEtxDto, Serializable {
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
