package com.bj58.etx.boot.serialize;

import com.bj58.etx.api.context.IEtxContext;
import com.bj58.etx.api.exception.EtxException;
import com.bj58.etx.api.serialize.IEtxSerializer;
import com.bj58.etx.boot.context.EtxBaseContext;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ProtoBufSerializer implements IEtxSerializer {

    private static Log log = LogFactory.getLog(ProtoBufSerializer.class);
    private static Schema<EtxBaseContext> schema = null;

    static {
        schema = RuntimeSchema.getSchema(EtxBaseContext.class);
    }

    @Override
    public byte[] serialize(IEtxContext ctx) {
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            EtxBaseContext bctx = (EtxBaseContext) ctx;
            return ProtobufIOUtil.toByteArray(bctx, schema, buffer);
        } catch (Exception e) {
            log.error("序列化失败", e);
            throw new EtxException("序列化失败");
        } finally {
            buffer.clear();
        }
    }

    @Override
    public IEtxContext deSerialize(byte[] bs) {
        EtxBaseContext obj = null;
        try {
            ProtostuffIOUtil.mergeFrom(bs, obj, schema);
        } catch (Exception e) {
            log.error("反序列化失败", e);
            throw new EtxException("序列化失败");
        }
        return obj;
    }

}
