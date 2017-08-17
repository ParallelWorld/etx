package com.bj58.etx.api.componet;

import com.bj58.etx.api.context.IEtxContext;

/**
 * etx TCC执行组件
 * @author shencl
 */
public interface IEtxTCCComponet extends IEtxSyncComponet {

	/**
	 * 执行操作
	 */
	public boolean doTry(IEtxContext ctx) throws Exception;
}
