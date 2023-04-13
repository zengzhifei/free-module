package cc.flyfree.free.module.core.common.support;

import java.util.function.Predicate;

import cc.flyfree.free.module.core.common.enums.ErrorCode;

import cn.hutool.core.util.ReflectUtil;

/**
 * @author zengzhifei
 * @date 2023/3/21 10:17
 */
public abstract class Validator<Child, P, C> {
    private Class<Child> clazz;
    protected P param;

    public static <Child extends Validator<Child, ?, ?>, P> Child baseCheck(Class<Child> clazz, P param) {
        Assert.notNull(clazz, ErrorCode.EMPTY_PARAMS);
        Assert.isValid(param, ErrorCode.EMPTY_PARAMS);

        Child child = ReflectUtil.newInstance(clazz);
        ReflectUtil.setFieldValue(child, "clazz", clazz);
        ReflectUtil.setFieldValue(child, "param", param);
        return child;
    }

    /**
     * 前置校验
     *
     * @return 对象本身
     */
    public Child preCheck() {
        return preCheck(null);
    }

    /**
     * 前置校验
     *
     * @param container 上下文
     *
     * @return 对象本身
     */
    public Child preCheck(C container) {
        return clazz.cast(this);
    }

    /**
     * 字段校验
     *
     * @return 对象本身
     */
    public Child fieldCheck() {
        return fieldCheck(null);
    }

    /**
     * 字段校验
     *
     * @param container 上下文
     *
     * @return 对象本身
     */
    public Child fieldCheck(C container) {
        return clazz.cast(this);
    }

    /**
     * 业务校验
     *
     * @return 对象本身
     */
    public Child bizCheck() {
        return bizCheck(null);
    }

    /**
     * 业务校验
     *
     * @param container 上下文
     *
     * @return 对象本身
     */
    public Child bizCheck(C container) {
        return clazz.cast(this);
    }

    /**
     * 后置校验
     *
     * @param predicate 参数验证
     *
     * @return
     */
    public final Child afterCheck(Predicate<P> predicate) {
        Assert.isTrue(predicate == null || predicate.test(param), ErrorCode.EMPTY_PARAMS, "后置校验失败");
        return clazz.cast(this);
    }
}
