package com.chafree.free.common.module.support;

import java.text.NumberFormat;
import java.util.Collections;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.StopWatch;

import lombok.extern.slf4j.Slf4j;

/**
 * 检测程序片段运行时间拓展,调试使用
 *
 * @author zengzhifei
 * @date 2022/12/12 10:35
 */
@Slf4j
public class StopWatchExpand {
    /**
     * StopWatch实例
     */
    private static StopWatch stopWatch;

    private static String threadName = "";

    /**
     * 断点计数器
     */
    private static final AtomicInteger COUNTER = new AtomicInteger(0);

    /**
     * StopWatch实例初始化
     */
    public static String initAndStart() {
        if (!Objects.equals(threadName, "")) {
            return "";
        }

        threadName = Thread.currentThread().getName();
        stopWatch = new StopWatch("[" + threadName + "]运行时间");
        return start();
    }

    /**
     * 暂停计时并开启下一个计时
     */
    public static String stopAndRestart() {
        if (!Objects.equals(threadName, Thread.currentThread().getName())) {
            return "";
        }

        stopWatch.stop();
        return start();
    }

    /**
     * 结束计时并打印
     */
    public static String endAndPrint() {
        if (!Objects.equals(threadName, Thread.currentThread().getName())) {
            return "";
        }

        stopWatch.stop();
        return prettyPrint();
    }

    /**
     * 开启计时
     */
    private static String start() {
        // 调用的类名
        String className = Thread.currentThread().getStackTrace()[3].getClassName();
        // 调用的方法名
        String methodName = Thread.currentThread().getStackTrace()[3].getMethodName();
        // 调用的行数
        int lineNumber = Thread.currentThread().getStackTrace()[3].getLineNumber();
        // 启动计时
        stopWatch.start(StringUtils.center(String.format("%s.%s:%s",
                className.substring(className.lastIndexOf(".") + 1), methodName, lineNumber), 70));
        // 计数
        String format = String.format("断点：%s 开始执行", COUNTER.incrementAndGet());
        log.info(format);
        return format;
    }

    /**
     * 格式化的统计输出
     *
     * @return 统计输出
     */
    private static String prettyPrint() {
        // 获取运行的毫秒数与秒数
        long totalTimeMillis = stopWatch.getTotalTimeMillis();
        double totalTimeSeconds = stopWatch.getTotalTimeSeconds();
        // 编写总结
        String shortSummary = String.format("%s: running time [ %s ms / %s s ]", stopWatch.getId(),
                String.format("%10s", totalTimeMillis), String.format("%10.3f", totalTimeSeconds));
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add(String.join("", Collections.nCopies(100, "-")));
        joiner.add(shortSummary);
        joiner.add(String.join("", Collections.nCopies(100, "-")));
        joiner.add(String.format("%10s%10s%10s%35s", "ms", "s", "%", "Task name"));
        joiner.add(String.join("", Collections.nCopies(100, "-")));
        NumberFormat pf = NumberFormat.getPercentInstance();
        pf.setMinimumIntegerDigits(3);
        pf.setGroupingUsed(false);
        for (StopWatch.TaskInfo task : stopWatch.getTaskInfo()) {
            String line = String.format("%10s%10.3f%10s%70s", task.getTimeMillis(), task.getTimeSeconds(),
                    pf.format((double) task.getTimeMillis() / totalTimeMillis), task.getTaskName());
            joiner.add(line);
        }
        joiner.add(String.join("", Collections.nCopies(100, "-")));
        String print = joiner.toString();
        log.info(print);
        return print;
    }
}