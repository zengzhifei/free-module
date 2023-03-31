package com.stoicfree.free.module.core.common.biz.retry;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zengzhifei
 * @date 2023/2/17 21:18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetryColumn<E> {
    /**
     * 自增id
     */
    private SFunction<E, Long> id;

    /**
     * 主id
     */
    private SFunction<E, String> mainId;

    /**
     * 补偿任务类型
     */
    private SFunction<E, Integer> type;

    /**
     * 补偿任务内容
     */
    private SFunction<E, String> content;

    /**
     * 最多重试次数
     */
    private SFunction<E, Integer> maxTimes;

    /**
     * 重试次数
     */
    private SFunction<E, Integer> retryTimes;

    /**
     * 任务状态
     */
    private SFunction<E, Integer> status;

    /**
     * 扩展信息
     */
    private SFunction<E, String> ext;

    /**
     * 创建时间
     */
    private SFunction<E, Long> createTime;

    /**
     * 更新时间
     */
    private SFunction<E, Long> updateTime;
}
