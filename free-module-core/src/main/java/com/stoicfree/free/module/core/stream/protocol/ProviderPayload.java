package com.stoicfree.free.module.core.stream.protocol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zengzhifei
 * @date 2023/3/30 19:27
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProviderPayload {
    private String pipe;
    private String password;
}
