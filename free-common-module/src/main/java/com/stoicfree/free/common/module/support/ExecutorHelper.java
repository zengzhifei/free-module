package com.stoicfree.free.common.module.support;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * @author zengzhifei
 * @date 2022/8/15 21:06
 */
public class ExecutorHelper {
    public static ExecutorService newSingleThreadPool(String name, int queueSize) {
        return new ThreadPoolExecutor(1, 1, 0L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(queueSize), new ThreadFactoryBuilder().setNameFormat(name + "-%d").build(),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public static ExecutorService newFixedThreadPool(String name, int poolSize, int queueSize) {
        return new ThreadPoolExecutor(poolSize, poolSize, 0L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(queueSize), new ThreadFactoryBuilder().setNameFormat(name + "-%d").build(),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public static ExecutorService newCacheThreadPool(String name, int corePoolSize, int maxPoolSize, int queueSize) {
        return new ThreadPoolExecutor(corePoolSize, maxPoolSize, 60L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(queueSize), new ThreadFactoryBuilder().setNameFormat(name + "-%d").build(),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public static ExecutorService newScheduledThreadPool(String name, int corePoolSize) {
        return new ScheduledThreadPoolExecutor(corePoolSize,
                new ThreadFactoryBuilder().setNameFormat(name + "-%d").build(),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }
}
