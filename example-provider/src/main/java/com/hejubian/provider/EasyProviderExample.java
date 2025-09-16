package com.hejubian.provider;

import com.hejubian.RpcApplication;
import com.hejubian.common.service.UserService;
import com.hejubian.registry.LocalRegistry;
import com.hejubian.server.HttpServer;
import com.hejubian.server.VertxHttpServer;
import io.vertx.core.http.impl.HttpServerImpl;

public class EasyProviderExample {
    public static void main(String[] args) {
        RpcApplication.init();

        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        HttpServer httpServer = new VertxHttpServer();
        httpServer.start(RpcApplication.getRpcConfig().getServerPort());
    }
}
