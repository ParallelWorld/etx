package com.bj58.etx.core.context;

import com.bj58.etx.api.context.IEtxContext;
import com.bj58.etx.api.dto.IEtxDto;
import com.bj58.etx.api.vo.IEtxVo;
import com.bj58.etx.core.cache.EtxClassCache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * etx上下文实现
 */

@SuppressWarnings("unchecked")
public class EtxBaseContext implements IEtxContext, Serializable {

    private int runMode;
    private String flowType;
    private IEtxDto dto;
    private IEtxVo vo;
    private Map<String, Object> map = new HashMap<String, Object>(16);
    private List<Object> paramList = new ArrayList<Object>(4);

    @Override
    public void add(String k, Object v) {
        map.put(k, v);
    }

    @Override
    public <T> T get(String k) {
        return (T) map.get(k);
    }

    @Override
    public void remove(String k) {
        map.remove(k);
    }

    @Override
    public void initParams(Object... params) {
        if (params != null) {
            for (Object p : params) {
                paramList.add(p);
            }
        }
    }

    @Override
    public void setDto(Class<? extends IEtxDto> clazz) {
        IEtxDto d = EtxClassCache.getInstance(clazz);
        setDto(d);
    }

    @Override
    public void setVo(Class<? extends IEtxVo> clazz) {
        IEtxVo v = EtxClassCache.getInstance(clazz);
        setVo(v);
    }

    @Override
    public IEtxDto getDto() {
        return dto;
    }

    @Override
    public IEtxVo getVo() {
        return vo;
    }

    @Override
    public <T> T param(int index) {
        if (paramList != null && paramList.size() >= index) {
            return (T) paramList.get(index);
        }
        return null;
    }

    @Override
    public int getRunMode() {
        return runMode;
    }

    @Override
    public void setRunMode(int runMode) {
        this.runMode = runMode;
    }

    @Override
    public String getFlowType() {
        return flowType;
    }

    @Override
    public void setFlowType(String flowType) {
        this.flowType = flowType;
    }

    @Override
    public <D extends IEtxDto> void setDto(D dto) {
        this.dto = dto;
    }

    @Override
    public <V extends IEtxVo> void setVo(V vo) {
        this.vo = vo;
    }

    @Override
    public String toString() {
        return "JianliContext [map=" + map + ", paramList=" + paramList + ", runMode=" + runMode + ", flowType=" + flowType + ", dto=" + dto + ", vo=" + vo + "]";
    }

}
