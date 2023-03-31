package com.stoicfree.free.module.core.common.biz.retry;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zengzhifei
 * @date 2022/12/8 15:21
 */
@AllArgsConstructor
public enum RetryStatus {
    // 重试任务类型
    PENDING_RETRY(0, "待重试"),
    RETRY_SUCCESS(1, "任务成功"),
    RETRY_FAIL(2, "任务失败"),
    RETRY_CANCEL(3, "任务取消"),
    ;

    @Getter
    private Integer status;
    @Getter
    private String name;
}
