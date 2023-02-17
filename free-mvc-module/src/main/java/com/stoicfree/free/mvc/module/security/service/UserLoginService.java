package com.stoicfree.free.mvc.module.security.service;

import java.nio.charset.StandardCharsets;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.InitializingBean;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stoicfree.free.common.module.enums.ErrorCode;
import com.stoicfree.free.common.module.gson.GsonUtil;
import com.stoicfree.free.common.module.support.Assert;
import com.stoicfree.free.common.module.support.BizException;
import com.stoicfree.free.common.module.support.Safes;
import com.stoicfree.free.common.module.util.UrlUtils;
import com.stoicfree.free.mvc.module.config.SecurityProperties;
import com.stoicfree.free.mvc.module.security.context.TokenContext;
import com.stoicfree.free.mvc.module.security.context.UserColumn;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.BCrypt;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.extra.servlet.ServletUtil;

/**
 * @author zengzhifei
 * @date 2023/2/17 16:54
 */
public class UserLoginService<E> extends AbstractUserService<E> implements InitializingBean {
    private static final AES AES = SecureUtil.aes("(7djPSrws9K2MJk8".getBytes(StandardCharsets.UTF_8));

    private SecurityProperties securityProperties;

    private final BaseMapper<E> mapper;
    private final UserColumn<E> userColumn;
    private final String usernameFiledName;
    private final String passwordFiledName;
    private final String uuidFiledName;
    private final String enableFiledName;

    public UserLoginService(BaseMapper<E> mapper, UserColumn<E> userColumn) {
        this.mapper = mapper;
        this.userColumn = userColumn;

        this.usernameFiledName = getFieldName(userColumn.getUsername());
        this.passwordFiledName = getFieldName(userColumn.getPassword());
        this.uuidFiledName = getFieldName(userColumn.getUuid());
        this.enableFiledName = getFieldName(userColumn.getEnable());
    }

    public void login(E entity, HttpServletRequest request, HttpServletResponse response) {
        // 解析登录信息
        Object usernameValue = getFieldValue(entity, usernameFiledName);
        String passwordValue = (String) getFieldValue(entity, passwordFiledName);

        // 获取用户
        LambdaQueryWrapper<E> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(userColumn.getUsername(), usernameValue);
        E user = mapper.selectOne(queryWrapper);
        Assert.notNull(user, ErrorCode.USER_NOT_FOUND);

        // 校验密码
        String password = (String) getFieldValue(user, passwordFiledName);
        Assert.isTrue(BCrypt.checkpw(passwordValue, password), ErrorCode.PASSWORD_ERROR);

        // 校验状态
        boolean enable = (boolean) getFieldValue(user, enableFiledName);
        Assert.isTrue(enable, ErrorCode.USER_DISABLE);

        // 获取uuid
        String uuid = (String) getFieldValue(user, uuidFiledName);

        // 写入登录状态
        refreshToken(uuid, password, request, response);
    }

    public void verifyLogin(HttpServletRequest request, HttpServletResponse response) {
        // 获取token
        String token = Safes.of(ServletUtil.getCookie(request, securityProperties.getTokenKey()), Cookie::getValue);
        Assert.notBlank(token, ErrorCode.NOT_LOGIN);

        // 解密token
        String decryptToken;
        try {
            decryptToken = AES.decryptStr(token);
        } catch (Exception e) {
            throw new BizException(ErrorCode.TOKEN_ERROR);
        }
        TokenContext tokenContext = GsonUtil.fromJson(decryptToken, TokenContext.class);

        // 校验token有效期
        Assert.isTrue(tokenContext.getExpires() > DateUtil.currentSeconds(), ErrorCode.LOGIN_EXPIRED);

        // 校验用户
        String uuid = tokenContext.getUuid();
        LambdaQueryWrapper<E> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(userColumn.getUuid(), uuid);
        E user = mapper.selectOne(queryWrapper);
        Assert.notNull(user, ErrorCode.USER_NOT_FOUND);

        // 校验状态
        boolean enable = (boolean) getFieldValue(user, enableFiledName);
        Assert.isTrue(enable, ErrorCode.USER_DISABLE);

        // 校验密码
        String password = (String) getFieldValue(user, passwordFiledName);
        Assert.equals(password, tokenContext.getPassword(), ErrorCode.PASSWORD_ERROR);

        // 刷新token
        if ((tokenContext.getExpires() - DateUtil.currentSeconds()) < (tokenContext.getExpires() / 2)) {
            refreshToken(uuid, password, request, response);
        }
    }

    public void setSecurityProperties(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(userColumn, ErrorCode.INVALID_PARAMS, "userColumn not be null");
        Assert.notNull(userColumn.getUsername(), ErrorCode.INVALID_PARAMS, "userColumn username not be null");
        Assert.notNull(userColumn.getPassword(), ErrorCode.INVALID_PARAMS, "userColumn password not be null");
        Assert.notNull(userColumn.getUuid(), ErrorCode.INVALID_PARAMS, "userColumn uuid not be null");
        Assert.notNull(userColumn.getEnable(), ErrorCode.INVALID_PARAMS, "userColumn enable not be null");
    }

    private void refreshToken(String uuid, String password, HttpServletRequest request, HttpServletResponse response) {
        TokenContext tokenContext = TokenContext.builder()
                .uuid(uuid).password(password)
                .random(RandomUtil.randomString(8))
                .expires(DateUtil.currentSeconds() + securityProperties.getExpires().getSeconds())
                .build();
        String token = AES.encryptHex(GsonUtil.toJson(tokenContext));
        Cookie cookie = new Cookie(securityProperties.getTokenKey(), token);
        cookie.setMaxAge((int) securityProperties.getExpires().getSeconds());
        cookie.setDomain(UrlUtils.getDomain(request.getHeader("host"), 1));
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
