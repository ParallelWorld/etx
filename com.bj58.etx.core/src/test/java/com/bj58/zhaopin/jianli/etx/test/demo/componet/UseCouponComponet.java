package com.bj58.zhaopin.jianli.etx.test.demo.componet;

import java.util.Random;

import com.bj58.zhaopin.jianli.etx.api.componet.IEtxSyncComponet;
import com.bj58.zhaopin.jianli.etx.api.context.IEtxContext;

public class UseCouponComponet implements IEtxSyncComponet {

	@Override
	public boolean doConfirm(IEtxContext ctx) throws Exception {
		System.out.println("使用优惠券");
		Thread.sleep(100);
		
		if(new Random().nextInt(1000) > 10){
			return true;
		}
		return false;
	}

	@Override
	public boolean doCancel(IEtxContext ctx) throws Exception {
		System.out.println("还原优惠券");
		
		if(new Random().nextInt(1000) > 200){
			return true;
		}
		return false;
	}
}
