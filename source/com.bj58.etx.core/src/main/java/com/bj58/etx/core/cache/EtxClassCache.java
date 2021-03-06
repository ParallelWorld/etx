package com.bj58.etx.core.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class EtxClassCache {

    private static Map<String, Object> classMap = new HashMap<String, Object>();
    private static Logger logger = LoggerFactory.getLogger(EtxClassCache.class);

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
            logger.error("create class error, className=" + className);
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
            logger.error("create class error, className=" + key);
            return null;
        }
    }
}
