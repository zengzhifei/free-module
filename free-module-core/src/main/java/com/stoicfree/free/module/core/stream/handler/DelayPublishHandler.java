package com.stoicfree.free.module.core.stream.handler;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import com.stoicfree.free.module.core.common.gson.GsonUtil;
import com.stoicfree.free.module.core.common.misc.socket.nio.ChannelIo;
import com.stoicfree.free.module.core.common.misc.socket.nio.protocol.Packet;
import com.stoicfree.free.module.core.common.support.ID;
import com.stoicfree.free.module.core.common.util.DateUtils;
import com.stoicfree.free.module.core.redis.client.RedisClient;
import com.stoicfree.free.module.core.stream.Streamer;
import com.stoicfree.free.module.core.stream.exception.StreamException;
import com.stoicfree.free.module.core.stream.protocol.Command;
import com.stoicfree.free.module.core.stream.protocol.Payload;

/**
 * @author zengzhifei
 * @date 2023/3/31 23:53
 */
public class DelayPublishHandler extends BaseHandler {
    @Override
    public boolean match(Command command) {
        return Command.DELAY_PUBLISH.equals(command);
    }

    @Override
    public void handle(RedisClient client, SelectionKey selectionKey, SocketChannel channel, Packet<Command> packet) {
        try {
            Payload.Provider.DelayPublish delayPublish = packet.getPayload(Payload.Provider.DelayPublish.class);

            String id = ID.SNOWFLAKE.nextIdStr();
            Payload.Provider.DelayQueue delayQueue = Payload.Provider.DelayQueue.builder()
                    .pipe(delayPublish.getPipe()).message(delayPublish.getMessage()).id(id)
                    .build();
            long time = DateUtils.getSecondTime(delayPublish.getDate());
            boolean ret = client.zadd(Streamer.DELAY_KEY, time, GsonUtil.toJson(delayQueue)) > 0;
            if (!ret) {
                throw new RuntimeException("delay publish fail");
            }

            ChannelIo.writeIn(channel, packet.newPayload(id));
        } catch (Exception e) {
            throw new StreamException(e.getMessage());
        }
    }
}
