package com.hejubian.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.hejubian.RpcApplication;
import com.hejubian.config.RpcConfig;
import com.hejubian.model.RpcRequest;
import com.hejubian.model.RpcResponse;
import com.hejubian.serializer.JdkSerializer;
import com.hejubian.serializer.Serializer;
import com.hejubian.serializer.SerializerFactory;


import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 服务代理（JDK 动态代理）
 */
public class ServiceProxy implements InvocationHandler {

    /**
     * 调用代理
     *
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {


        // 指定序列化器
        final Serializer serializer = SerializerFactory.getSerializer(RpcApplication.getRpcConfig().getSerializer());

        System.out.println("serializer: " + serializer);

        // 构造请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .paramTypes(method.getParameterTypes())
                .args(args)
                .build();
        try {
            // 序列化
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            // 发送请求

            String serverHost = RpcApplication.getRpcConfig().getServerHost();
            Integer serverPort = RpcApplication.getRpcConfig().getServerPort();
            System.out.println("http://"+ serverHost+ ":" + serverPort);

            System.out.println("http://"+ serverHost+ ":" + serverPort);

            try (HttpResponse httpResponse = HttpRequest.post("http://"+ serverHost+ ":" + serverPort)
                    .body(bodyBytes)
                    .execute()) {
                byte[] result = httpResponse.bodyBytes();
                // 反序列化
                RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
                return rpcResponse.getData();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
