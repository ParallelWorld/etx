package com.bj58.etx.boot.dao.mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bj58.etx.api.db.EtxAsyncLog;
import com.bj58.etx.api.db.EtxSyncLog;
import com.bj58.etx.api.db.EtxTX;

public class RsMapper {

	public static void fillSt(EtxTX tx, PreparedStatement st, boolean idFirst) {
		if (tx == null) {
			return;
		}
		try {
			if (idFirst) {
				st.setLong(1, tx.getId());
				st.setString(2, tx.getFlowType());
				st.setInt(3, tx.getState());
				st.setLong(4, tx.getAddTime());
				st.setLong(5, tx.getModifyTime());
			} else {
				st.setString(1, tx.getFlowType());
				st.setInt(2, tx.getState());
				st.setLong(3, tx.getAddTime());
				st.setLong(4, tx.getModifyTime());
				st.setLong(5, tx.getId());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void fillSt(EtxSyncLog log, PreparedStatement st,
			boolean idFirst) {
		if (log == null) {
			return;
		}
		try {
			if (idFirst) {
				st.setLong(1, log.getId());
				st.setLong(2, log.getTxId());
				st.setString(3, log.getComponet());
				st.setBytes(4, log.getData());
				st.setString(5, log.getState());
				st.setLong(6, log.getAddTime());
				st.setLong(7, log.getModifyTime());
				st.setInt(8, log.getCancelCount());
			} else {
				st.setLong(1, log.getTxId());
				st.setString(2, log.getComponet());
				st.setBytes(3, log.getData());
				st.setString(4, log.getState());
				st.setLong(5, log.getAddTime());
				st.setLong(6, log.getModifyTime());
				st.setInt(7, log.getCancelCount());
				st.setLong(8, log.getId());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void fillSt(EtxAsyncLog log, PreparedStatement st,
			boolean idFirst) {
		if (log == null) {
			return;
		}
		try {
			if (idFirst) {
				st.setLong(1, log.getId());
				st.setLong(2, log.getTxId());
				st.setString(3, log.getComponet());
				st.setBytes(4, log.getData());
				st.setString(5, log.getState());
				st.setLong(6, log.getAddTime());
				st.setLong(7, log.getModifyTime());
				st.setInt(8, log.getCount());
			}else {
				st.setLong(1, log.getTxId());
				st.setString(2, log.getComponet());
				st.setBytes(3, log.getData());
				st.setString(4, log.getState());
				st.setLong(5, log.getAddTime());
				st.setLong(6, log.getModifyTime());
				st.setInt(7, log.getCount());
				st.setLong(8, log.getId());
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static EtxTX toTx(ResultSet rs) {
		if (rs == null) {
			return null;
		}
		EtxTX tx = new EtxTX();
		try {
			tx.setId(rs.getLong("id"));
			tx.setFlowType(rs.getString("flowtype"));
			tx.setState(rs.getInt("state"));
			tx.setAddTime(rs.getLong("addtime"));
			tx.setModifyTime(rs.getLong("modifytime"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tx;
	}

	public static EtxSyncLog toSyncLog(ResultSet rs) {
		if (rs == null) {
			return null;
		}
		EtxSyncLog log = new EtxSyncLog();
		try {
			log.setId(rs.getLong("id"));
			log.setTxId(rs.getLong("txid"));
			log.setComponet(rs.getString("componet"));
			log.setData(rs.getBytes("data"));
			log.setState(rs.getString("state"));
			log.setAddTime(rs.getLong("addtime"));
			log.setModifyTime(rs.getLong("modifytime"));
			log.setCancelCount(rs.getInt("cancelcount"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return log;
	}

	public static EtxAsyncLog toAsyncLog(ResultSet rs) {
		if (rs == null) {
			return null;
		}
		EtxAsyncLog log = new EtxAsyncLog();
		try {
			log.setId(rs.getLong("id"));
			log.setTxId(rs.getLong("txid"));
			log.setComponet(rs.getString("componet"));
			log.setData(rs.getBytes("data"));
			log.setState(rs.getString("state"));
			log.setAddTime(rs.getLong("addtime"));
			log.setModifyTime(rs.getLong("modifytime"));
			log.setCount(rs.getInt("count"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return log;
	}

}
