package com.stoicfree.free.module.core.mvc.passport;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.web.method.HandlerMethod;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.stoicfree.free.module.core.common.enums.ErrorCode;
import com.stoicfree.free.module.core.common.gson.GsonUtil;
import com.stoicfree.free.module.core.common.support.Assert;
import com.stoicfree.free.module.core.common.exception.BizException;
import com.stoicfree.free.module.core.common.support.GlobalCache;
import com.stoicfree.free.module.core.common.support.ID;
import com.stoicfree.free.module.core.common.support.Safes;
import com.stoicfree.free.module.core.common.util.LambdaUtils;
import com.stoicfree.free.module.core.common.util.ReflectionUtils;
import com.stoicfree.free.module.core.common.util.UrlUtils;
import com.stoicfree.free.module.core.mvc.config.PassportProperties;
import com.stoicfree.free.module.core.mvc.passport.anotation.Auth;
import com.stoicfree.free.module.core.mvc.passport.anotation.NoLogin;
import com.stoicfree.free.module.core.mvc.passport.context.TokenContext;
import com.stoicfree.free.module.core.mvc.passport.context.UserColumn;
import com.stoicfree.free.module.core.mvc.passport.context.UserContext;

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
public class PassGate<E> {
    private static final AES AES = SecureUtil.aes("(7djPSrws9K2MJk8".getBytes(StandardCharsets.UTF_8));
    private final BaseMapper<E> mapper;
    private final UserColumn<E> column;
    private final PassportProperties properties;

    public PassGate(BaseMapper<E> mapper, UserColumn<E> column, PassportProperties properties) {
        this.mapper = mapper;
        this.column = column;
        this.properties = properties;
    }

    @PostConstruct
    private void init() {
        Assert.notNull(mapper, ErrorCode.INVALID_PARAMS, "mapper not be null");
        Assert.allFieldsValid(column, ErrorCode.INVALID_PARAMS, "%s must be valid", "column not be null");
        Assert.notNull(properties, ErrorCode.INVALID_PARAMS, "properties not be null");
    }

    public void register(E entity) {
        // 校验用户是否存在
        Object username = ReflectionUtils.getFieldValue(entity, fn(column.getUsername()));
        LambdaQueryWrapper<E> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(column.getUsername(), username);
        E user = mapper.selectOne(queryWrapper);
        if (user != null) {
            return;
        }

        // 密码加密
        String password = (String) ReflectionUtils.getFieldValue(entity, fn(column.getPassword()));
        ReflectionUtils.setFieldValue(entity, fn(column.getPassword()), BCrypt.hashpw(password));

        // 生成uuid
        ReflectionUtils.setFieldValue(entity, fn(column.getUuid()), ID.SHORT_SNOWFLAKE.nextIdStr());

        mapper.insert(entity);
    }

    public void login(String username, String password, HttpServletRequest request, HttpServletResponse response) {
        // 获取用户
        LambdaQueryWrapper<E> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(column.getUsername(), username);
        E user = mapper.selectOne(queryWrapper);
        Assert.notNull(user, ErrorCode.USER_NOT_FOUND);

        // 校验密码
        String dbPassword = (String) ReflectionUtils.getFieldValue(user, fn(column.getPassword()));
        Assert.isTrue(BCrypt.checkpw(password, dbPassword), ErrorCode.PASSWORD_ERROR);

        // 校验状态
        boolean enable = (boolean) ReflectionUtils.getFieldValue(user, fn(column.getEnable()));
        Assert.isTrue(enable, ErrorCode.USER_DISABLE);

        // 获取uuid
        String uuid = (String) ReflectionUtils.getFieldValue(user, fn(column.getUuid()));

        // 写入登录状态
        refreshToken(uuid, dbPassword, request, response);
    }

