package com.stoicfree.free.module.autoconfigure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.stoicfree.free.module.core.rpc.agent.ProviderAgent;
import com.stoicfree.free.module.core.rpc.agent.ProviderContainer;
import com.stoicfree.free.module.core.rpc.config.RpcProperties;

/**
 * @author zengzhifei
 * @date 2023/2/3 17:14
 */
@Configuration
@EnableConfigurationProperties(RpcProperties.class)
public class RpcModuleAutoConfiguration {
    @Autowired
    private RpcProperties rpcProperties;

    @Bean
    public ProviderContainer providerContainer() {
        return new ProviderContainer();
    }

    @Bean
    @ConditionalOnBean(ProviderContainer.class)
    public ProviderAgent providerAgent() {
        return new ProviderAgent(rpcProperties.getProvider());
    }
}
