package com.stoicfree.free.module.core.common.support;

import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.map.LRUMap;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * @author zengzhifei
 * @date 2023/3/18 00:26
 */
public class GlobalCache<K, V> {
    private static final LRUMap GLOBAL_CACHE_MAP = new LRUMap(256);
    private Cache<K, V> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .initialCapacity(32)
            .maximumSize(1024)
            .build();

    private GlobalCache() {
    }

    @SuppressWarnings("unchecked")
    public static <K, V> GlobalCache<K, V> cache(String type) {
        GlobalCache<K, V> globalCache = (GlobalCache<K, V>) GLOBAL_CACHE_MAP.get(type);
        if (globalCache == null) {
            globalCache = new GlobalCache<>();
            globalCache.cache = globalCache.buildDefaultCache();
            GLOBAL_CACHE_MAP.put(type, globalCache);
        }
        return globalCache;
    }

    @SuppressWarnings("unchecked")
    public static <K, V> GlobalCache<K, V> cache(String type, Cache<K, V> cache) {
        GlobalCache<K, V> globalCache = (GlobalCache<K, V>) GLOBAL_CACHE_MAP.get(type);
        if (globalCache == null) {
            globalCache = new GlobalCache<>();
            globalCache.cache = cache;
            GLOBAL_CACHE_MAP.put(type, globalCache);
        }
        return globalCache;
    }

    public void put(K key, V value) {
        cache.put(key, value);
    }

    public V get(K key) {
        return cache.getIfPresent(key);
    }

    public V getIfAbsent(K key, Callback2<V> callback) {
        V value = get(key);
        if (value != null) {
            return value;
        }

        V newVal = callback.call();
        put(key, newVal);

        return newVal;
    }

    private Cache<K, V> buildDefaultCache() {
        return CacheBuilder.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS)
                .initialCapacity(32)
                .maximumSize(1024)
                .build();
    }
}
