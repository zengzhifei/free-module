package com.stoicfree.free.module.core.stream.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zengzhifei
 * @date 2023/3/26 22:07
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DelayMember {
    private String id;
    private String message;
}
