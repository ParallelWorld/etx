package com.bj58.zhaopin.jianli.etx.test.demo.componet;

import java.util.Random;

import com.bj58.zhaopin.jianli.etx.api.componet.IEtxTCCComponet;
import com.bj58.zhaopin.jianli.etx.api.context.IEtxContext;
import com.bj58.zhaopin.jianli.etx.test.demo.dto.ResumeBuyDto;

public class PayOrderComponet implements IEtxTCCComponet {

	@Override
	public boolean doTry(IEtxContext ctx) throws Exception {
		System.out.println("冻结金额");
		Thread.sleep(20);
		
		ResumeBuyDto dto = ctx.getDto();
		dto.setId(1000);
		
		if(new Random().nextInt(1000) > 1){
			return true;
		}
		return false;
	}

	@Override
	public boolean doConfirm(IEtxContext ctx) throws Exception {
		System.out.println("扣减金额");
		Thread.sleep(100);
		
		if(new Random().nextInt(1000) > 100){
			return true;
		}
		return false;
	}

	@Override
	public boolean doCancel(IEtxContext ctx) throws Exception {
		System.out.println("解冻金额");
		System.out.println("退款");
		
		if(new Random().nextInt(1000) > 200){
			return true;
		}
		return false;
	}



}
