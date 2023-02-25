package com.stoicfree.free.module.core.mvc.security.context;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zengzhifei
 * @date 2023/2/17 17:27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenContext {
    private String uuid;
    private String password;
    private String random;
    private Long expires;
}
