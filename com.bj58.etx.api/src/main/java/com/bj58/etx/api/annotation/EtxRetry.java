package com.bj58.etx.api.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.bj58.etx.api.idempotent.EtxDefaultQueryCheck;
import com.bj58.etx.api.idempotent.IEtxQueryCheck;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EtxRetry {

	// 重复次数
	int repeat() default 1;

	// 查询确认
	Class<? extends IEtxQueryCheck> condition() default EtxDefaultQueryCheck.class;
}
