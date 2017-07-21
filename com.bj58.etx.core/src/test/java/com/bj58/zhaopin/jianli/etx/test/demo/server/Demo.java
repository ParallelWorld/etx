package com.bj58.zhaopin.jianli.etx.test.demo.server;

import com.bj58.zhaopin.jianli.etx.core.JianliEtx;
import com.bj58.zhaopin.jianli.etx.test.demo.componet.AddResourceComponet;
import com.bj58.zhaopin.jianli.etx.test.demo.componet.PayOrderComponet;
import com.bj58.zhaopin.jianli.etx.test.demo.componet.UpdateOrderComponet;
import com.bj58.zhaopin.jianli.etx.test.demo.componet.UseCouponComponet;
import com.bj58.zhaopin.jianli.etx.test.demo.dto.ResumeBuyDto;

public class Demo {
	
	public void buyResume(){
		JianliEtx.newEtxService()
		.setBinLogMode(true)
		.setDto(ResumeBuyDto.class)
		.addComponet(UseCouponComponet.class)
		.addComponet(PayOrderComponet.class)
		.addComponet(UpdateOrderComponet.class)
		.addComponet(AddResourceComponet.class)
		.invoke();
	}
	
	public static void taskStart(){
		JianliEtx.newEtxTaskService().start();
	}
	
	public static void main(String[] args) {
		new Demo().buyResume();
		taskStart();
	}
	
}
