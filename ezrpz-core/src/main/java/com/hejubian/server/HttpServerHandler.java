package com.hejubian.server;

import com.hejubian.RpcApplication;
import com.hejubian.model.RpcRequest;
import com.hejubian.model.RpcResponse;
import com.hejubian.registry.LocalRegistry;
import com.hejubian.serializer.JdkSerializer;
import com.hejubian.serializer.Serializer;
import com.hejubian.serializer.SerializerFactory;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.net.http.HttpRequest;

@Slf4j
public class HttpServerHandler implements Handler<HttpServerRequest> {


    @Override
    public void handle(HttpServerRequest httpServerRequest) {
        //指定使用的序列化器
        final Serializer serializer = SerializerFactory.getSerializer(RpcApplication.getRpcConfig().getSerializer());

        // 记录日志
        log.info("接收到请求: {}", httpServerRequest.uri());


        httpServerRequest.bodyHandler(body -> {
            byte[] bytes = body.getBytes();
            RpcRequest request = null;
            try {
                request = serializer.deserialize(bytes, RpcRequest.class);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //构造响应结果对象
            RpcResponse response = new RpcResponse();

            //如果请求参数为空，则返回失败
            if (request ==null){
                response.setMessage("请求的RpcRequest为空");
                doResponse(httpServerRequest, response, serializer);
                return;
            }


            try {
                //
                Class<?> implClass = LocalRegistry.get(request.getServiceName());
                Method method = implClass.getMethod(request.getMethodName(), request.getParamTypes());
                Object result = method.invoke(implClass.newInstance(), request.getArgs());

                response.setData(result);
                response.setMessage("success");
                response.setDataType(method.getReturnType());
            } catch (Exception e) {
                e.printStackTrace();
                response.setMessage(e.getMessage());
                response.setException(e);
            }
            doResponse(httpServerRequest, response, serializer);
        });
    }

    /**
     *  响应
     * @param httpServerRequest
     * @param rpcResponse
     * @param serializer
     */
    void doResponse(HttpServerRequest httpServerRequest, RpcResponse rpcResponse, Serializer serializer) {
        HttpServerResponse  httpServerResponse = httpServerRequest.response()
                .putHeader("content-type", "application/json");
        try {
            //序列化
            byte[] serialized = serializer.serialize(rpcResponse);
            httpServerResponse.end(Buffer.buffer(serialized));
        } catch (Exception e) {
            e.printStackTrace();
            httpServerResponse.end(Buffer.buffer());
        }
    }
}
