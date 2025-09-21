package com.hejubian.tcp;

import com.hejubian.server.HttpServer;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetServer;

import java.net.Socket;

public class VertxTcpServer implements HttpServer {

    private byte[] handleRequest(byte[] requestData) {
        //在此编写处理请求数据的逻辑，根据requestData生成响应数据并返回
        // 这里简单返回一个示例响应，实际应用中应根据请求内容生成响应
        return "Hello, Client!".getBytes();
    }

    @Override
    public void start(int port) {
        // 创建Vert.x实例
        Vertx vertx = Vertx.vertx();
        // 创建TCP服务器
        NetServer netServer = vertx.createNetServer();

        netServer.connectHandler(new TcpServerHandler());
        // 监听指定端口
//        netServer.connectHandler(socket -> {
//            // 处理客户端连接
//            socket.handler(buffer -> {
//                // 读取请求数据
//                byte[] requestData = buffer.getBytes();
//                // 处理请求并生成响应数据
//                byte[] responseData = handleRequest(requestData);
//                // 发送响应数据给客户端
//                socket.write(io.vertx.core.buffer.Buffer.buffer(responseData));
//            });
//        });

        netServer.listen(port, res -> {
            if (res.succeeded()) {
                System.out.println("TCP 服务器已启动，监听端口: " + port);
            } else {
                System.out.println("启动 TCP 服务器失败: " + res.cause().getMessage());
            }
        });
    }

    public static void main(String[] args) {
        new VertxTcpServer().start(8888);
    }
}
