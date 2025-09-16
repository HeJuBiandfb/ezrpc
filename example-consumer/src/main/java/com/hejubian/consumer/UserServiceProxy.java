package com.hejubian.consumer;


import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.hejubian.common.model.User;
import com.hejubian.common.service.UserService;
import com.hejubian.model.RpcRequest;
import com.hejubian.model.RpcResponse;
import com.hejubian.serializer.JdkSerializer;
import com.hejubian.serializer.Serializer;

import java.io.IOException;


public class UserServiceProxy implements UserService {
    @Override
    public User gerUser(User user) {
        // 指定序列化器
        Serializer serializer = new JdkSerializer();
        //
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(UserService.class.getName())
                .methodName("gerUser")
                .paramTypes(new Class[]{User.class})
                .args(new Object[]{user})
                .build();
        try {
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            byte[] result;
            try (HttpResponse httpResponse = HttpRequest.post("http://localhost:8080").body(bodyBytes).execute()){
                result = httpResponse.bodyBytes();
            }
            RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
            return  (User) rpcResponse.getData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  null;
    }
}
