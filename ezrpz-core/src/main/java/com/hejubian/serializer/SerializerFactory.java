package com.hejubian.serializer;

import com.hejubian.spi.SpiLoader;

import java.util.HashMap;
import java.util.Map;

public class SerializerFactory {
//    private static final Map<String, Serializer> KEY_SERIALIZER_MAP = new HashMap<String, Serializer>(){
//        {
//            put(SerializerKeys.JDK, new JdkSerializer());
//            put(SerializerKeys.JSON, new JsonSerializer());
//            put(SerializerKeys.KRYO, new KryoSerializer());
//            put(SerializerKeys.HESSIAN, new HessianSerializer());
//        }
//    };

    static {
        SpiLoader.load(Serializer.class);
    }

    /**
     *  默认序列化器
     */
    private static final Serializer DEFAULT_SERIALIZER = new JdkSerializer();

    /**
     *  获取序列化器
     * @param key
     * @return
     */
    public static Serializer getSerializer(String key) {
        return SpiLoader.getinstance(Serializer.class, key);
    }
}
