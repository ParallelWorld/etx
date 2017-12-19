package com.bj58.etx.core.cache;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.bj58.etx.api.componet.IEtxComponet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bj58.etx.api.annotation.EtxRetry;
import com.bj58.etx.api.componet.IEtxAsyncComponet;
import com.bj58.etx.api.componet.IEtxSyncComponet;
import com.bj58.etx.api.context.IEtxContext;
import com.bj58.etx.api.exception.EtxException;

public class EtxAnnotationCache {

    private static Map<String, EtxRetry> do_confirm_retry_map = new HashMap<String, EtxRetry>(32);
    private static Map<String, EtxRetry> do_cancel_retry_map = new HashMap<String, EtxRetry>(32);
    private static Map<String, EtxRetry> do_service_retry_map = new HashMap<String, EtxRetry>(32);
    private static Log logger = LogFactory.getLog(EtxAnnotationCache.class);

    private static EtxRetry getRetry(IEtxComponet c, Map<String, EtxRetry> map, String methodName) {
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

    public static EtxRetry getCancelRetry(IEtxComponet c) {
        return getRetry(c, do_cancel_retry_map, "doCancel");
    }

    public static EtxRetry getServiceRetry(IEtxAsyncComponet c) {
        return getRetry(c, do_service_retry_map, "doService");
    }
}
