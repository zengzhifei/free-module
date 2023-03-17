package com.stoicfree.free.module.core.common.support.record;

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
public class RecordColumn<E> {
    /**
     * 主id
     */
    private SFunction<E, String> mainId;

    /**
     * 业务类型
     */
    private SFunction<E, Integer> type;

    /**
     * 操作行为
     */
    private SFunction<E, String> action;

    /**
     * 操作内容
     */
    private SFunction<E, String> content;

    /**
     * 扩展信息
     */
    private SFunction<E, String> ext;

    /**
     * 操作人
     */
    private SFunction<E, String> user;

    /**
     * 操作时间
     */
    private SFunction<E, Long> time;
}
