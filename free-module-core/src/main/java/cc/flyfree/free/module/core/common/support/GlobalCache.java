package cc.flyfree.free.module.core.common.support;

import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.map.LRUMap;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import cn.hutool.core.thread.ThreadUtil;

/**
 * @author zengzhifei
 * @date 2023/3/18 00:26
 */
public class GlobalCache<K, V> {
    private static final LRUMap GLOBAL_CACHE_MAP = new LRUMap(256);
    private Cache<K, V> cache = null;

    private GlobalCache() {
    }

    public static <K, V> GlobalCache<K, V> cache() {
        String type = getDefaultCacheType();
        return cache(type);
    }

    public static <K, V> GlobalCache<K, V> cache(String type) {
        return cache(type, 1, TimeUnit.HOURS);
    }

    public static <K, V> GlobalCache<K, V> cache(long duration, TimeUnit unit) {
        String type = getDefaultCacheType();
        return cache(type, duration, unit);
    }

    public static <K, V> GlobalCache<K, V> cache(String type, long duration, TimeUnit unit) {
        return cache(type, duration, unit, 32, 1024);
    }

    public static <K, V> GlobalCache<K, V> cache(long duration, TimeUnit unit, int initialCapacity, long maximumSize) {
        String type = getDefaultCacheType();
        return cache(type, buildCache(duration, unit, initialCapacity, maximumSize));
    }

    public static <K, V> GlobalCache<K, V> cache(String type, long duration, TimeUnit unit, int initialCapacity,
                                                 long maximumSize) {
        return cache(type, buildCache(duration, unit, initialCapacity, maximumSize));
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

    private static String getDefaultCacheType() {
        StackTraceElement element = ThreadUtil.getStackTraceElement(5);
        return element.getClassName() + ":" + element.getMethodName() + ":" + element.getLineNumber();
    }

    private static <K, V> Cache<K, V> buildCache(long duration, TimeUnit unit, int initialCapacity, long maximumSize) {
        return CacheBuilder.newBuilder()
                .expireAfterWrite(duration, unit)
                .initialCapacity(initialCapacity)
                .maximumSize(maximumSize)
                .build();
    }

    @Override
    public String toString() {
        return "GlobalCache{" + cache.asMap() + '}';
    }

    public static String print() {
        return GLOBAL_CACHE_MAP.toString();
    }
}