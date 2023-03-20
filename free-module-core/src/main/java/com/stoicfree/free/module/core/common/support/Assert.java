package com.stoicfree.free.module.core.common.support;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;

import com.stoicfree.free.module.core.common.enums.ErrorCode;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ReflectUtil;

/**
 * @author zengzhifei
 * @date 2022/8/11 17:02
 */
public class Assert {
    public static <T> void notNull(T param, ErrorCode errorCode, String... messages) {
        if (param == null) {
            throwException(errorCode, messages);
        }
    }

    public static <T> void allFieldsValid(T param, ErrorCode errorCode, String fieldFormat, String... messages) {
        Assert.notNull(param, errorCode, messages);
        for (Field field : param.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Object value = ReflectUtil.getFieldValue(param, field);
            Class<?> fieldType = field.getType();
            if (ClassUtils.isAssignable(fieldType, String.class, true)) {
                Assert.notBlank((String) value, errorCode, String.format(fieldFormat, field.getName()));
            } else if (ClassUtils.isAssignable(fieldType, Collection.class, true)) {
                Assert.notEmpty((Collection<?>) value, errorCode, String.format(fieldFormat, field.getName()));
            } else {
                Assert.notNull(value, errorCode, String.format(fieldFormat, field.getName()));
            }
        }
    }

    public static <T> void isNull(T param, ErrorCode errorCode, String... messages) {
        if (param != null) {
            throwException(errorCode, messages);
        }
    }

    public static void isBlank(String param, ErrorCode errorCode, String... messages) {
        if (StringUtils.isNotBlank(param)) {
            throwException(errorCode, messages);
        }
    }

    public static void notBlank(String param, ErrorCode errorCode, String... messages) {
        if (StringUtils.isBlank(param)) {
            throwException(errorCode, messages);
        }
    }

    public static <T> void isEmpty(Collection<T> cols, ErrorCode errorCode, String... messages) {
        if (CollectionUtils.isNotEmpty(cols)) {
            throwException(errorCode, messages);
        }
    }

    public static <T> void notEmpty(Collection<T> cols, ErrorCode errorCode, String... messages) {
        if (CollectionUtils.isEmpty(cols)) {
            throwException(errorCode, messages);
        }
    }

    public static <T> void equals(Object a, Object b, ErrorCode errorCode, String... messages) {
        if (!Objects.equals(a, b)) {
            throwException(errorCode, messages);
        }
    }

    public static <T> void notEquals(Object a, Object b, ErrorCode errorCode, String... messages) {
        if (Objects.equals(a, b)) {
            throwException(errorCode, messages);
        }
    }

    public static void isTrue(Boolean express, ErrorCode errorCode, String... messages) {
        if (!Boolean.TRUE.equals(express)) {
            throwException(errorCode, messages);
        }
    }

    public static <T> void isTrue(Collection<T> cols, Function<T, Boolean> function, ErrorCode errorCode,
                                  String... messages) {
        for (T col : cols) {
            Boolean express = function.apply(col);
            if (!Boolean.TRUE.equals(express)) {
                throwException(errorCode, messages);
            }
        }
    }

    public static void isFalse(Boolean express, ErrorCode errorCode, String... messages) {
        if (!Boolean.FALSE.equals(express)) {
            throwException(errorCode, messages);
        }
    }

    public static <T> void in(T object, Collection<T> cols, ErrorCode errorCode, String... messages) {
        if (!cols.contains(object)) {
            throwException(errorCode, messages);
        }
    }

    public static <T> void notIn(T object, Collection<T> cols, ErrorCode errorCode, String... messages) {
        if (cols.contains(object)) {
            throwException(errorCode, messages);
        }
    }

    public static void match(String str, String regex, ErrorCode errorCode, String... messages) {
        if (!str.matches(regex)) {
            throwException(errorCode, messages);
        }
    }

    public static void length(String str, Integer min, Integer max, ErrorCode errorCode, String... messages) {
        if (str == null && min != null && min > 0) {
            throwException(errorCode, messages);
        }
        if (str != null && min != null && str.length() < min) {
            throwException(errorCode, messages);
        }
        if (str != null && max != null && str.length() > max) {
            throwException(errorCode, messages);
        }
    }

    public static void chineseLength(String str, Integer min, Integer max, ErrorCode errorCode,
                                     String... messages) {
        if (str == null && min != null && min > 0) {
            throwException(errorCode, messages);
        }
        byte[] bytes = str != null ? str.getBytes(CharsetUtil.CHARSET_GBK) : new byte[0];
        if (min != null && bytes.length < min) {
            throwException(errorCode, messages);
        }
        if (max != null && bytes.length > max) {
            throwException(errorCode, messages);
        }
    }

    public static void size(Collection<?> cols, Integer min, Integer max, ErrorCode errorCode,
                            String... messages) {
        if (cols == null && min != null && min > 0) {
            throwException(errorCode, messages);
        }
        if (cols != null && min != null && cols.size() < min) {
            throwException(errorCode, messages);
        }
        if (cols != null && max != null && cols.size() > max) {
            throwException(errorCode, messages);
        }
    }

    public static void size(Long num, Long min, Long max, ErrorCode errorCode, String... messages) {
        if (num == null) {
            throwException(errorCode, messages);
        }
        if (num != null && min != null && num < min) {
            throwException(errorCode, messages);
        }
        if (num != null && max != null && num > max) {
            throwException(errorCode, messages);
        }
    }

    public static void size(Integer num, Integer min, Integer max, ErrorCode errorCode, String... messages) {
        if (num == null) {
            throwException(errorCode, messages);
        }
        if (num != null && min != null && num < min) {
            throwException(errorCode, messages);
        }
        if (num != null && max != null && num > max) {
            throwException(errorCode, messages);
        }
    }

    public static void size(Double num, Double min, Double max, ErrorCode errorCode, String... messages) {
        if (num == null) {
            throwException(errorCode, messages);
        }
        if (num != null && min != null && num < min) {
            throwException(errorCode, messages);
        }
        if (num != null && max != null && num > max) {
            throwException(errorCode, messages);
        }
    }

    public static void size(Float num, Float min, Float max, ErrorCode errorCode, String... messages) {
        if (num == null) {
            throwException(errorCode, messages);
        }
        if (num != null && min != null && num < min) {
            throwException(errorCode, messages);
        }
        if (num != null && max != null && num > max) {
            throwException(errorCode, messages);
        }
    }

    private static void throwException(ErrorCode errorCode, String... messages) {
        if (messages.length > 0) {
            String message = Arrays.stream(messages).filter(Objects::nonNull).collect(Collectors.joining(","));
            throw new BizException(errorCode, message);
        } else {
            throw new BizException(errorCode);
        }
    }
}
