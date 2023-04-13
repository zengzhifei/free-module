package cc.flyfree.free.module.core.stream.handler;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import cc.flyfree.free.module.core.common.gson.GsonUtil;
import cc.flyfree.free.module.core.common.misc.socket.nio.protocol.Packet;
import cc.flyfree.free.module.core.common.support.ID;
import cc.flyfree.free.module.core.common.util.DateUtils;
import cc.flyfree.free.module.core.redis.client.RedisClient;
import cc.flyfree.free.module.core.stream.Streamer;
import cc.flyfree.free.module.core.stream.protocol.Command;
import cc.flyfree.free.module.core.stream.protocol.Payload;

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
        execute(channel, packet, null, () -> {
            Payload.Provider.DelayPublish delayPublish = packet.getPayload(Payload.Provider.DelayPublish.class);

            String id = ID.SNOWFLAKE.nextIdStr();
            Payload.Provider.DelayQueue delayQueue = Payload.Provider.DelayQueue.builder()
                    .pipe(delayPublish.getPipe()).message(delayPublish.getMessage()).id(id)
                    .build();
            long time = DateUtils.getSecondTime(delayPublish.getDate());
            boolean ret = client.zadd(Streamer.getDelayKey(0), time, GsonUtil.toJson(delayQueue)) > 0;
            if (!ret) {
                throw new RuntimeException("delay publish fail");
            }

            return id;
        });
    }
}
