package com.bj58.etx.api.enums;

public enum EtxSyncLogStateEnum {

    INIT("未执行"),
    CONFIRM_SUCCESS("confirm成功"),
    CONFIRM_ERROR("confirm失败"),
    CANCEL_SUCCESS("cancel成功"),
    CANCEL_ERROR("cancel失败"),
    CLOSE("关闭");

    private String msg;

    private EtxSyncLogStateEnum(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

}
