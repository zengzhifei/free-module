package com.stoicfree.free.module.core.mvc.captcha.service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.stoicfree.free.module.core.common.enums.ErrorCode;
import com.stoicfree.free.module.core.common.support.Assert;
import com.stoicfree.free.module.core.common.util.EnumUtils;
import com.stoicfree.free.module.core.mvc.captcha.enums.CaptchaStyle;
import com.stoicfree.free.module.core.mvc.config.CaptchaProperties;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ICaptcha;

/**
 * @author zengzhifei
 * @date 2023/3/19 12:21
 */
public class Captcha {
    private final static String CAPTCHA_SESSION_KEY = "captcha";
    private final CaptchaProperties properties;

    public Captcha(CaptchaProperties properties) {
        this.properties = properties;
    }

    public void createCode(HttpServletRequest request, HttpServletResponse response, boolean isCloseOut) {
        // 获取验证码实例
        ICaptcha captcha = getCaptcha();

        // 生成验证码
        captcha.createCode();

        // 写入验证码
        request.getSession().setAttribute(CAPTCHA_SESSION_KEY, captcha);

        // 返回验证码
        OutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            captcha.write(outputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (isCloseOut && outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    public void verify(String captchaCode, HttpServletRequest request) {
        Object value = request.getSession().getAttribute(CAPTCHA_SESSION_KEY);
        Assert.notNull(value, ErrorCode.IO_EXCEPTION, "验证码校验异常");
        ICaptcha captcha = (ICaptcha) value;
        Assert.isTrue(captcha.verify(captchaCode), ErrorCode.INVALID_PARAMS, "验证码错误");
    }

    private ICaptcha getCaptcha() {
        ICaptcha captcha;
        String style = Optional.ofNullable(properties.getStyle()).map(String::toLowerCase).orElse("");
        CaptchaStyle captchaStyle = EnumUtils.of(CaptchaStyle.class, CaptchaStyle::getStyle, style);
        switch (Optional.ofNullable(captchaStyle).orElse(CaptchaStyle.LINE)) {
            default:
            case LINE:
                captcha = CaptchaUtil.createLineCaptcha(properties.getWidth(), properties.getHeight(),
                        properties.getCodeCount(), properties.getLineCount());
                break;
            case CIRCLE:
                captcha = CaptchaUtil.createCircleCaptcha(properties.getWidth(), properties.getHeight(),
                        properties.getCodeCount(), properties.getCircleCount());
                break;
            case SHEAR:
                captcha = CaptchaUtil.createShearCaptcha(properties.getWidth(), properties.getHeight(),
                        properties.getCodeCount(), properties.getShearThickness());
                break;
            case GIF:
                captcha = CaptchaUtil.createGifCaptcha(properties.getWidth(), properties.getHeight(),
                        properties.getCodeCount());
                break;
        }

        return captcha;
    }
}
