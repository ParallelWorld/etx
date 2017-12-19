package com.bj58.etx.core.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EtxClassCache {

	private static Map<String, Object> classMap = new ConcurrentHashMap<String, Object>();
	private static Log logger = LogFactory.getLog(EtxClassCache.class);
	
	@SuppressWarnings("unchecked")
	public static <T> T getInstance(String className) {
		String key = className;
		if (classMap.containsKey(key)) {
			return (T) classMap.get(key);
		}
		try {
			synchronized (classMap) {
				if (classMap.containsKey(key)) {
					return (T) classMap.get(key);
				}
				Object o = Class.forName(className).newInstance();
				classMap.put(key, o);
				return (T) o;
			}
		} catch (Exception e) {
			logger.error("create class error, className="+className);
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getInstance(Class<?> clazz) {
		String key = clazz.getName();
		if (classMap.containsKey(key)) {
			return (T) classMap.get(key);
		}
		try {
			synchronized (classMap) {
				if (classMap.containsKey(key)) {
					return (T) classMap.get(key);
				}
				Object o = clazz.newInstance();
				classMap.put(key, o);
				return (T) o;
			}
		} catch (Exception e) {
			logger.error("create class error, className="+key);
			return null;
		}
	}
}
