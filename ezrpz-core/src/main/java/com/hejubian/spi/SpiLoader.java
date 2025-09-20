package com.hejubian.spi;

import cn.hutool.core.io.resource.ResourceUtil;
import com.hejubian.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SpiLoader {

    /**
     * 储存已加载的类，Key：接口名，Value：实现类名
     */
    private static Map<String, Map<String, Class<?>>> loaderMap = new ConcurrentHashMap<>();
    /**
     * 实例对象缓存（避免重复new），Key：类路径，Value：实例对象，用于单例模式
     */
    private static Map<String, Object>  instanceCache = new ConcurrentHashMap<>();
    /**
     * 系统SPI路径
     */
    private static final String RPC_SYSTEM_SPI_DIR = "META-INF/rpc/system/";
    /**
     * 用户自定义SPI路径
     */
    private static final String RPC_CUSTOM_SPI_DIR = "META-INF/rpc/custom/";
    /**
     * 扫描路径
     */
    private static final String[] SCAN_DIRS = new String[]{RPC_SYSTEM_SPI_DIR, RPC_CUSTOM_SPI_DIR};

    private static final List<Class<?>> LOAD_CLASS_LIST = Arrays.asList(Serializer.class);

    public static void loadAll() {
        log.info("开始加载SPI接口");
        for (Class<?> aClass : LOAD_CLASS_LIST) {
            load(aClass);
        }
    }

    /**
     *  加载指定接口的实现类
     * @param clazz
     * @param key
     * @return
     * @param <T>
     */
    public static <T> T getInstance(Class<?> clazz, String key){
        String name = clazz.getName();
        Map<String, Class<?>> keyClassMap = loaderMap.get(name);
        if (keyClassMap == null){
            throw new RuntimeException(String.format("SpiLoader未加载 %s 的实现类", name));
        }
        if (!keyClassMap.containsKey(key)){
            throw new RuntimeException(String.format("SpiLoader 的 %s 没有 key = %s 的实现类", name, key));
        }
        Class<?> implClass = keyClassMap.get(key);

        String implClassName = implClass.getName();
        if (!instanceCache.containsKey(implClassName)){
            try {
                instanceCache.put(implClassName, implClass.newInstance());
            } catch (InstantiationException  | IllegalAccessException e) {
                String errorMsg = String.format("实例化 %s 失败", implClassName);
                throw new RuntimeException(errorMsg, e);
            }
        }
        return  (T) instanceCache.get(implClassName);
    }

    /**
     *  加载指定接口的实现类
     * @param clazz
     * @return
     */
    public static Map<String, Class<?>> load(Class<?> clazz) {
        log.info("开始加载SPI接口: {}", clazz.getName());
        HashMap<String, Class<?>> keyClassMap = new HashMap<>();
        for (String scanDir : SCAN_DIRS) {
            List<URL> resources = ResourceUtil.getResources(scanDir + clazz.getName());

            for (URL resource : resources) {
                try {
                    InputStreamReader inputStreamReader = new InputStreamReader(resource.openStream());
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] split = line.split("=");
                        if (split.length > 1) {
                            String key = split[0];
                            String className = split[1];
                            keyClassMap.put(key, Class.forName(className));
                        }
                    }
                } catch (Exception e) {
                    log.error("加载SPI接口失败", e);
                }

            }
        }
        loaderMap.put(clazz.getName(), keyClassMap);
        return  keyClassMap;

    }
}
