package cc.flyfree.free.module.core.stream.client;

import java.nio.ByteBuffer;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import cc.flyfree.free.module.core.common.misc.socket.nio.ChannelIo;
import cc.flyfree.free.module.core.common.misc.socket.nio.NioClient;
import cc.flyfree.free.module.core.common.misc.socket.nio.protocol.Packet;
import cc.flyfree.free.module.core.common.misc.socket.nio.protocol.Protocol;
import cc.flyfree.free.module.core.stream.IConsumer;
import cc.flyfree.free.module.core.stream.domain.Message;
import cc.flyfree.free.module.core.stream.exception.StreamClientException;
import cc.flyfree.free.module.core.stream.protocol.Command;
import cc.flyfree.free.module.core.stream.protocol.Payload;

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
                List<Message> messages = packet.getPayload(List.class, Message.class);
                consumer.batchConsume(messages);
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
        Protocol.Proto input = Protocol.encode(Command.PROVIDER_AUTH, payload);
        ByteBuffer output = blockingClient.blockingWrite(input);
        if (Protocol.decode(output).getPayload(Boolean.class)) {
            nonblockingClient.nonblockingWrite(input);
        } else {
            throw new StreamClientException("provider auth fail");
        }
        return new Provider(pipe, blockingClient, nonblockingClient);
    }

    public Consumer consumerAuth(String queue, String token) {
        Payload.Consumer.Auth payload = Payload.Consumer.Auth.builder().queue(queue).token(token).build();
        Protocol.Proto input = Protocol.encode(Command.CONSUMER_AUTH, payload);
        ByteBuffer output = blockingClient.blockingWrite(input);
        String pipe = Protocol.decode(output).getPayload(String.class);
        if (StringUtils.isNotBlank(pipe)) {
            nonblockingClient.nonblockingWrite(input);
        } else {
            throw new StreamClientException("consumer auth fail");
        }
        return new Consumer(pipe, queue, nonblockingClient);
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
        private final String pipe;
        private final String queue;
        private final NioClient nonblockingClient;

        public Consumer(String pipe, String queue, NioClient nonblockingClient) {
            this.pipe = pipe;
            this.queue = queue;
            this.nonblockingClient = nonblockingClient;
        }

        public void consume(int count) {
            Payload.Consumer.Consume payload = Payload.Consumer.Consume.builder()
                    .pipe(pipe).queue(queue).count(count).build();
            nonblockingClient.nonblockingWrite(Protocol.encode(Command.CONSUME, payload));
        }
    }
}
