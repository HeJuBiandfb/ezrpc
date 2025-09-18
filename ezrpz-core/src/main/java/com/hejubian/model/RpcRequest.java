package com.hejubian.model;

import com.hejubian.constant.RpcConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcRequest implements Serializable {
    /**
     * 服务名
     */
    private String serviceName;
    /**
     * 方法名
     */
    private String methodName;
    /**
     *  版本号
     */
    private  String serviceVersion= RpcConstant.DEFAULT_SERVICE_VERSION;
    /**
     * 参数类型list
     */
    private Class<?>[] paramTypes;
    /**
     * 参数list
     */
    private Object[] args;
}
