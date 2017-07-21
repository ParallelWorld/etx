package com.bj58.zhaopin.jianli.etx.api.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EtxClassCache {

	private static Map<String, Object> classMap = new ConcurrentHashMap<String, Object>();
	
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
			throw new RuntimeException("create class error", e);
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
			throw new RuntimeException("create class error", e);
		}
	}
}
