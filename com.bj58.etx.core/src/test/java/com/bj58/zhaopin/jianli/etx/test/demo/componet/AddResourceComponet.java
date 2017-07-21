package com.bj58.zhaopin.jianli.etx.test.demo.componet;

import java.util.Random;

import com.bj58.zhaopin.jianli.etx.api.annotation.EtxRetry;
import com.bj58.zhaopin.jianli.etx.api.componet.IEtxAsyncComponet;
import com.bj58.zhaopin.jianli.etx.api.context.IEtxContext;
import com.bj58.zhaopin.jianli.etx.test.demo.dto.ResumeBuyDto;

public class AddResourceComponet implements IEtxAsyncComponet {

	@Override
	@EtxRetry(repeat = 5)
	public boolean doService(IEtxContext ctx) throws Exception {
		System.out.println("添加资源点");
		Thread.sleep(3000);
		
	
		
		ResumeBuyDto dto = ctx.getDto();
		System.out.println("反序列化获取到id====="+dto.getId());
		
		if(new Random().nextInt(20) > 10){
			return true;
		}
		return false;
	}
	
}
