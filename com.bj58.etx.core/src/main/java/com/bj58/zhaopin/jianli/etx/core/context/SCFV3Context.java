package com.bj58.zhaopin.jianli.etx.core.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bj58.spat.scf.serializer.component.annotation.SCFMember;
import com.bj58.spat.scf.serializer.component.annotation.SCFSerializable;
import com.bj58.zhaopin.jianli.etx.api.cache.EtxClassCache;
import com.bj58.zhaopin.jianli.etx.api.context.IEtxContext;
import com.bj58.zhaopin.jianli.etx.api.dto.IEtxDto;
import com.bj58.zhaopin.jianli.etx.api.vo.IEtxVo;

/**
 * etx上下文实现
 */

@SuppressWarnings("unchecked")
@SCFSerializable(name = "com.bj58.zhaopin.jianli.etx.context.SCFV3Context")
public class SCFV3Context implements IEtxContext {

	@SCFMember(orderId = 1)
	private Map<String, Object> map = new HashMap<String, Object>(16);

	@SCFMember(orderId = 2)
	private List<Object> paramList = new ArrayList<Object>(4);

	@SCFMember(orderId = 3)
	private boolean binLogMode;

	@SCFMember(orderId = 4, generic = true)
	private IEtxDto dto;

	@SCFMember(orderId = 5, generic = true)
	private IEtxVo vo;

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

	public IEtxDto getDto() {
		return dto;
	}

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
	public void setBinLogMode(boolean binLogMode) {
		this.binLogMode = binLogMode;

	}

	@Override
	public boolean isBinLogMode() {
		return binLogMode;
	}

	@Override
	public <D extends IEtxDto> void setDto(D dto) {
		this.dto = dto;
	}

	@Override
	public <V extends IEtxVo> void setVo(V vo) {
		this.vo = vo;
	}
	
}
