package com.bj58.etx.demo;

import com.bj58.etx.boot.Etx;
import com.bj58.etx.boot.init.EtxInit;

public class TaskMain {
	public static void main(String[] args) {
		EtxInit.init("/opt/etx.properties");
		
		Etx.startTask();
	}
}
