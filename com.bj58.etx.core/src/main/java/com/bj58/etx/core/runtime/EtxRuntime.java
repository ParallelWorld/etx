package com.bj58.etx.core.runtime;

import com.bj58.etx.api.db.IEtxDao;
import com.bj58.etx.api.monitor.IEtxMonitor;
import com.bj58.etx.api.serialize.IEtxSerializer;

public class EtxRuntime {
	// 持久化类
	public static IEtxDao dao = null;

	// 序列化类
	public static IEtxSerializer serializer = null;
	
	// 监控类
	public static IEtxMonitor monitor = null;

	// 心跳检测时间
	public static int dbHeatBeatInterval = 5 * 60 * 1000;
	
	// db异常出现的最大次数
	public static int dbMaxFailCount = 3;
	
	// 进程内执行异步任务的线程数
	public static int processThreadCount = 4;
	
	// task执行任务的线程数
	public static int taskThreadCount = 4;
	
	// task日志流水时间(毫秒)
	public static int taskLoopTxInterval = 5 * 60 * 1000;
	
	// 单次从同步记录表里获取的记录条数
	public static int countForCancelOnce = 2000;
	
	// 单次从异步记录表里获取的记录条数
	public static int countForDoAsyncOnce = 2000;

	public static void setDao(IEtxDao dao) {
		EtxRuntime.dao = dao;
	}

	public static void setSerializer(IEtxSerializer serializer) {
		EtxRuntime.serializer = serializer;
	}
	
	public static void setMonitor(IEtxMonitor monitor) {
		EtxRuntime.monitor = monitor;
	}

	public static void setDbHeatBeatInterval(int dbHeatBeatInterval) {
		EtxRuntime.dbHeatBeatInterval = dbHeatBeatInterval;
	}

	public static void setDbMaxFailCount(int dbMaxFailCount) {
		EtxRuntime.dbMaxFailCount = dbMaxFailCount;
	}

	public static void setProcessThreadCount(int processThreadCount) {
		EtxRuntime.processThreadCount = processThreadCount;
	}

	public static void setTaskThreadCount(int taskThreadCount) {
		EtxRuntime.taskThreadCount = taskThreadCount;
	}

	public static void setTaskLoopTxInterval(int taskLoopTxInterval) {
		EtxRuntime.taskLoopTxInterval = taskLoopTxInterval;
	}

	public static void setCountForCancelOnce(int countForCancelOnce) {
		EtxRuntime.countForCancelOnce = countForCancelOnce;
	}

	public static void setCountForDoAsyncOnce(int countForDoAsyncOnce) {
		EtxRuntime.countForDoAsyncOnce = countForDoAsyncOnce;
	}
	
}
