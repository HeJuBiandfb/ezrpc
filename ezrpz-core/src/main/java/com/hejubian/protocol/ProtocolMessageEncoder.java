package com.hejubian.protocol;


import com.hejubian.serializer.Serializer;
import com.hejubian.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;

import java.io.IOException;

/**
 * 协议消息编码器
 */
public class ProtocolMessageEncoder {
    /**
     * 编码
     * @param message
     * @return
     * @throws IOException
     */
    public static Buffer encode(ProtocolMessage<?> message) throws IOException {
        if (message == null || message.getHeader() == null) {
            return Buffer.buffer();
        }
        ProtocolMessage.Header header = message.getHeader();
        Buffer buffer = Buffer.buffer();
        buffer.appendByte(header.getMagic());
        buffer.appendByte(header.getVersion());
        buffer.appendByte(header.getSerializer());
        buffer.appendByte(header.getType());
        buffer.appendByte(header.getStatus());
        buffer.appendLong(header.getRequestId());
        //获取序列化器
        ProtocolMessageSerializerEnum enumByKey = ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer());
        if (enumByKey == null) {
            throw new RuntimeException("序列化协议不存在");
        }
        // 进行序列化
        Serializer serializer = SerializerFactory.getSerializer(enumByKey.getValue());
        byte[] bodyBytes = serializer.serialize(message.getBody());
        // 消息体长度
        buffer.appendInt(bodyBytes.length);
        buffer.appendBytes(bodyBytes);
        return buffer;
    }
}
