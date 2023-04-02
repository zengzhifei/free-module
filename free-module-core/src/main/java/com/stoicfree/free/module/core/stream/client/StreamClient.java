package com.stoicfree.free.module.core.stream.client;

import java.nio.ByteBuffer;
import java.util.Date;

import com.stoicfree.free.module.core.common.misc.socket.nio.ChannelIo;
import com.stoicfree.free.module.core.common.misc.socket.nio.NioClient;
import com.stoicfree.free.module.core.common.misc.socket.nio.protocol.Packet;
import com.stoicfree.free.module.core.common.misc.socket.nio.protocol.Protocol;
import com.stoicfree.free.module.core.stream.IConsumer;
import com.stoicfree.free.module.core.stream.domain.Message;
import com.stoicfree.free.module.core.stream.exception.StreamException;
import com.stoicfree.free.module.core.stream.protocol.Command;
import com.stoicfree.free.module.core.stream.protocol.Payload;

import cn.hutool.core.convert.Convert;

/**
 * @author zengzhifei
 * @date 2023/3/31 18:25
 */
public class StreamClient {
    private final NioClient blockingClient;
    private final NioClient nonblockingClient;

    public StreamClient(String host, int port) {
        // 阻塞客户端
        this.blockingClient = new NioClient(host, port, true);
        // 非阻塞客户端
        this.nonblockingClient = new NioClient(host, port, false);
    }

    public StreamClient setConsumerHandler(IConsumer consumer) {
        this.nonblockingClient.registerChannelHandler((selectionKey, channel) -> {
            Packet<Command> packet = ChannelIo.readout(channel);
            if (Command.CONSUME.equals(packet.getCommand())) {
                Object payload = packet.getPayload();
                consumer.batchConsume(Convert.toList(Message.class, payload));
            }
        });
        return this;
    }

    public void start() {
        this.blockingClient.start();
        this.nonblockingClient.start();
    }

    public Provider providerAuth(String pipe, String password) {
        Payload.Provider.Auth payload = Payload.Provider.Auth.builder().pipe(pipe).password(password).build();
        ByteBuffer input = Protocol.encode(Command.PROVIDER_AUTH, payload);
        ByteBuffer output = blockingClient.blockingWrite(input);
        if (Protocol.decode(output).getPayload(Boolean.class)) {
            throw new StreamException("provider auth fail");
        } else {
            nonblockingClient.nonblockingWrite(input);
        }
        return new Provider(pipe, blockingClient, nonblockingClient);
    }

    public Consumer consumerAuth(String queue, String token) {
        Payload.Consumer.Auth payload = Payload.Consumer.Auth.builder().queue(queue).token(token).build();
        ByteBuffer input = Protocol.encode(Command.CONSUMER_AUTH, payload);
        ByteBuffer output = blockingClient.blockingWrite(input);
        if (Protocol.decode(output).getPayload(Boolean.class)) {
            throw new StreamException("consumer auth fail");
        } else {
            nonblockingClient.nonblockingWrite(input);
        }
        return new Consumer(queue, nonblockingClient);
    }

    public static class Provider {
        private final String pipe;
        private final NioClient blockingClient;
        private final NioClient nonblockingClient;

        private Provider(String pipe, NioClient blockingClient, NioClient nonblockingClient) {
            this.pipe = pipe;
            this.blockingClient = blockingClient;
            this.nonblockingClient = nonblockingClient;
        }

        public String publish(String message) {
            Payload.Provider.Publish payload = Payload.Provider.Publish.builder().pipe(pipe).message(message).build();
            ByteBuffer output = blockingClient.blockingWrite(Protocol.encode(Command.PUBLISH, payload));
            return Protocol.decode(output).getPayload(String.class);
        }

        public void asyncPublish(String message) {
            Payload.Provider.Publish payload = Payload.Provider.Publish.builder().pipe(pipe).message(message).build();
            nonblockingClient.nonblockingWrite(Protocol.encode(Command.PUBLISH, payload));
        }

        public String delayPublish(String message, Date date) {
            Payload.Provider.DelayPublish payload = Payload.Provider.DelayPublish.builder()
                    .pipe(pipe).message(message).date(date).build();
            ByteBuffer buffer = blockingClient.blockingWrite(Protocol.encode(Command.DELAY_PUBLISH, payload));
            return Protocol.decode(buffer).getPayload(String.class);
        }

        public void asyncDelayPublish(String message, Date date) {
            Payload.Provider.DelayPublish payload = Payload.Provider.DelayPublish.builder()
                    .pipe(pipe).message(message).date(date).build();
            nonblockingClient.nonblockingWrite(Protocol.encode(Command.DELAY_PUBLISH, payload));
        }
    }

    public static class Consumer {
        private final String queue;
        private final NioClient nonblockingClient;

        private Consumer(String queue, NioClient nonblockingClient) {
            this.queue = queue;
            this.nonblockingClient = nonblockingClient;
        }

        public void consume(int count) {
            Payload.Consumer.Consume payload = Payload.Consumer.Consume.builder().queue(queue).count(count).build();
            nonblockingClient.nonblockingWrite(Protocol.encode(Command.CONSUME, payload));
        }
    }
}
