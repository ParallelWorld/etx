package com.bj58.zhaopin.jianli.etx.api.componet;

import com.bj58.zhaopin.jianli.etx.api.context.IEtxContext;

/**
 * etx 异步组件接口
 * @author shencl
 */
public interface IEtxAsyncComponet extends IEtxComponet{
	
	/**
	 * 执行方法，要求幂等
	 */
	public boolean doService(IEtxContext ctx) throws Exception;
}
