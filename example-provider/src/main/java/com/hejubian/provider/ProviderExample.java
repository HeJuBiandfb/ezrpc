package com.hejubian.provider;

import com.hejubian.RpcApplication;
import com.hejubian.common.service.UserService;
import com.hejubian.config.RegistryConfig;
import com.hejubian.config.RpcConfig;
import com.hejubian.model.ServiceMetaInfo;
import com.hejubian.registry.LocalRegistry;
import com.hejubian.registry.Registry;
import com.hejubian.registry.RegistryFactory;
import com.hejubian.server.VertxHttpServer;

public class ProviderExample {
    public static void main(String[] args) {
        //rpc框架初始化
        RpcApplication.init();

        //服务注册
        String name = UserService.class.getName();
        LocalRegistry.register(name, UserServiceImpl.class);

        //注册服务到注册中心
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(name);
        serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
        serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
        try {
            registry.register(serviceMetaInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //启动web服务
        VertxHttpServer vertxHttpServer = new VertxHttpServer();
        vertxHttpServer.start(RpcApplication.getRpcConfig().getServerPort());
    }

}
