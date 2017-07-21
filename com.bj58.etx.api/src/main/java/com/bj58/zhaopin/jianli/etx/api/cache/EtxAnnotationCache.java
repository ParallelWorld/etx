package com.bj58.zhaopin.jianli.etx.api.cache;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.bj58.zhaopin.jianli.etx.api.annotation.EtxRetry;
import com.bj58.zhaopin.jianli.etx.api.componet.IEtxAsyncComponet;
import com.bj58.zhaopin.jianli.etx.api.componet.IEtxSyncComponet;
import com.bj58.zhaopin.jianli.etx.api.context.IEtxContext;

public class EtxAnnotationCache {

	private static Map<String, EtxRetry> retryMap = new ConcurrentHashMap<String, EtxRetry>();

	public static EtxRetry getRetry(IEtxSyncComponet c) {
		try {
			String key = c.getClass().getName();
			if (retryMap.containsKey(key)) {
				return retryMap.get(key);
			}
			synchronized (retryMap) {
				if (retryMap.containsKey(key)) {
					return retryMap.get(key);
				}
				Method m = c.getClass().getMethod("doCancel", IEtxContext.class);
				EtxRetry etxRetry = m.getAnnotation(EtxRetry.class);
				retryMap.put(key, etxRetry);
				return etxRetry;
			}
		} catch (Exception e) {
			throw new RuntimeException("get retry error", e);
		}
	}

	public static EtxRetry getRetry(IEtxAsyncComponet c) {
		try {
			String key = c.getClass().getName();
			if (retryMap.containsKey(key)) {
				return retryMap.get(key);
			}
			synchronized (retryMap) {
				if (retryMap.containsKey(key)) {
					return retryMap.get(key);
				}
				Method m = c.getClass().getMethod("doService", IEtxContext.class);
				EtxRetry etxRetry = m.getAnnotation(EtxRetry.class);
				retryMap.put(key, etxRetry);
				return etxRetry;
			}
		} catch (Exception e) {
			throw new RuntimeException("get retry error", e);
		}
	}
}
