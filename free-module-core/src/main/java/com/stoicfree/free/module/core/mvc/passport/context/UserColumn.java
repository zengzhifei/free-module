package com.stoicfree.free.module.core.mvc.passport.context;

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
    private SFunction<E, String> username;
    private SFunction<E, String> password;
    private SFunction<E, Boolean> enable;
    private SFunction<E, String> uuid;
    private SFunction<E, String> roles;
}
