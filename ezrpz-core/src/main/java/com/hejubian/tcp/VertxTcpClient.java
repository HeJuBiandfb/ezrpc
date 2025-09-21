package com.hejubian.tcp;

import io.vertx.core.Vertx;

public class VertxTcpClient {
    public void start(){
        // 创建Vert.x实例
        Vertx vertx = Vertx.vertx();
        vertx.createNetClient().connect(8888, "localhost", res -> {
            if (res.succeeded()) {
                System.out.println("成功连接到服务器！");
                // 连接成功后的处理逻辑
                io.vertx.core.net.NetSocket socket = res.result();
                // 发送数据到服务器
                socket.write("Hello, Server!");
                // 处理服务器响应
                socket.handler(buffer -> {
                    System.out.println("收到服务器响应: " + buffer.toString());
                });
            } else {
                System.out.println("连接 TCP 服务器失败" + res.cause().getMessage());
            }
        });
    }

    public static void main(String[] args) {
        new VertxTcpClient().start();
    }
}
