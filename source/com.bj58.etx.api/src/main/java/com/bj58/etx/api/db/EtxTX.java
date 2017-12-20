package com.bj58.etx.api.db;


/**
 * 事务组信息
 */
public class EtxTX {

    // 事务组Id
    private long id;

    // 业务类型
    private String flowType;

    // 状态
    private int state;

    // 添加时间
    private long addTime;

    // 修改时间
    private long modifyTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFlowType() {
        return flowType;
    }

    public void setFlowType(String flowType) {
        this.flowType = flowType;
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

    @Override
    public String toString() {
        return "EtxTX [id=" + id + ", flowType=" + flowType + ", state=" + state + ", addTime=" + addTime + ", modifyTime=" + modifyTime + "]";
    }
}
