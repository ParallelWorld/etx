package com.bj58.zhaopin.jianli.etx.core.dao;

import java.util.List;

import com.bj58.zhaopin.jianli.etx.api.db.EtxAsyncLog;
import com.bj58.zhaopin.jianli.etx.api.db.EtxSyncLog;
import com.bj58.zhaopin.jianli.etx.api.db.EtxTX;
import com.bj58.zhaopin.jianli.etx.api.db.IEtxDao;
import com.bj58.zhaopin.jianli.etx.api.enums.EtxTXStateEnum;

public class MangoDao implements IEtxDao{

	@Override
	public EtxTX getTxById(long txId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EtxAsyncLog getAsyncLogById(long logId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EtxSyncLog getSyncLogById(long logId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long insertTx(EtxTX tx) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long insertAsyncLog(EtxAsyncLog log) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long insertSyncLog(EtxSyncLog log) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<EtxTX> getTxList(EtxTXStateEnum state,int pageSize) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<EtxAsyncLog> getAsyncLogList(long txId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<EtxSyncLog> getSyncLogList(long txId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateTx(EtxTX tx) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateSyncLog(EtxSyncLog log) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateAsyncLog(EtxAsyncLog log) throws Exception {
		// TODO Auto-generated method stub
		
	}

	
}