package com.bj58.etx.boot.dao;

import java.util.ArrayList;
import java.util.List;

import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.bj58.etx.api.db.EtxAsyncLog;
import com.bj58.etx.api.db.EtxSyncLog;
import com.bj58.etx.api.db.EtxTX;
import com.bj58.etx.api.db.IEtxDao;
import com.bj58.etx.api.enums.EtxTXStateEnum;
import com.bj58.etx.boot.dao.mongo.DocMaper;
import com.bj58.etx.boot.helper.IdHelper;
import com.bj58.etx.boot.helper.MongoHelper;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

public class MongoDao implements IEtxDao {

	@Override
	public EtxTX getTxById(long txId) throws Exception {
		Document doc = null;
		MongoCursor<Document> cursor = MongoHelper.getTXTable(txId).find(Filters.eq("_id", txId)).iterator();
		while (cursor.hasNext()) {
			doc = cursor.next();
			break;
		}

		return DocMaper.toTx(doc);
	}

	@Override
	public EtxAsyncLog getAsyncLogById(long txId,long logId) throws Exception {
		Document doc = null;
		MongoCursor<Document> cursor = MongoHelper.getAsyncLogTable(txId).find(Filters.eq("_id", logId)).iterator();
		while (cursor.hasNext()) {
			doc = cursor.next();
			break;
		}

		return DocMaper.toAsyncLog(doc);
	}

	@Override
	public EtxSyncLog getSyncLogById(long txId,long logId) throws Exception {
		Document doc = null;
		MongoCursor<Document> cursor = MongoHelper.getSyncLogTable(txId).find(Filters.eq("_id", logId)).iterator();
		while (cursor.hasNext()) {
			doc = cursor.next();
			break;
		}

		return DocMaper.toSyncLog(doc);
	}

	@Override
	public long insertTx(EtxTX tx) throws Exception {
		long id = IdHelper.genId();
		tx.setId(id);
		Document doc = DocMaper.toDoc(tx);
		MongoHelper.getTXTable(id).insertOne(doc);
		return id;
	}

	@Override
	public long insertAsyncLog(EtxAsyncLog log) throws Exception {
		long id = IdHelper.genId();
		log.setId(id);
		Document doc = DocMaper.toDoc(log);
		MongoHelper.getAsyncLogTable(log.getTxId()).insertOne(doc);
		return id;
	}

	@Override
	public long insertSyncLog(EtxSyncLog log) throws Exception {
		long id = IdHelper.genId();
		log.setId(id);
		Document doc = DocMaper.toDoc(log);
		MongoHelper.getSyncLogTable(log.getTxId()).insertOne(doc);
		return id;
	}

	@Override
	public List<EtxTX> getTxList(EtxTXStateEnum state, int pageSize) throws Exception {
		List<EtxTX> list = new ArrayList<EtxTX>();
		Bson query = new BsonDocument();
		query = Filters.eq("state", state.getCode());
		// 聚合最近 N张表的数据
		for (int i = 0; i < MongoHelper.AGG_TABLE_COUNT; i++) {
			MongoCursor<Document> cursor = MongoHelper.getLastTXTable(i).find(query).limit(pageSize).iterator();
			while (cursor.hasNext()) {
				Document doc = cursor.next();
				list.add(DocMaper.toTx(doc));
			}
		}

		return list;
	}

	@Override
	public List<EtxAsyncLog> getAsyncLogList(long txId) throws Exception {
		List<EtxAsyncLog> list = new ArrayList<EtxAsyncLog>();
		Bson query = new BsonDocument();
		query = Filters.eq("txid", txId);
		Bson sort = new BsonDocument();
		sort = Filters.eq("_id", 1); // 1 表示升序
		MongoCursor<Document> cursor = MongoHelper.getAsyncLogTable(txId).find(query).sort(sort).iterator();
		while (cursor.hasNext()) {
			Document doc = cursor.next();
			list.add(DocMaper.toAsyncLog(doc));
		}
		return list;
	}

	@Override
	public List<EtxSyncLog> getSyncLogList(long txId) throws Exception {
		List<EtxSyncLog> list = new ArrayList<EtxSyncLog>();
		Bson query = new BsonDocument();
		query = Filters.eq("txid", txId);
		Bson sort = new BsonDocument();
		sort = Filters.eq("_id", -1); // -1 表示降序
		MongoCursor<Document> cursor = MongoHelper.getSyncLogTable(txId).find(query).sort(sort).iterator();
		while (cursor.hasNext()) {
			Document doc = cursor.next();
			list.add(DocMaper.toSyncLog(doc));
		}
		return list;
	}

	@Override
	public void updateTx(EtxTX tx) throws Exception {
		Document doc = DocMaper.toDoc(tx);
		Document filter = new Document();
		filter.append("_id", tx.getId());
		MongoHelper.getTXTable(tx.getId()).replaceOne(filter, doc);
	}

	@Override
	public void updateSyncLog(EtxSyncLog log) throws Exception {
		Document doc = DocMaper.toDoc(log);
		Document filter = new Document();
		filter.append("_id", log.getId());
		MongoHelper.getSyncLogTable(log.getTxId()).replaceOne(filter, doc);
	}

	@Override
	public void updateAsyncLog(EtxAsyncLog log) throws Exception {
		Document doc = DocMaper.toDoc(log);
		Document filter = new Document();
		filter.append("_id", log.getId());
		MongoHelper.getAsyncLogTable(log.getTxId()).replaceOne(filter, doc);
	}
}