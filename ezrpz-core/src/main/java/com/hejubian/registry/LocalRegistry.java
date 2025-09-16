package com.hejubian.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LocalRegistry {
    /**
     * 注册信息存储
     */
    private static final Map<String, Class<?>> map = new ConcurrentHashMap<>();

    /**
     * 注册服务
     * @param serviceName
     * @param serviceClass
     */
    public static void register(String serviceName, Class<?> serviceClass) {
        map.put(serviceName, serviceClass);
    }

    /**
     * 获取服务
     * @param serviceName
     * @return
     */
    public static Class<?> get(String serviceName) {
        return map.get(serviceName);
    }

    /**
     * 移除服务
     * @param serviceName
     */
    public static void remove(String serviceName) {
        map.remove(serviceName);
    }
}
