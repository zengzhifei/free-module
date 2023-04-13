package cc.flyfree.free.module.core.mvc.config;

import lombok.Data;

/**
 * @author zengzhifei
 * @date 2023/2/11 14:47
 */
@Data
public class CrossDomainProperties {
    private String host = "*";
    private String headers;
}
