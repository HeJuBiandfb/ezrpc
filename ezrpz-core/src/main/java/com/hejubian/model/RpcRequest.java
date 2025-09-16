package com.hejubian.model;

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
     * 参数类型list
     */
    private Class<?>[] paramTypes;
    /**
     * 参数list
     */
    private Object[] args;
}
