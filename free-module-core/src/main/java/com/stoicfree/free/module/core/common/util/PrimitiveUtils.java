package com.stoicfree.free.module.core.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zengzhifei
 * @date 2023/3/20 16:05
 */
public class PrimitiveUtils {
    public static double fenToYuan(Long fen) {
        return new BigDecimal(fen).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP).doubleValue();
    }

    public static Map<String, String> strToMap(String str, String firstSeparator, String secondSeparator) {
        Map<String, String> map = new HashMap<>(8);
        Arrays.stream(str.split(firstSeparator)).filter(kv -> kv.contains(secondSeparator))
                .map(kv -> kv.split(secondSeparator))
                .forEach(kv -> map.put(kv[0], kv[1]));
        return map;
    }
}
