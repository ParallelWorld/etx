package com.bj58.etx.api.context;

import com.bj58.etx.api.dto.IEtxDto;
import com.bj58.etx.api.vo.IEtxVo;

/**
 * etx上下文
 * 注意：异步组件不要修改上下文的内容。
 * @author shencl
 */
public interface IEtxContext {

	public void add(String k, Object t);

	public <T> T get(String k);

	public void remove(String k);

	public <D extends IEtxDto> D getDto();

	public <D extends IEtxDto> void setDto(D dto);

	public <V extends IEtxVo> V getVo();

	public <V extends IEtxVo> void setVo(V vo);

	public <T> T param(int index);

	public void initParams(Object... params);

	public void setDto(Class<? extends IEtxDto> clazz);

	public void setVo(Class<? extends IEtxVo> clazz);

	public void setRunMode(int mode);

	public int getRunMode();
	
	public void setFlowType(String flowType);

	public String getFlowType();

}
