package com.stoicfree.free.mvc.module.security.context;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zengzhifei
 * @date 2023/2/17 21:18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserColumn<E> {
    private SFunction<E, ?> username;
    private SFunction<E, ?> password;
    private SFunction<E, ?> enable;
    private SFunction<E, ?> uuid;
    private SFunction<E, ?> roles;
}
