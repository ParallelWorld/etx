package com.bj58.etx.core.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EtxDateTimeUtil {
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
	
	/**
	 * 获取当前时间戳
	 */
	public static long getFullTimestamp(){
		Date now = new Date();
		return Long.valueOf(sdf.format(now));
	}
	
	/**
	 * 根据时间戳获取年月日
	 */
	public static String getYYYYMMDD(Date date){
		return sdf1.format(date);
	}
}