package cc.flyfree.free.module.autoconfigure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cc.flyfree.free.module.core.rpc.agent.ProviderAgent;
import cc.flyfree.free.module.core.rpc.config.RpcProperties;

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
    public ProviderAgent providerAgent() {
        return new ProviderAgent(rpcProperties.getProvider());
    }
}
