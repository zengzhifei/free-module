package com.stoicfree.free.module.core.common.util;

import java.lang.reflect.Method;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * AopUtils
 *
 * @author zengzhifei
 * @date 2020/8/11 17:05
 */
public class AopUtils {
    /**
     * 解析注解SePL参数
     *
     * @param key
     * @param method
     * @param args
     * @param returnType
     * @param <T>
     *
     * @return
     */
    public static <T> T parseAnnotationParam(String key, Method method, Object[] args, Class<T> returnType) {
        // 获取参数列表
        LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();
        String[] params = discoverer.getParameterNames(method);

        // 解析参数
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();

        // 对key判空，避免解析异常
        if (StringUtils.isBlank(key) || params == null) {
            return null;
        }

        for (int i = 0; i < params.length; i++) {
            context.setVariable(params[i], args[i]);
        }

        return parser.parseExpression(key).getValue(context, returnType);
    }
}