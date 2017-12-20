package com.bj58.etx.boot.dao;

import com.bj58.etx.api.db.EtxAsyncLog;
import com.bj58.etx.api.db.EtxSyncLog;
import com.bj58.etx.api.db.EtxTX;
import com.bj58.etx.api.db.IEtxDao;
import com.bj58.etx.api.enums.EtxTXStateEnum;
import com.bj58.etx.boot.dao.mysql.RsMapper;
import com.bj58.etx.boot.helper.IdHelper;
import com.bj58.etx.boot.helper.MysqlHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class MysqlDao implements IEtxDao {

	@Override
	public EtxTX getTxById(long txId) throws Exception {
		Connection conn = MysqlHelper.getConn();
		String sql = "select * from t_tx where `id` = ?";
		PreparedStatement st = conn.prepareStatement(sql);
		st.setLong(1, txId);
		ResultSet rs = st.executeQuery();
		while (rs.next()) {
			return RsMapper.toTx(rs);
		}
		MysqlHelper.releaseConn(conn);
		return null;
	}

	@Override
	public EtxAsyncLog getAsyncLogById(long txId, long logId) throws Exception {
		Connection conn = MysqlHelper.getConn();
		String sql = "select * from t_async_log where `id` = ?";
		PreparedStatement st = conn.prepareStatement(sql);
		st.setLong(1, logId);
		ResultSet rs = st.executeQuery();
		while (rs.next()) {
			return RsMapper.toAsyncLog(rs);
		}
		MysqlHelper.releaseConn(conn);
		return null;
	}

	@Override
	public EtxSyncLog getSyncLogById(long txId, long logId) throws Exception {
		Connection conn = MysqlHelper.getConn();
		String sql = "select * from t_sync_log where `id` = ?";
		PreparedStatement st = conn.prepareStatement(sql);
		st.setLong(1, logId);
		ResultSet rs = st.executeQuery();
		while (rs.next()) {
			return RsMapper.toSyncLog(rs);
		}
		MysqlHelper.releaseConn(conn);
		return null;
	}
	@Override
	public long insertTx(EtxTX tx) throws Exception {
		long id = IdHelper.genId();
		tx.setId(id);
		Connection conn = MysqlHelper.getConn();
		String sql = "INSERT INTO t_tx(`id`,`flowtype`,`state`,`addtime`,`modifytime`) VALUES (?, ?, ?, ?, ?)";
		PreparedStatement st = conn.prepareStatement(sql);
		RsMapper.fillSt(tx, st,true);
		st.executeUpdate();
		MysqlHelper.releaseConn(conn);
		return id;
	}

	@Override
	public long insertAsyncLog(EtxAsyncLog log) throws Exception {
		long id = IdHelper.genId();
		log.setId(id);
		Connection conn = MysqlHelper.getConn();
		String sql = "INSERT INTO t_async_log(`id`,`txid`,`componet`,`data`,`state`,`addtime`,`modifytime`,`count`) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		PreparedStatement st = conn.prepareStatement(sql);
		RsMapper.fillSt(log, st,true);
		st.executeUpdate();
		MysqlHelper.releaseConn(conn);
		return id;
	}
	@Override
	public long insertSyncLog(EtxSyncLog log) throws Exception {
		long id = IdHelper.genId();
		log.setId(id);
		Connection conn = MysqlHelper.getConn();
		String sql = "INSERT INTO t_sync_log(`id`,`txid`,`componet`,`data`,`state`,`addtime`,`modifytime`,`cancelcount`) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		PreparedStatement st = conn.prepareStatement(sql);
		RsMapper.fillSt(log, st,true);
		st.executeUpdate();
		MysqlHelper.releaseConn(conn);
		return id;
	}

	@Override
	public List<EtxTX> getTxList(EtxTXStateEnum state, int pageSize)
			throws Exception {
		Connection conn = MysqlHelper.getConn();
		String sql = "select * from t_async_log where `state` = ? limit 0,?";
		PreparedStatement st = conn.prepareStatement(sql);
		st.setInt(1, state.getCode());
		st.setLong(2, pageSize);
		ResultSet rs = st.executeQuery();
		List<EtxTX> list = new ArrayList<EtxTX>();
		while (rs.next()) {
			list.add(RsMapper.toTx(rs));
		}
		MysqlHelper.releaseConn(conn);
		return list;
	}

	/**
	 * 此list查询出来用于做未完成的异步组件，所以和插入序一致
	 */
	@Override
	public List<EtxAsyncLog> getAsyncLogList(long txId) throws Exception {
		Connection conn = MysqlHelper.getConn();
		String sql = "select * from t_async_log where `txid` = ? order by id desc";
		PreparedStatement st = conn.prepareStatement(sql);
		st.setLong(1, txId);
		ResultSet rs = st.executeQuery();
		List<EtxAsyncLog> list = new ArrayList<EtxAsyncLog>();
		while (rs.next()) {
			list.add(RsMapper.toAsyncLog(rs));
		}
		MysqlHelper.releaseConn(conn);
		return list;
	}

	/**
	 * 此list查询出来用于做回滚操作，所以是插入序的倒序
	 */
	@Override
	public List<EtxSyncLog> getSyncLogList(long txId) throws Exception {
		Connection conn = MysqlHelper.getConn();
		String sql = "select * from t_sync_log where txid = ? order by id desc";
		PreparedStatement st = conn.prepareStatement(sql);
		st.setLong(1, txId);
		ResultSet rs = st.executeQuery();
		List<EtxSyncLog> list = new ArrayList<EtxSyncLog>();
		while (rs.next()) {
			list.add(RsMapper.toSyncLog(rs));
		}
		MysqlHelper.releaseConn(conn);
		return list;
	}

	@Override
	public void updateTx(EtxTX tx) throws Exception {
		Connection conn = MysqlHelper.getConn();
		String sql = "UPDATE t_tx SET `flowtype` = ?,`state` = ?,`addtime`=?,`modifytime` =? WHERE id = ?";
		PreparedStatement st = conn.prepareStatement(sql);
		RsMapper.fillSt(tx, st,false);
		st.executeUpdate();
		MysqlHelper.releaseConn(conn);
	}

	
	@Override
	public void updateSyncLog(EtxSyncLog log) throws Exception {
		Connection conn = MysqlHelper.getConn();
		String sql = "UPDATE t_sync_log SET `txid` = ?,`componet` = ?,`data`=?,`state` =?,`addtime`=?,`modifytime`=?,`cancelcount`=? WHERE id = ?";
		PreparedStatement st = conn.prepareStatement(sql);
		RsMapper.fillSt(log, st,false);
		st.executeUpdate();
		MysqlHelper.releaseConn(conn);
	}

	@Override
	public void updateAsyncLog(EtxAsyncLog log) throws Exception {
		Connection conn = MysqlHelper.getConn();
		String sql = "UPDATE t_async_log SET `txid` = ?,`componet` = ?,`data`=?,`state` =?,`addtime`=?,`modifytime`=?,`count`=? WHERE id = ?";
		PreparedStatement st = conn.prepareStatement(sql);
		RsMapper.fillSt(log, st,false);
		st.executeUpdate();
		MysqlHelper.releaseConn(conn);
	}
}
