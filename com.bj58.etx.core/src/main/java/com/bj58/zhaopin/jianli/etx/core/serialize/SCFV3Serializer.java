package com.bj58.zhaopin.jianli.etx.core.serialize;

import com.bj58.spat.scf.protocol.serializer.SCFSerializerV3;
import com.bj58.zhaopin.jianli.etx.api.context.IEtxContext;
import com.bj58.zhaopin.jianli.etx.api.serialize.IEtxSerializer;
import com.bj58.zhaopin.jianli.etx.core.context.SCFV3Context;

public class SCFV3Serializer implements IEtxSerializer {

	@Override
	public byte[] serialize(IEtxContext ctx) {
		SCFSerializerV3 ser = new SCFSerializerV3();
		try {
			return ser.serialize(ctx);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public IEtxContext deSerialize(byte[] bs) {
		SCFSerializerV3 ser = new SCFSerializerV3();
		try {
			SCFV3Context ctx = (SCFV3Context)ser.deserialize(bs, SCFV3Context.class);
			return ctx;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
