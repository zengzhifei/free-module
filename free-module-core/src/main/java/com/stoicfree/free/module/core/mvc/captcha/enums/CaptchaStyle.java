package com.stoicfree.free.module.core.mvc.captcha.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zengzhifei
 * @date 2023/3/19 12:37
 */
@Getter
@AllArgsConstructor
public enum CaptchaStyle {
    // 验证码样式
    LINE("line"), CIRCLE("circle"), SHEAR("shear"), GIF("gif");

    private String style;
}
