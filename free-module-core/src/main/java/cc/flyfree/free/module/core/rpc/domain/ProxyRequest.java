package cc.flyfree.free.module.core.rpc.domain;

import lombok.Data;

/**
 * @author zengzhifei
 * @date 2023/2/26 00:23
 */
@Data
public class ProxyRequest {
    private String providerId;
    private Object[] params;
}
