package com.bj58.zhaopin.jianli.etx.api.db;

import java.util.List;

import com.bj58.zhaopin.jianli.etx.api.enums.EtxTXStateEnum;

public interface IEtxDao {

	/**
	 * 获取事务组实体
	 */
	public EtxTX getTxById(long txId) throws Exception;

	/**
	 * 获取异步日志实体
	 */
	public EtxAsyncLog getAsyncLogById(long logId) throws Exception;

	/**
	 * 获取同步Log实体
	 */
	public EtxSyncLog getSyncLogById(long logId) throws Exception;

	/**
	 * 插入事务组记录
	 */
	public long insertTx(EtxTX tx) throws Exception;

	/**
	 * 插入执行明细
	 */
	public long insertAsyncLog(EtxAsyncLog log) throws Exception;

	/**
	 * 插入同步Log
	 */
	public long insertSyncLog(EtxSyncLog log) throws Exception;

	/**
	 * 根据状态获取事务组记录
	 */
	public List<EtxTX> getTxList(EtxTXStateEnum state,int pageSize) throws Exception;

	/**
	 * 获取事务组的异步log
	 */
	public List<EtxAsyncLog> getAsyncLogList(long txId) throws Exception;

	/**
	 * 获取事务组的tccLog
	 */
	public List<EtxSyncLog> getSyncLogList(long txId) throws Exception;

	/**
	 * 修改事务组状态
	 */
	public void updateTx(EtxTX tx) throws Exception;

	/**
	 * 修改同步日志
	 */
	public void updateSyncLog(EtxSyncLog log) throws Exception;

	/**
	 * 修改异步日志
	 */
	public void updateAsyncLog(EtxAsyncLog log) throws Exception;

}
