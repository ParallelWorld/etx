package com.bj58.zhaopin.jianli.etx.api.runtime;

import com.bj58.zhaopin.jianli.etx.api.db.IEtxDao;
import com.bj58.zhaopin.jianli.etx.api.serialize.IEtxSerializer;

public class EtxRuntime {
	// 持久化类
	public static IEtxDao dao = null;

	// 序列化类
	public static IEtxSerializer serializer = null;

	// task轮询流水时间(毫秒)
	public static long interval = 20000;

	public static void setDao(IEtxDao dao) {
		EtxRuntime.dao = dao;
	}

	public static void setSerializer(IEtxSerializer serializer) {
		EtxRuntime.serializer = serializer;
	}

	public static void setInterval(long interval) {
		EtxRuntime.interval = interval;
	}

}
