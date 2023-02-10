package com.stoicfree.free.common.module.gson;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/**
 * @author zengzhifei
 * @date 2022/12/8 23:10
 */
public class GsonIgnoreExclusionStrategy implements ExclusionStrategy {
    @Override
    public boolean shouldSkipClass(Class<?> aClass) {
        return aClass.getAnnotation(GsonIgnore.class) != null;
    }

    @Override
    public boolean shouldSkipField(FieldAttributes fieldAttributes) {
        return fieldAttributes.getAnnotation(GsonIgnore.class) != null;
    }
}
