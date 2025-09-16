package com.hejubian.server;

import io.vertx.core.Vertx;

public class VertxHttpServer implements HttpServer {
    @Override
    public void start(int port) {
        // 创建Vert.x实例
        Vertx vertx = Vertx.vertx();
        // 创建HTTP服务器
        io.vertx.core.http.HttpServer server = vertx.createHttpServer();
        // 设置请求处理器
        server.requestHandler(new HttpServerHandler());
        // 监听端口并处理请求
        server.listen(port,result ->{
            if (result.succeeded()) {
                System.out.println("HTTP server started on port " + port);
            } else {
                System.out.println("Failed to start HTTP server: " + result.cause());
            }
        });
    }
}
