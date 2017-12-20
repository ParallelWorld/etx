package com.bj58.etx.boot.serialize;

import com.bj58.etx.api.context.IEtxContext;
import com.bj58.etx.api.exception.EtxException;
import com.bj58.etx.api.serialize.IEtxSerializer;
import com.bj58.etx.core.context.EtxBaseContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SystemSerializer implements IEtxSerializer {

    private static Logger log = LoggerFactory.getLogger(SystemSerializer.class);

    @Override
    public byte[] serialize(IEtxContext ctx) {
        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = null;
        try {
            // 序列化
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(ctx);
            byte[] bytes = baos.toByteArray();
            return bytes;
        } catch (Exception e) {
            log.error("序列化失败", e);
            throw new EtxException("序列化失败");
        }
    }

    @Override
    public IEtxContext deSerialize(byte[] bs) {
        ByteArrayInputStream bais = null;
        try {
            // 反序列化
            bais = new ByteArrayInputStream(bs);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (EtxBaseContext) ois.readObject();
        } catch (Exception e) {
            log.error("反序列化失败", e);
            throw new EtxException("反序列化失败");
        }
    }
}
