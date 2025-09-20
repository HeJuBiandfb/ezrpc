package com.hejubian.registry;

import com.hejubian.spi.SpiLoader;

public class RegistryFactory {
    static {
        SpiLoader.load(Registry.class);
    }

    /**
     * 获取默认注册中心实现
     */
    private static final Registry DEFAULT_REGISTRY = new EtcdRegistry();

    public static Registry getInstance(String key) {
        return SpiLoader.getInstance(Registry.class, key);
    }
}
