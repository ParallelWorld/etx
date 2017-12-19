package com.bj58.etx.core.util;

public class EtxIdUtil {
	private static long lastTimeStamp = -1L;
	private static long begin = 1483200000000L; //2017-01-01 00:00:00

	/**
	 *
	 * 12位时间戳+6位机器id，高位补0
	 */
	public synchronized static long genId(int serverId) {
		if (serverId > 63 || serverId < 0) {
			throw new RuntimeException("serverId must between 0 and 63");
		}
		
		long timestamp = System.currentTimeMillis();
		if (timestamp < begin) {
			throw new RuntimeException("clock error!");
		}

		if (timestamp == lastTimeStamp) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		timestamp = System.currentTimeMillis();
		long seq = timestamp - begin;

		long id = (seq << 6) | serverId;
		lastTimeStamp = timestamp;

		return id;
	}
	
	
	/**
	 * 根据id获取时间戳，此方法依赖id生成策略
	 */
	public static long getTimestamp(long id) {
		return (id >> 6) + begin;
	}
}
