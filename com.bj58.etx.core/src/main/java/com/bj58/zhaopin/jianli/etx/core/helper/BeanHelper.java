package com.bj58.zhaopin.jianli.etx.core.helper;

import com.bj58.zhaopin.jianli.common.utils.BeanUtil;
import com.bj58.zhaopin.jianli.etx.api.db.EtxAsyncLog;
import com.bj58.zhaopin.jianli.etx.api.db.EtxSyncLog;
import com.bj58.zhaopin.jianli.etx.api.db.EtxTX;
import com.bj58.zhaopin.jianli.etx.core.entity.AsyncLogShadow;
import com.bj58.zhaopin.jianli.etx.core.entity.SyncLogShadow;
import com.bj58.zhaopin.jianli.etx.core.entity.TXShadow;

public class BeanHelper {
	public static AsyncLogShadow convert(EtxAsyncLog log) {
		if (log == null) {
			return null;
		}
		AsyncLogShadow bean = new AsyncLogShadow();
		BeanUtil.copyBean(log, bean, null);
		return bean;
	}
	
	public static EtxAsyncLog convert(AsyncLogShadow log) {
		if (log == null) {
			return null;
		}
		EtxAsyncLog bean = new EtxAsyncLog();
		BeanUtil.copyBean(log, bean, null);
		return bean;
	}
	
	public static TXShadow convert(EtxTX tx) {
		if (tx == null) {
			return null;
		}
		TXShadow bean = new TXShadow();
		BeanUtil.copyBean(tx, bean, null);
		return bean;
	}
	
	public static EtxTX convert(TXShadow tx) {
		if (tx == null) {
			return null;
		}
		EtxTX bean = new EtxTX();
		BeanUtil.copyBean(tx, bean, null);
		return bean;
	}
	
	public static SyncLogShadow convert(EtxSyncLog log) {
		if (log == null) {
			return null;
		}
		SyncLogShadow bean = new SyncLogShadow();
		BeanUtil.copyBean(log, bean, null);
		return bean;
	}
	
	public static EtxSyncLog convert(SyncLogShadow log) {
		if (log == null) {
			return null;
		}
		EtxSyncLog bean = new EtxSyncLog();
		BeanUtil.copyBean(log, bean, null);
		return bean;
	}

}
