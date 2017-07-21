package com.bj58.zhaopin.jianli.etx.test.demo.componet;

import java.util.Random;

import com.bj58.zhaopin.jianli.etx.api.componet.IEtxTCCComponet;
import com.bj58.zhaopin.jianli.etx.api.context.IEtxContext;


public class UpdateOrderComponet implements IEtxTCCComponet {

	@Override
	public boolean doTry(IEtxContext ctx) throws Exception {
		System.out.println("查询订单");
		Thread.sleep(20);
		
		if(new Random().nextInt(1000) > 1){
			return true;
		}
		return false;
	}

	@Override
	public boolean doConfirm(IEtxContext ctx) throws Exception {
		System.out.println("修改订单");
		Thread.sleep(100);
		
		if(new Random().nextInt(1000) > 10){
			return true;
		}
		return false;
	}

	@Override
	public boolean doCancel(IEtxContext ctx) throws Exception {
		System.out.println("修改订单状态为无效");
		
		if(new Random().nextInt(1000) > 200){
			return true;
		}
		return false;
	}
	
}
