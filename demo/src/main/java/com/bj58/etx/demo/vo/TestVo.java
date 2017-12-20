package com.bj58.etx.demo.vo;

import com.bj58.etx.api.vo.IEtxVo;

import java.io.Serializable;

public class TestVo implements IEtxVo, Serializable {

    private boolean success;

    @Override
    public boolean isSuccess() {
        return success;
    }

    @Override
    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "TestVo [success=" + success + "]";
    }
}
