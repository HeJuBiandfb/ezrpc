package com.hejubian.loadbalancer;

import com.hejubian.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ConsistentHashLoadBalancer implements LoadBalancer{

    /**
     * 一致性哈希环，key：虚拟节点的哈希值，value：服务节点信息
     */
    private final TreeMap<Integer, ServiceMetaInfo> virtualNodes = new TreeMap<>();

    /**
     * 虚拟节点数
     */
    public static final int VIRTUAL_NODE_NUM = 100;

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if (serviceMetaInfoList.isEmpty()) {
            return null;
        }
        //构建虚拟节点环
        for (ServiceMetaInfo serviceMetaInfo : serviceMetaInfoList) {
            for (int i = 0; i < VIRTUAL_NODE_NUM; i++) {
                int hash = getHash(serviceMetaInfo.getServiceAddress() + "#" + i);
                virtualNodes.put(hash, serviceMetaInfo);
            }
        }

        //计算请求参数的哈希值
        int hash = getHash(requestParams);

        //顺时针找到大于等于该哈希值的第一个虚拟节点
        Map.Entry<Integer, ServiceMetaInfo> integerServiceMetaInfoEntry = virtualNodes.ceilingEntry(hash);
        if (integerServiceMetaInfoEntry == null) {
            //如果没有大于等于该哈希值的虚拟节点，则选择环上的第一个节点
            integerServiceMetaInfoEntry = virtualNodes.firstEntry();
        }
        return integerServiceMetaInfoEntry.getValue();
    }

    /**
     * 获取对象的哈希值
     * @param key
     * @return
     */
    private int getHash(Object key) {
        return key.hashCode();
    }
}
