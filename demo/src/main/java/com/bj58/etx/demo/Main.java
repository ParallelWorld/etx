package com.bj58.etx.demo;

import com.bj58.etx.core.Etx;
import com.bj58.etx.core.EtxInit;
import com.bj58.etx.demo.componet.*;
import com.bj58.etx.demo.dto.TestDto;
import com.bj58.etx.demo.vo.TestVo;

import java.net.URL;

public class Main {
    public static void main(String[] args) {
        URL url = Main.class.getClassLoader().getResource("etx.properties");
        EtxInit.init(url.getPath());

        TestDto dto = new TestDto();
        Long testId = 100L;

        Etx.open().setDto(dto).setVo(TestVo.class)
                .setFlowType("TEST_BUSSINESS")
                .addComponet(SyncComponet.class)
                .addComponet(TCC1Componet.class)
                .addComponet(TCC2Componet.class)
                .addComponet(MonitorAsyncComponent.class)
                .addComponet(AsyncComponet.class)
                .invoke(testId);
    }
}
