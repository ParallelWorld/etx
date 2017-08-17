package com.bj58.etx.api.idempotent;

import com.bj58.etx.api.context.IEtxContext;

/**
 * 幂等 查询确认 接口
 * 一个方法调用没有返回成功，（但他内部可能是成功的，比较典型的是超时异常）。
 * etx框架会在重复执行失败组件的时候，先调用接口来确认是否真正执行成功
 * @author shencl
 */
public interface IEtxQueryCheck {
	
	public boolean isAreadySuccess(IEtxContext ctx);
}
