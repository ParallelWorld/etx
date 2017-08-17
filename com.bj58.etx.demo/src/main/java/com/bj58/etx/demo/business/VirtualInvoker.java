package com.bj58.etx.demo.business;

import java.util.Random;

import com.bj58.etx.api.context.IEtxContext;

/**
 * 模拟业务执行 失败概率20%
 */
public class VirtualInvoker {
	
	static Random ran = new Random();
	
	public static boolean doBiz(IEtxContext ctx) throws Exception {
		
		//模拟业务执行
		Thread.sleep(3000);
		
		// 模拟执行结果
		if(ran.nextInt(20) > 5){
			return true;
		}
		return false;
	}
}
