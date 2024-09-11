package cc.flyfree.free.module.core.stream;

import java.util.Date;

import cc.flyfree.free.module.core.common.enums.ErrorCode;
import cc.flyfree.free.module.core.common.support.Assert;
import cc.flyfree.free.module.core.stream.client.StreamClient;
import cc.flyfree.free.module.core.stream.config.ProviderProperties;
import cn.hutool.core.convert.Convert;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zengzhifei
 * @date 2023/3/19 18:15
 */
@Slf4j
public class StreamProvider {
    private final ProviderProperties properties;
    private StreamClient client;
    private StreamClient.Provider provider;

    public StreamProvider(ProviderProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    private void init() {
        // 参数校验
        Assert.allFieldsValid(properties, ErrorCode.INVALID_PARAMS, "%s must be valid", "properties not be null");

        String[] hostPort = properties.getMetaHost().split(":");
        Assert.hasLength(hostPort, 2, ErrorCode.INVALID_PARAMS, "meta host must be valid");

        // 创建客户端
        this.client = new StreamClient(hostPort[0], Convert.toInt(hostPort[1], 1009));

        // 创建发布器
        this.provider = auth();
        Assert.notNull(this.provider, ErrorCode.INVALID_PARAMS, "provider auth fail");
    }

    public String publish(String message) {
        String id = null;
        try {
            id = provider.publish(message);
            return id;
        } finally {
            log.info("stream provider publish message[{}], messageId[{}]", message, id);
        }
    }

    public void asyncPublish(String message) {
        try {
            provider.asyncPublish(message);
        } finally {
            log.info("stream provider async publish message[{}]", message);
        }
    }

    public String delayPublish(String message, Date date) {
        String id = null;
        try {
            id = provider.delayPublish(message, date);
            return id;
        } finally {
            log.info("stream provider delay publish message[{}], date[{}], messageId[{}]", message, date, id);
        }
    }

    public void asyncDelayPublish(String message, Date date) {
        try {
            provider.asyncDelayPublish(message, date);
        } finally {
            log.info("stream provider async delay publish message[{}], date[{}]", message, date);
        }
    }

    private StreamClient.Provider auth() {
        return client.providerAuth(properties.getPipe(), properties.getPassword());
    }
}
