package com.bj58.etx.boot.dao.mongo;

import com.bj58.etx.api.db.EtxAsyncLog;
import com.bj58.etx.api.db.EtxSyncLog;
import com.bj58.etx.api.db.EtxTX;
import org.bson.Document;
import org.bson.types.Binary;


public class DocMaper {

    public static Document toDoc(EtxTX tx) {
        if (tx == null) {
            return null;
        }
        Document doc = new Document();
        doc.append("_id", tx.getId());
        doc.append("flowtype", tx.getFlowType());
        doc.append("state", tx.getState());
        doc.append("addtime", tx.getAddTime());
        doc.append("modifytime", tx.getModifyTime());
        return doc;
    }

    public static Document toDoc(EtxSyncLog log) {
        if (log == null) {
            return null;
        }
        Document doc = new Document();
        doc.append("_id", log.getId());
        doc.append("txid", log.getTxId());
        doc.append("componet", log.getComponet());
        doc.append("data", log.getData());
        doc.append("state", log.getState());
        doc.append("addtime", log.getAddTime());
        doc.append("modifytime", log.getModifyTime());
        doc.append("cancelcount", log.getCancelCount());
        return doc;
    }

    public static Document toDoc(EtxAsyncLog log) {
        if (log == null) {
            return null;
        }
        Document doc = new Document();
        doc.append("_id", log.getId());
        doc.append("txid", log.getTxId());
        doc.append("componet", log.getComponet());
        doc.append("data", log.getData());
        doc.append("state", log.getState());
        doc.append("addtime", log.getAddTime());
        doc.append("modifytime", log.getModifyTime());
        doc.append("count", log.getCount());
        return doc;
    }

    public static EtxTX toTx(Document doc) {
        if (doc == null) {
            return null;
        }
        EtxTX tx = new EtxTX();
        tx.setId(doc.getLong("_id"));
        tx.setFlowType(doc.getString("flowtype"));
        tx.setState(doc.getInteger("state"));
        tx.setAddTime(doc.getLong("addtime"));
        tx.setModifyTime(doc.getLong("modifytime"));
        return tx;
    }

    public static EtxSyncLog toSyncLog(Document doc) {
        EtxSyncLog log = new EtxSyncLog();
        if (doc == null) {
            return null;
        }

        log.setId(doc.getLong("_id"));
        log.setTxId(doc.getLong("txtd"));
        log.setComponet(doc.getString("componet"));
        log.setData(doc.get("data", Binary.class).getData());
        log.setState(doc.getString("state"));
        log.setAddTime(doc.getLong("addtime"));
        log.setModifyTime(doc.getLong("modifytime"));
        log.setCancelCount(doc.getInteger("cancelcount"));
        return log;
    }

    public static EtxAsyncLog toAsyncLog(Document doc) {
        if (doc == null) {
            return null;
        }
        EtxAsyncLog log = new EtxAsyncLog();
        log.setId(doc.getLong("_id"));
        log.setTxId(doc.getLong("txid"));
        log.setComponet(doc.getString("componet"));
        log.setData(doc.get("data", Binary.class).getData());
        log.setState(doc.getString("state"));
        log.setAddTime(doc.getLong("addtime"));
        log.setModifyTime(doc.getLong("modifytime"));
        log.setCount(doc.getInteger("count"));
        return log;
    }

}
