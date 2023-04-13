package cc.flyfree.free.module.core.stream.protocol;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * @author zengzhifei
 * @date 2023/3/30 19:27
 */
public class Payload {
    @Data
    @SuperBuilder
    public static class Provider {
        private String pipe;

        @Data
        @SuperBuilder
        @ToString(callSuper = true)
        @EqualsAndHashCode(callSuper = true)
        public static class Auth extends Provider {
            private String password;
        }

        @Data
        @SuperBuilder
        @ToString(callSuper = true)
        @EqualsAndHashCode(callSuper = true)
        public static class Publish extends Provider {
            private String message;
        }

        @Data
        @SuperBuilder
        @ToString(callSuper = true)
        @EqualsAndHashCode(callSuper = true)
        public static class DelayPublish extends Provider {
            private String message;
            private Date date;
        }

        @Data
        @SuperBuilder
        @ToString(callSuper = true)
        @EqualsAndHashCode(callSuper = true)
        public static class DelayQueue extends Provider {
            private String id;
            private String message;
        }
    }

    @Data
    @SuperBuilder
    public static class Consumer {
        String queue;

        @Data
        @SuperBuilder
        @ToString(callSuper = true)
        @EqualsAndHashCode(callSuper = true)
        public static class Auth extends Consumer {
            private String token;
        }

        @Data
        @SuperBuilder
        @ToString(callSuper = true)
        @EqualsAndHashCode(callSuper = true)
        public static class Consume extends Consumer {
            private String pipe;
            private int count;
        }
    }
}
