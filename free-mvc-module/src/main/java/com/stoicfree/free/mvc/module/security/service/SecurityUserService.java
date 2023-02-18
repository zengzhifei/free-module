package com.stoicfree.free.mvc.module.security.service;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.method.HandlerMethod;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stoicfree.free.common.module.enums.ErrorCode;
import com.stoicfree.free.common.module.gson.GsonUtil;
import com.stoicfree.free.common.module.support.Assert;
import com.stoicfree.free.common.module.support.BizException;
import com.stoicfree.free.common.module.support.ID;
import com.stoicfree.free.common.module.support.Safes;
import com.stoicfree.free.common.module.util.UrlUtils;
import com.stoicfree.free.mvc.module.config.SecurityProperties;
import com.stoicfree.free.mvc.module.security.anotation.Auth;
import com.stoicfree.free.mvc.module.security.context.TokenContext;
import com.stoicfree.free.mvc.module.security.context.UserColumn;
import com.stoicfree.free.mvc.module.security.context.UserContext;

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
public class SecurityUserService<E> extends AbstractUserService<E> implements InitializingBean {
    private static final AES AES = SecureUtil.aes("(7djPSrws9K2MJk8".getBytes(StandardCharsets.UTF_8));
    private final BaseMapper<E> mapper;
    private final UserColumn<E> userColumn;
    private final SecurityProperties securityProperties;

    private String usernameFiledName;
    private String passwordFiledName;
    private String uuidFiledName;
    private String enableFiledName;
    private String rolesFiledName;

    public SecurityUserService(BaseMapper<E> mapper, UserColumn<E> userColumn, SecurityProperties securityProperties) {
        this.mapper = mapper;
        this.userColumn = userColumn;
        this.securityProperties = securityProperties;
    }

    public void register(E entity) {
        // 校验用户是否存在
        Object username = getFieldValue(entity, usernameFiledName);
        LambdaQueryWrapper<E> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(userColumn.getUsername(), username);
        E user = mapper.selectOne(queryWrapper);
        if (user != null) {
            return;
        }

        // 密码加密
        String password = (String) getFieldValue(entity, passwordFiledName);
        setFieldValue(entity, passwordFiledName, BCrypt.hashpw(password));

        // 生成uuid
        setFieldValue(entity, uuidFiledName, ID.SNOWFLAKE.nextIdStr());

        mapper.insert(entity);
    }

    public void login(String username, String password, HttpServletRequest request, HttpServletResponse response) {
        // 获取用户
        LambdaQueryWrapper<E> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(userColumn.getUsername(), username);
        E user = mapper.selectOne(queryWrapper);
        Assert.notNull(user, ErrorCode.USER_NOT_FOUND);

        // 校验密码
        String dbPassword = (String) getFieldValue(user, passwordFiledName);
        Assert.isTrue(BCrypt.checkpw(password, dbPassword), ErrorCode.PASSWORD_ERROR);

        // 校验状态
        boolean enable = (boolean) getFieldValue(user, enableFiledName);
        Assert.isTrue(enable, ErrorCode.USER_DISABLE);

        // 获取uuid
        String uuid = (String) getFieldValue(user, uuidFiledName);

        // 写入登录状态
        refreshToken(uuid, dbPassword, request, response);
    }

    public void verifyLogin(HttpServletRequest request, HttpServletResponse response, Object handler) {
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

        // 校验密码
        String dbPassword = (String) getFieldValue(user, passwordFiledName);
        Assert.equals(tokenContext.getPassword(), dbPassword, ErrorCode.PASSWORD_ERROR);

        // 校验状态
        boolean enable = (boolean) getFieldValue(user, enableFiledName);
        Assert.isTrue(enable, ErrorCode.USER_DISABLE);

        // 校验角色
        String userRole = (String) getFieldValue(user, rolesFiledName);
        Set<String> userRoles = Safes.of(userRole.split(",")).stream().map(String::toLowerCase)
                .collect(Collectors.toSet());
        checkAuth(userRoles, handler);

        // 刷新token
        if ((tokenContext.getExpires() - DateUtil.currentSeconds()) < (tokenContext.getExpires() / 2)) {
            refreshToken(uuid, dbPassword, request, response);
        }

        // 存储用户上下文
        String username = (String) getFieldValue(user, usernameFiledName);
        UserContext.set(UserContext.User.builder().uuid(uuid).username(username).userRoles(userRoles).build());
    }

