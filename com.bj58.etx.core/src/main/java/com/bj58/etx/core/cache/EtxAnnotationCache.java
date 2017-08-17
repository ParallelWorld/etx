package com.bj58.etx.core.cache;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bj58.etx.api.annotation.EtxRetry;
import com.bj58.etx.api.componet.IEtxAsyncComponet;
import com.bj58.etx.api.componet.IEtxSyncComponet;
import com.bj58.etx.api.context.IEtxContext;

public class EtxAnnotationCache {

	private static Map<String, EtxRetry> retryMap = new ConcurrentHashMap<String, EtxRetry>();
	private static Log logger = LogFactory.getLog(EtxAnnotationCache.class);

	public static EtxRetry getRetry(IEtxSyncComponet c) {
		String key = c.getClass().getName();
		try {
			if (retryMap.containsKey(key)) {
				return retryMap.get(key);
			}
			synchronized (retryMap) {
				if (retryMap.containsKey(key)) {
					return retryMap.get(key);
				}
				Method m = c.getClass().getMethod("doCancel", IEtxContext.class);
				EtxRetry etxRetry = m.getAnnotation(EtxRetry.class);
				if(etxRetry!=null){
					retryMap.put(key, etxRetry);
					
				}
				return etxRetry;
			}
		} catch (Exception e) {
			logger.error("get retry error, className="+key);
			return null;
		}
	}

	public static EtxRetry getRetry(IEtxAsyncComponet c) {
		String key = c.getClass().getName();
		try {
			if (retryMap.containsKey(key)) {
				return retryMap.get(key);
			}
			synchronized (retryMap) {
				if (retryMap.containsKey(key)) {
					return retryMap.get(key);
				}
				Method m = c.getClass().getMethod("doService", IEtxContext.class);
				EtxRetry etxRetry = m.getAnnotation(EtxRetry.class);
				if(etxRetry!=null){
					retryMap.put(key, etxRetry);
				}
				return etxRetry;
			}
		} catch (Exception e) {
			logger.error("get retry error, className="+key);
			return null;
		}
	}
}
