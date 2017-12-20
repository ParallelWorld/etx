package com.bj58.etx.api.annotation;

import com.bj58.etx.api.idempotent.EtxDefaultQueryCheck;
import com.bj58.etx.api.idempotent.IEtxQueryCheck;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EtxRetry {

	// 重复次数
	int repeat() default 3;
	
	// 间隔时间(毫秒)
	int interval() default 20;
	
	// 查询确认
	Class<? extends IEtxQueryCheck> condition() default EtxDefaultQueryCheck.class;
}
