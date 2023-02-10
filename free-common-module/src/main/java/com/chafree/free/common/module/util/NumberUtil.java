package com.chafree.free.common.module.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 处理精度问题
 *
 * @author zengzhifei
 * @date 2022/11/24 5:18 PM
 */
public class NumberUtil {
    public static double fenToYuan(Long fen) {
        return new BigDecimal(fen).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP).doubleValue();
    }
}
