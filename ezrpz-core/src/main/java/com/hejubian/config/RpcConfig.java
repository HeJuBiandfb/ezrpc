package com.hejubian.config;

import com.hejubian.serializer.SerializerKeys;
import lombok.Data;

/**
 *   RPC 配置类
 */
@Data
public class RpcConfig {
    /**
     *  服务名称
     */
    private String name = "ezrpc";
    /**
     *  版本号
     */
    private String version = "1.0.0";
    /**
     *  服务器地址
     */
    private String serverHost = "localhost";
    /**
     *  服务器端口号
     */
    private Integer serverPort = 8080;
    /**
     * 模拟调用
     */
    private boolean mock = false;
    /**
     * 序列化器
     */
    private String serializer = SerializerKeys.JDK;
    /**
     * 注册中心配置
     */
    private RegistryConfig registryConfig = new RegistryConfig();
}
