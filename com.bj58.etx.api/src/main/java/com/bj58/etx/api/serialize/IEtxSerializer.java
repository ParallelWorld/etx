package com.bj58.etx.api.serialize;

import com.bj58.etx.api.context.IEtxContext;

public interface IEtxSerializer {

	public byte[] serialize(IEtxContext ctx);

	public IEtxContext deSerialize(byte[] bs);

}
