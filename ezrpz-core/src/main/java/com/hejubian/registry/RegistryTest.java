package com.hejubian.registry;

import com.hejubian.config.RegistryConfig;
import com.hejubian.model.ServiceMetaInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class RegistryTest {
    final Registry registry = new EtcdRegistry();

    @Before
    public void init() {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress("http://127.0.0.1:2379");
        registry.init(registryConfig);
    }

    @Test
    public void  register() throws Exception {
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("testService");
        serviceMetaInfo.setServiceVersion("1.0.0");
        serviceMetaInfo.setServiceHost("http://127.0.0.1:2379");
        serviceMetaInfo.setServicePort(4739);
        registry.register(serviceMetaInfo);

        serviceMetaInfo =  new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("testService");
        serviceMetaInfo.setServiceVersion("1.0.0");
        serviceMetaInfo.setServiceHost("http://127.0.0.1:2379");
        serviceMetaInfo.setServicePort(4740);
        registry.register(serviceMetaInfo);

        serviceMetaInfo =  new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("testService");
        serviceMetaInfo.setServiceVersion("2.0.0");
        serviceMetaInfo.setServiceHost("http://127.0.0.1:2379");
        serviceMetaInfo.setServicePort(4739);
        registry.register(serviceMetaInfo);
    }

    /**
     *
     */
    @Test
    public void unRegister() {
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("testService");
        serviceMetaInfo.setServiceVersion("1.0.0");
        serviceMetaInfo.setServiceHost("127.0.0.1");
        serviceMetaInfo.setServicePort(4739);
        registry.unRegister(serviceMetaInfo);
    }

    @Test
    public void serviceDiscovery() {
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("testService");
        serviceMetaInfo.setServiceVersion("1.0.0");
        String serviceKey = serviceMetaInfo.getServiceKey();
        List<ServiceMetaInfo> serviceMetaInfos = registry.serviceDiscovery(serviceKey);
        Assert.assertNotNull(serviceMetaInfos);
    }
}