    public void changePassword(String username, String oldPassword, String newPassword) {
        // 获取用户
        LambdaQueryWrapper<E> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(userColumn.getUsername(), username);
        E user = mapper.selectOne(queryWrapper);
        Assert.notNull(user, ErrorCode.USER_NOT_FOUND);

        // 校验旧密码
        String dbPassword = (String) getFieldValue(user, passwordFiledName);
        Assert.isTrue(BCrypt.checkpw(oldPassword, dbPassword), ErrorCode.OLD_PASSWORD_ERROR);

        // 更新密码
        LambdaUpdateWrapper<E> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(userColumn.getPassword(), BCrypt.hashpw(newPassword))
                .eq(userColumn.getUuid(), getFieldValue(user, uuidFiledName));
        mapper.update(null, updateWrapper);
    }

    public void updateRoles(String username, Set<String> roles) {
        // 获取用户
        LambdaQueryWrapper<E> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(userColumn.getUsername(), username);
        E user = mapper.selectOne(queryWrapper);
        Assert.notNull(user, ErrorCode.USER_NOT_FOUND);

        // 更新角色
        LambdaUpdateWrapper<E> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(userColumn.getRoles(), String.join(",", roles))
                .eq(userColumn.getUuid(), getFieldValue(user, uuidFiledName));
        mapper.update(null, updateWrapper);
    }

    public void updateEnable(String username, boolean enable) {
        // 获取用户
        LambdaQueryWrapper<E> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(userColumn.getUsername(), username);
        E user = mapper.selectOne(queryWrapper);
        Assert.notNull(user, ErrorCode.USER_NOT_FOUND);

        // 更新状态
        LambdaUpdateWrapper<E> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(userColumn.getEnable(), enable)
                .eq(userColumn.getUuid(), getFieldValue(user, uuidFiledName));
        mapper.update(null, updateWrapper);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(mapper, ErrorCode.INVALID_PARAMS, "mapper not be null");
        Assert.notNull(securityProperties, ErrorCode.INVALID_PARAMS, "securityProperties not be null");
        Assert.notNull(userColumn, ErrorCode.INVALID_PARAMS, "userColumn not be null");
        Assert.notNull(userColumn.getUsername(), ErrorCode.INVALID_PARAMS, "userColumn username not be null");
        Assert.notNull(userColumn.getPassword(), ErrorCode.INVALID_PARAMS, "userColumn password not be null");
        Assert.notNull(userColumn.getUuid(), ErrorCode.INVALID_PARAMS, "userColumn uuid not be null");
        Assert.notNull(userColumn.getEnable(), ErrorCode.INVALID_PARAMS, "userColumn enable not be null");
        Assert.notNull(userColumn.getRoles(), ErrorCode.INVALID_PARAMS, "userColumn roles not be null");

        this.usernameFiledName = getFieldName(userColumn.getUsername());
        this.passwordFiledName = getFieldName(userColumn.getPassword());
        this.uuidFiledName = getFieldName(userColumn.getUuid());
        this.enableFiledName = getFieldName(userColumn.getEnable());
        this.rolesFiledName = getFieldName(userColumn.getRoles());
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

    private void checkAuth(Set<String> userRoles, Object handler) {
        if (handler == null) {
            return;
        }

        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Auth auth = handlerMethod.getMethodAnnotation(Auth.class);
            if (auth == null) {
                Class<?> controller = handlerMethod.getBeanType();
                auth = controller.getDeclaredAnnotation(Auth.class);
            }
            if (auth == null) {
                return;
            }

            Set<String> roles = Safes.of(auth.roles()).stream().map(String::toLowerCase).collect(Collectors.toSet());
            Set<String> excludeRoles = Safes.of(auth.excludeRoles()).stream().map(String::toLowerCase)
                    .collect(Collectors.toSet());
            if (CollectionUtils.isNotEmpty(roles)) {
                Assert.isFalse(Collections.disjoint(userRoles, roles), ErrorCode.ROLE_NO_PERMISSION);
            } else if (CollectionUtils.isNotEmpty(excludeRoles)) {
                Assert.isTrue(Collections.disjoint(userRoles, excludeRoles), ErrorCode.ROLE_NO_PERMISSION);
            }
        }
    }
}
