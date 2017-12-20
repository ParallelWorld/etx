package com.bj58.etx.core.cache;

import com.bj58.etx.api.annotation.EtxRetry;
import com.bj58.etx.api.componet.IEtxAsyncComponent;
import com.bj58.etx.api.componet.IEtxComponent;
import com.bj58.etx.api.componet.IEtxSyncComponet;
import com.bj58.etx.api.context.IEtxContext;
import com.bj58.etx.api.exception.EtxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class EtxAnnotationCache {

    private static Map<String, EtxRetry> do_confirm_retry_map = new HashMap<String, EtxRetry>(32);
    private static Map<String, EtxRetry> do_cancel_retry_map = new HashMap<String, EtxRetry>(32);
    private static Map<String, EtxRetry> do_service_retry_map = new HashMap<String, EtxRetry>(32);
    private static Logger logger = LoggerFactory.getLogger(EtxAnnotationCache.class);

    private static EtxRetry getRetry(IEtxComponent c, Map<String, EtxRetry> map, String methodName) {
        if (c == null) {
            return null;
        }

        String key = c.getClass().getName();
        try {
            if (map.containsKey(key)) {
                return map.get(key);
            }
            synchronized (map) {
                if (map.containsKey(key)) {
                    return map.get(key);
                }
                Method m = c.getClass().getMethod(methodName, IEtxContext.class);
                EtxRetry etxRetry = m.getAnnotation(EtxRetry.class);
                if (etxRetry != null) {
                    if (etxRetry.repeat() < 1) {
                        throw new EtxException(methodName + " EtxRetry repeat must >= 1,className=" + key);
                    }

                    if (etxRetry.interval() < 0) {
                        throw new EtxException(methodName + " EtxRetry interval must >= 0,className=" + key);
                    }
                    map.put(key, etxRetry);
                }

                return etxRetry;
            }
        } catch (Exception e) {
            logger.error(methodName + " retry error, className=" + key);
        }
        return null;
    }

    public static EtxRetry getConfirmRetry(IEtxSyncComponet c) {
        return getRetry(c, do_confirm_retry_map, "doConfirm");
    }

    public static EtxRetry getCancelRetry(IEtxComponent c) {
        return getRetry(c, do_cancel_retry_map, "doCancel");
    }

    public static EtxRetry getServiceRetry(IEtxAsyncComponent c) {
        return getRetry(c, do_service_retry_map, "doService");
    }
}
