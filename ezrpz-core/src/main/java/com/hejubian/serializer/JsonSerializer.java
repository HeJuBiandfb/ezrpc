package com.hejubian.serializer;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.hejubian.model.RpcRequest;
import com.hejubian.model.RpcResponse;

import java.io.IOException;

public class JsonSerializer implements  Serializer {
    public  static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public <T> byte[] serialize(T object) throws IOException {
        return OBJECT_MAPPER.writeValueAsBytes(object);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) throws IOException {
        T obj = OBJECT_MAPPER.readValue(bytes, type);
        if (obj instanceof RpcRequest){
            return  handleRequest((RpcRequest) obj, type);
        }
        if (obj  instanceof RpcResponse){
            return  handleResponse((RpcResponse) obj, type);
        }
        return  obj;

    }

    private <T> T handleRequest(RpcRequest request, Class<T> type) throws IOException {
        Class<?>[] paramTypes = request.getParamTypes();
        Object[] args = request.getArgs();

        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> paramType = paramTypes[i];
            if (!paramType.isAssignableFrom(args[i].getClass())){
                byte[] bytes = OBJECT_MAPPER.writeValueAsBytes(args[i]);
                args[i] = OBJECT_MAPPER.readValue(bytes, paramType);
            }
        }
        return type.cast(request);
    }

    private  <T> T handleResponse(RpcResponse response, Class<T> type) throws IOException {
        byte[] bytes = OBJECT_MAPPER.writeValueAsBytes(response.getData());
        response.setData(OBJECT_MAPPER.readValue(bytes, response.getDataType()));
        return  type.cast(response);
    }
}
