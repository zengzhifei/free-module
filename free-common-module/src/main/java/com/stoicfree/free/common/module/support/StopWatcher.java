package com.stoicfree.free.common.module.support;

import org.springframework.util.StopWatch;

/**
 * @author zengzhifei
 * @date 2022/8/14 21:07
 */
public class StopWatcher {
    private final StopWatch stopWatch;

    public StopWatcher() {
        this.stopWatch = new StopWatch();
        this.stopWatch.start();
    }

    public long stop() {
        if (this.stopWatch.isRunning()) {
            this.stopWatch.stop();
        }
        return this.stopWatch.getLastTaskTimeMillis();
    }

    public long end() {
        if (this.stopWatch.isRunning()) {
            this.stopWatch.stop();
        }
        return this.stopWatch.getTotalTimeMillis();
    }

    public void start() {
        if (this.stopWatch.isRunning()) {
            this.stopWatch.stop();
        }
        this.stopWatch.start();
    }
}
