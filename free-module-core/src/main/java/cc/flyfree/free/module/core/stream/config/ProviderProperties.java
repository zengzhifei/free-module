package cc.flyfree.free.module.core.stream.config;

import lombok.Data;

/**
 * @author zengzhifei
 * @date 2023/2/26 10:42
 */
@Data
public class ProviderProperties {
    /**
     * server地址
     */
    private String metaHost;
    /**
     * pipe名称
     */
    private String pipe;
    /**
     * pipe密码
     */
    private String password;
}
