package com.bj58.etx.api.enums;

public enum EtxTXStateEnum {

    ERROR(0, "失败"), SYNCSUCCESS(1, "同步阶段成功"), FINISH(2, "事务结束");
    private int code;
    private String msg;

    private EtxTXStateEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
