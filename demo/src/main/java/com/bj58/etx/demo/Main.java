package com.bj58.etx.demo;

import com.bj58.etx.boot.Etx;
import com.bj58.etx.boot.init.EtxInit;
import com.bj58.etx.demo.componet.AsyncComponet;
import com.bj58.etx.demo.componet.MonitorAsyncComponet;
import com.bj58.etx.demo.componet.SyncComponet;
import com.bj58.etx.demo.componet.TCC1Componet;
import com.bj58.etx.demo.componet.TCC2Componet;
import com.bj58.etx.demo.dto.TestDto;
import com.bj58.etx.demo.vo.TestVo;

public class Main {
	public static void main(String[] args) {
		EtxInit.init("d:/etx/config/etx.properties");
		
		TestDto dto = new TestDto();
		Long testId = 100L;
		
		Etx.open()
		.setDto(dto)
		.setVo(TestVo.class)
		.setFlowType("TEST_BUSSINESS")
		.addComponet(SyncComponet.class)
		.addComponet(TCC1Componet.class)       
		.addComponet(TCC2Componet.class)           
		.addComponet(MonitorAsyncComponet.class)        
		.addComponet(AsyncComponet.class)            
		.invoke(testId);
	}
}