    public void verifyLogin(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 验证免登录
        if (isNoLogin(handler)) {
            return;
        }

        // 获取token
        String token = Safes.of(ServletUtil.getCookie(request, properties.getTokenKey()), Cookie::getValue);
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
        queryWrapper.eq(column.getUuid(), uuid);
        E user = mapper.selectOne(queryWrapper);
        Assert.notNull(user, ErrorCode.USER_NOT_FOUND);

        // 校验密码
        String dbPassword = (String) ReflectionUtils.getFieldValue(user, fn(column.getPassword()));
        Assert.equals(tokenContext.getPassword(), dbPassword, ErrorCode.PASSWORD_ERROR);

        // 校验状态
        boolean enable = (boolean) ReflectionUtils.getFieldValue(user, fn(column.getEnable()));
        Assert.isTrue(enable, ErrorCode.USER_DISABLE);

        // 校验角色
        String userRole = (String) ReflectionUtils.getFieldValue(user, fn(column.getRoles()));
        Set<String> userRoles = Safes.of(userRole.split(",")).stream().map(String::toLowerCase)
                .collect(Collectors.toSet());
        checkAuth(userRoles, handler);

        // 刷新token
        if ((tokenContext.getExpires() - DateUtil.currentSeconds()) < (tokenContext.getExpires() / 2)) {
            refreshToken(uuid, dbPassword, request, response);
        }

        // 存储用户上下文
        String username = (String) ReflectionUtils.getFieldValue(user, fn(column.getUsername()));
        UserContext.set(UserContext.User.builder().uuid(uuid).username(username).userRoles(userRoles).build());
    }

    public void changePassword(String username, String oldPassword, String newPassword) {
        // 获取用户
        LambdaQueryWrapper<E> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(column.getUsername(), username);
        E user = mapper.selectOne(queryWrapper);
        Assert.notNull(user, ErrorCode.USER_NOT_FOUND);

        // 校验旧密码
        String dbPassword = (String) ReflectionUtils.getFieldValue(user, fn(column.getPassword()));
        Assert.isTrue(BCrypt.checkpw(oldPassword, dbPassword), ErrorCode.OLD_PASSWORD_ERROR);

        // 更新密码
        LambdaUpdateWrapper<E> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(column.getPassword(), BCrypt.hashpw(newPassword));
        updateWrapper.eq(column.getUuid(), ReflectionUtils.getFieldValue(user, fn(column.getUuid())));
        mapper.update(null, updateWrapper);
    }

    public void updateRoles(String username, Set<String> roles) {
        // 获取用户
        LambdaQueryWrapper<E> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(column.getUsername(), username);
        E user = mapper.selectOne(queryWrapper);
        Assert.notNull(user, ErrorCode.USER_NOT_FOUND);

        // 更新角色
        LambdaUpdateWrapper<E> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(column.getRoles(), String.join(",", roles))
                .eq(column.getUuid(), ReflectionUtils.getFieldValue(user, fn(column.getUuid())));
        mapper.update(null, updateWrapper);
    }

    public void updateEnable(String username, boolean enable) {
        // 获取用户
        LambdaQueryWrapper<E> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(column.getUsername(), username);
        E user = mapper.selectOne(queryWrapper);
        Assert.notNull(user, ErrorCode.USER_NOT_FOUND);

        // 更新状态
        LambdaUpdateWrapper<E> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(column.getEnable(), enable)
                .eq(column.getUuid(), ReflectionUtils.getFieldValue(user, fn(column.getUuid())));
        mapper.update(null, updateWrapper);
    }

    private void refreshToken(String uuid, String password, HttpServletRequest request, HttpServletResponse response) {
        TokenContext tokenContext = TokenContext.builder()
                .uuid(uuid).password(password)
                .random(RandomUtil.randomString(8))
                .expires(DateUtil.currentSeconds() + properties.getExpires().getSeconds())
                .build();
        String token = AES.encryptHex(GsonUtil.toJson(tokenContext));
        Cookie cookie = new Cookie(properties.getTokenKey(), token);
        cookie.setMaxAge((int) properties.getExpires().getSeconds());
        cookie.setDomain(UrlUtils.getDomain(request.getHeader("host"), 1));
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    private boolean isNoLogin(Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return false;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        NoLogin noLogin = handlerMethod.getMethodAnnotation(NoLogin.class);
        if (noLogin == null) {
            Class<?> controller = handlerMethod.getBeanType();
            noLogin = controller.getDeclaredAnnotation(NoLogin.class);
        }
        return noLogin != null;
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

    private String fn(SFunction<E, ?> filed) {
        return GlobalCache.<SFunction<E, ?>, String>cache(getClass().getName()).getIfAbsent(
                filed, () -> LambdaUtils.getFieldName(filed)
        );
    }
}
