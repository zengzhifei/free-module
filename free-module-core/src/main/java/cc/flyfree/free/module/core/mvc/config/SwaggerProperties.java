package cc.flyfree.free.module.core.mvc.config;

import lombok.Data;

/**
 * @author zengzhifei
 * @date 2023/2/11 14:46
 */
@Data
public class SwaggerProperties {
    private boolean enable = false;
    private String title;
    private String description;
    private String basePackage;
    private String version;
}
