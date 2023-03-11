package com.stoicfree.free.module.core.common.support.pdf;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zengzhifei
 * @date 2022/12/4 12:01
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PdfPoi {
    /**
     * 页码
     */
    int[] page();

    /**
     * 关键词
     */
    String keyword();

    /**
     * 行数
     */
    int line() default 0;

    /**
     * 顺序
     */
    int order() default 1;

    /**
     * 方向
     */
    Direction direction() default Direction.AFTER;

    /**
     * 对齐
     */
    Align align() default Align.LEFT;

    /**
     * x偏移量
     */
    float offsetX() default 0;

    /**
     * y偏移量
     */
    float offsetY() default 0;

    /**
     * 字体
     */
    float fontSize() default 0;

    /**
     * 填充区域宽度
     */
    float areaWidth() default 0;

    /**
     * 填充区域高度
     */
    float areaHeight() default 0;

    enum Direction {
        // 方向
        UP, DOWN, BEFORE, AFTER
    }

    enum Align {
        // 位置
        LEFT, CENTER, RIGHT
    }
}
