package com.hejubian.protocol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消息结构类
 * @param <T>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProtocolMessage<T> {
    /**
     * 消息头
     */
    private Header header;
    /**
     * 消息体
     */
    private T body;
    /**
     * 消息头结构体
     */
    @Data
    public static class Header {
        /**
         * 魔数，保证协议的正确性
         */
        private byte magic;
        /**
         * 版本号
         */
        private byte version;
        /**
         * 序列化器
         */
        private byte serializer;
        /**
         * 消息类型（请求/响应/心跳等）
         */
        private byte type;
        /**
         * 状态
         */
        private byte status;
        /**
         * 请求 ID
         */
        private long requestId;
        /**
         * 消息体长度
         */
        private int bodyLength;
    }
}
