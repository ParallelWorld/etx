package com.bj58.zhaopin.jianli.etx.core.helper;

import com.bj58.zhaopin.jianli.common.utils.IdUtil;
import com.bj58.zhaopin.jianli.dao.SingleDaoHandler;

public class DaoHelper {

	public static SingleDaoHandler daoHelper = null;
	private static int machineNo = 1;


	public static int getMachineNo() {
		return machineNo;
	}
	
	
	public static void init(String path) {
		try {
			daoHelper = SingleDaoHandler.createDaoHandler(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static long genId() {
		return IdUtil.genId(machineNo);
	}
}
