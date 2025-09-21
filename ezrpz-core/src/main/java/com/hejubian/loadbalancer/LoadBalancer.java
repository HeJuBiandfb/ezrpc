package com.hejubian.loadbalancer;

import com.hejubian.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 负载均衡接口（消费端）
 */
public interface LoadBalancer {

    /**
     * 选择服务节点
     * @param requestParams
     * @param serviceMetaInfoList
     * @return
     */
    ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList);
}
