package cc.flyfree.free.module.core.mvc.config;

import lombok.Data;
import lombok.ToString;

/**
 * @author zengzhifei
 * @date 2023/2/11 14:46
 */
@Data
@ToString(callSuper = true)
public class LoggingProperties extends BaseInterceptorProperties {
    private boolean toPrettyString;
}
