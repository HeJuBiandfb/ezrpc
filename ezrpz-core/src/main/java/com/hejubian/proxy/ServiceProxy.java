package com.hejubian.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.hejubian.RpcApplication;
import com.hejubian.config.RpcConfig;
import com.hejubian.constant.RpcConstant;
import com.hejubian.loadbalancer.LoadBalancer;
import com.hejubian.loadbalancer.LoadBalancerFactory;
import com.hejubian.model.RpcRequest;
import com.hejubian.model.RpcResponse;
import com.hejubian.model.ServiceMetaInfo;
import com.hejubian.protocol.*;
import com.hejubian.registry.Registry;
import com.hejubian.registry.RegistryFactory;
import com.hejubian.serializer.JdkSerializer;
import com.hejubian.serializer.Serializer;
import com.hejubian.serializer.SerializerFactory;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;


import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
        String serviceName = method.getDeclaringClass().getName();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .paramTypes(method.getParameterTypes())
                .args(args)
                .build();
        try {
            // 序列化
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            //从注册中心获取服务地址
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(rpcConfig.getVersion());
            List<ServiceMetaInfo> serviceMetaInfos = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            if (CollUtil.isEmpty(serviceMetaInfos)){
                throw new RuntimeException("没有服务");
            }

            ServiceMetaInfo serviceMetaInfo1 = serviceMetaInfos.get(0);

            //发送TCP请求
            //创建Vert.x实例
            Vertx vertx = Vertx.vertx();
            //创建TCP客户端
            NetClient netClient = vertx.createNetClient();
            //用于接收响应结果
            CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();
            //连接到TCP服务器
            netClient.connect(serviceMetaInfo1.getServicePort(), serviceMetaInfo1.getServiceHost(),
                    result -> {
                        if (result.succeeded()) {

                            System.out.println("成功连接到TCP服务器");
                            NetSocket socket = result.result();

                            ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
                            ProtocolMessage.Header header = new ProtocolMessage.Header();
                            header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
                            header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
                            header.setSerializer((byte) ProtocolMessageSerializerEnum.getEnumByValue(RpcApplication.getRpcConfig().getSerializer()).getKey());
                            header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
                            header.setRequestId(IdUtil.getSnowflakeNextId());
                            protocolMessage.setHeader(header);
                            protocolMessage.setBody(rpcRequest);
                            //编码请求
                            try {
                                Buffer encodeBuffer = ProtocolMessageEncoder.encode(protocolMessage);
                                socket.write(encodeBuffer);
                            } catch (IOException e) {
                                throw new RuntimeException("协议消息编码失败", e);
                            }

                            socket.handler(buffer -> {
                                try {
                                    ProtocolMessage<RpcResponse> rpcResponseProtocolMessage = (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
                                    responseFuture.complete(rpcResponseProtocolMessage.getBody());
                                } catch (IOException e) {
                                    throw new RuntimeException("解码消息失败", e);
                                }
                            });
                        }else {
                            System.out.println("连接到TCP服务器失败: " + result.cause().getMessage());
                        }
                    });

            RpcResponse rpcResponse = responseFuture.get();
            netClient.close();
            return rpcResponse.getData();

//            // 负载均衡
//            LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());
//            //将请调用方法名（请求路径）作为负载均衡请求参数，后续可以添加更多请求参数
//            Map<String, Object> requestParams = new HashMap<>();
//            requestParams.put("methodName", rpcRequest.getMethodName());
//            ServiceMetaInfo select = loadBalancer.select(requestParams, serviceMetaInfos);
//
//            System.out.println(serviceMetaInfo1.getServiceAddress());
//
//            //todo 使用自定义协议发送请求
//            try (HttpResponse httpResponse = HttpRequest.post(serviceMetaInfo1.getServiceAddress())
//                    .body(bodyBytes)
//                    .execute()) {
//                byte[] result = httpResponse.bodyBytes();
//                // 反序列化
//                RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
//                return rpcResponse.getData();
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
