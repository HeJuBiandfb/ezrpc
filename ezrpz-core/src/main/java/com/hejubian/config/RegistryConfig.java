package com.hejubian.config;

import lombok.Data;

/**
 *  注册中心配置
 */
@Data
public class RegistryConfig {
    /**
     * 注册中心类别
     */
    private String registry = "etcd";
    /**
     *  注册中心地址
     */
    private String address = "http://127.0.0.1:2379";
    /**
     * 用户名
     */
    private  String username;
    /**
     * 密码
     */
    private  String password;
    /**
     * 超时时间(毫秒)
     */
    private Long timeout = 10000L;
}
