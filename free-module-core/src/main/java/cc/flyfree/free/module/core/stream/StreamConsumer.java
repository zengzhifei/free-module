package cc.flyfree.free.module.core.stream;

import javax.annotation.PostConstruct;

import cc.flyfree.free.module.core.common.enums.ErrorCode;
import cc.flyfree.free.module.core.common.support.Assert;
import cc.flyfree.free.module.core.stream.client.StreamClient;
import cc.flyfree.free.module.core.stream.config.ConsumerProperties;

import cn.hutool.core.convert.Convert;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zengzhifei
 * @date 2023/3/19 18:15
 */
@Slf4j
public class StreamConsumer {
    private final ConsumerProperties properties;
    private final IConsumer consumerHandler;
    private StreamClient client;
    private StreamClient.Consumer consumer;

    public StreamConsumer(ConsumerProperties properties, IConsumer consumer) {
        this.properties = properties;
        this.consumerHandler = consumer;
    }

    @PostConstruct
    private void init() {
        // 参数校验
        Assert.allFieldsValid(properties, ErrorCode.INVALID_PARAMS, "%s must be valid", "properties not be null");

        // metaHost校验
        String[] hostPort = properties.getMetaHost().split(":");
        Assert.hasLength(hostPort, 2, ErrorCode.INVALID_PARAMS, "meta host must be valid");

        // 创建客户端
        this.client = new StreamClient(hostPort[0], Convert.toInt(hostPort[1], 1009));
        this.client.setConsumerHandler(consumerHandler);

        // 启动客户端
        this.client.start();

        // 创建消费者
        this.consumer = auth();
        Assert.notNull(this.consumer, ErrorCode.INVALID_PARAMS, "consumer auth fail");

        // 启动消费者
        consume();
    }

    private void consume() {
        consumer.consume(properties.getCount());
    }

    private StreamClient.Consumer auth() {
        return client.consumerAuth(properties.getQueue(), properties.getToken());
    }
}
