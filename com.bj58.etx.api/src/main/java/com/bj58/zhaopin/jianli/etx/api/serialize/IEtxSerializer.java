package com.bj58.zhaopin.jianli.etx.api.serialize;

import com.bj58.zhaopin.jianli.etx.api.context.IEtxContext;

public interface IEtxSerializer {

	public byte[] serialize(IEtxContext ctx);

	public IEtxContext deSerialize(byte[] bs);

}
