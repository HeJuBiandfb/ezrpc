package com.hejubian;

import com.hejubian.config.RegistryConfig;
import com.hejubian.config.RpcConfig;
import com.hejubian.constant.RpcConstant;
import com.hejubian.registry.Registry;
import com.hejubian.registry.RegistryFactory;
import com.hejubian.utils.ConfigUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcApplication {
    public static volatile RpcConfig rpcConfig;

    /**
     *  框架初始化，指出传入自定义配置
     * @param newrpcConfig
     */
    public static void init(RpcConfig newrpcConfig){
        rpcConfig = newrpcConfig;
        log.info("rpc init, config = {}", newrpcConfig.toString());
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        registry.init(registryConfig);
        log.info("registry init, registry = {}", registryConfig.getRegistry());
    }

    /**
     *  框架初始化，使用默认配置
     */
    public static void init(){
        RpcConfig newRpcConfig;
        try{
            newRpcConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        }catch (Exception e){
            newRpcConfig = new RpcConfig();
        }
        init(newRpcConfig);
    }

    /**
     *  获取配置信息
     * @return
     */
    public static RpcConfig getRpcConfig(){
        if (rpcConfig == null){
            synchronized (RpcApplication.class){
                if (rpcConfig == null){
                    init();
                }
            }
        }
        return  rpcConfig;
    }
}
