package com.bj58.zhaopin.jianli.etx.core.dao;

import java.util.ArrayList;
import java.util.List;

import com.bj58.zhaopin.jianli.etx.api.db.EtxAsyncLog;
import com.bj58.zhaopin.jianli.etx.api.db.EtxSyncLog;
import com.bj58.zhaopin.jianli.etx.api.db.EtxTX;
import com.bj58.zhaopin.jianli.etx.api.db.IEtxDao;
import com.bj58.zhaopin.jianli.etx.api.enums.EtxTXStateEnum;
import com.bj58.zhaopin.jianli.etx.core.entity.AsyncLogShadow;
import com.bj58.zhaopin.jianli.etx.core.entity.SyncLogShadow;
import com.bj58.zhaopin.jianli.etx.core.entity.TXShadow;
import com.bj58.zhaopin.jianli.etx.core.helper.BeanHelper;
import com.bj58.zhaopin.jianli.etx.core.helper.DaoHelper;

public class MysqlDao implements IEtxDao {

	static {
		DaoHelper.init("d:/db1.properties");
	}

	@Override
	public EtxTX getTxById(long txId) throws Exception {
		TXShadow tx = (TXShadow) DaoHelper.daoHelper.get(TXShadow.class, txId);
		return BeanHelper.convert(tx);
	}

	@Override
	public EtxAsyncLog getAsyncLogById(long logId) throws Exception {
		AsyncLogShadow log = (AsyncLogShadow) DaoHelper.daoHelper.get(AsyncLogShadow.class, logId);
		return BeanHelper.convert(log);
	}

	@Override
	public EtxSyncLog getSyncLogById(long logId) throws Exception {
		SyncLogShadow log = (SyncLogShadow) DaoHelper.daoHelper.get(SyncLogShadow.class, logId);
		return BeanHelper.convert(log);
	}

	@Override
	public long insertTx(EtxTX tx) throws Exception {
		long id = DaoHelper.genId();
		tx.setId(id);

		TXShadow bean = BeanHelper.convert(tx);
		DaoHelper.daoHelper.insert(bean);
		return id;
	}

	@Override
	public long insertAsyncLog(EtxAsyncLog log) throws Exception {
		long id = DaoHelper.genId();
		log.setId(id);

		AsyncLogShadow bean = BeanHelper.convert(log);
		DaoHelper.daoHelper.insert(bean);
		return id;
	}

	@Override
	public long insertSyncLog(EtxSyncLog log) throws Exception {
		long id = DaoHelper.genId();
		log.setId(id);

		SyncLogShadow bean = BeanHelper.convert(log);
		DaoHelper.daoHelper.insert(bean);
		return id;
	}

	@Override
	public List<EtxTX> getTxList(EtxTXStateEnum state, int pageSize) throws Exception {
		List<TXShadow> list = DaoHelper.daoHelper.getPageList(TXShadow.class, "", "state=?", "", 1, pageSize, new Object[] { state.getCode() });
		List<EtxTX> result = new ArrayList<EtxTX>(list.size());
		if (list != null) {
			for (TXShadow e : list) {
				result.add(BeanHelper.convert(e));
			}
		}
		return result;
	}

	@Override
	public List<EtxAsyncLog> getAsyncLogList(long txId) throws Exception {
		List<AsyncLogShadow> list = DaoHelper.daoHelper.getPageList(AsyncLogShadow.class, "", "tx_id=?", "", 1, 100, new Object[] { txId });
		List<EtxAsyncLog> result = new ArrayList<EtxAsyncLog>(list.size());
		if (list != null) {
			for (AsyncLogShadow e : list) {
				result.add(BeanHelper.convert(e));
			}
		}
		return result;
	}

	@Override
	public List<EtxSyncLog> getSyncLogList(long txId) throws Exception {
		List<SyncLogShadow> list = DaoHelper.daoHelper.getPageList(SyncLogShadow.class, "", "tx_id=?", "", 1, 100, new Object[] { txId });
		List<EtxSyncLog> result = new ArrayList<EtxSyncLog>(list.size());
		if (list != null) {
			for (SyncLogShadow e : list) {
				result.add(BeanHelper.convert(e));
			}
		}
		return result;
	}

	@Override
	public void updateTx(EtxTX tx) throws Exception {
		TXShadow bean = BeanHelper.convert(tx);
		DaoHelper.daoHelper.update(bean);
	}

	@Override
	public void updateSyncLog(EtxSyncLog log) throws Exception {
		SyncLogShadow bean = BeanHelper.convert(log);
		DaoHelper.daoHelper.update(bean);
	}

	@Override
	public void updateAsyncLog(EtxAsyncLog log) throws Exception {
		AsyncLogShadow bean = BeanHelper.convert(log);
		DaoHelper.daoHelper.update(bean);
	}

}
