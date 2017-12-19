package com.bj58.etx.api.enums;

/**
 * ext 运行的三种模式 
 * PROCESS模式：完全在当前进程里运行
 * DB模式：异步任务会放在数据库里，由定时任务滞后执行。
 * BINLOG模式：每一条执行过程都落地为日志，拥有最高级别的一致性。
 * 
 * @author shencl
 *
 */
public enum EtxRunMode {

	PROCESS(1, "进程模式"), DB(2, "DB模式"), BINLOG(3, "BINLOG模式");

	private int code;
	private String msg;

	private EtxRunMode(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

	public static EtxRunMode byCode(int code) {
		EtxRunMode[] modes = EtxRunMode.values();
		for (EtxRunMode mode : modes) {
			if (mode.getCode() == code) {
				return mode;
			}
		}
		return null;
	}

}
