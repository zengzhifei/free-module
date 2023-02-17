package com.stoicfree.free.mvc.module.security.service;

import org.springframework.beans.factory.InitializingBean;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stoicfree.free.common.module.enums.ErrorCode;
import com.stoicfree.free.common.module.support.Assert;
import com.stoicfree.free.common.module.support.ID;
import com.stoicfree.free.mvc.module.security.context.UserColumn;

import cn.hutool.crypto.digest.BCrypt;

/**
 * @author zengzhifei
 * @date 2023/2/17 14:42
 */
public class UserRegisterService<E> extends AbstractUserService<E> implements InitializingBean {
    private final BaseMapper<E> mapper;
    private final UserColumn<E> userColumn;
    private final String usernameFiledName;
    private final String passwordFiledName;
    private final String uuidFiledName;

    public UserRegisterService(BaseMapper<E> mapper, UserColumn<E> userColumn) {
        this.mapper = mapper;
        this.userColumn = userColumn;
        this.usernameFiledName = getFieldName(userColumn.getUsername());
        this.passwordFiledName = getFieldName(userColumn.getPassword());
        this.uuidFiledName = getFieldName(userColumn.getUuid());
    }

    public void register(E entity) {
        try {
            // 校验用户是否存在
            Object usernameValue = getFieldValue(entity, usernameFiledName);
            LambdaQueryWrapper<E> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(userColumn.getUsername(), usernameValue);
            E user = mapper.selectOne(queryWrapper);
            if (user != null) {
                return;
            }

            // 密码加密
            String passwordValue = (String) getFieldValue(entity, passwordFiledName);
            setFieldValue(entity, passwordFiledName, BCrypt.hashpw(passwordValue));

            // 生成uuid
            setFieldValue(entity, uuidFiledName, ID.SNOWFLAKE.nextIdStr());

            mapper.insert(entity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(userColumn, ErrorCode.INVALID_PARAMS, "userColumn not be null");
        Assert.notNull(userColumn.getUsername(), ErrorCode.INVALID_PARAMS, "userColumn username not be null");
        Assert.notNull(userColumn.getPassword(), ErrorCode.INVALID_PARAMS, "userColumn password not be null");
        Assert.notNull(userColumn.getUuid(), ErrorCode.INVALID_PARAMS, "userColumn uuid not be null");
    }
}
