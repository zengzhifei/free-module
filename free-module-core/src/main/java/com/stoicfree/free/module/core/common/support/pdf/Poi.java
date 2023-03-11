package com.stoicfree.free.module.core.common.support.pdf;

import lombok.Data;

/**
 * @author zengzhifei
 * @date 2022/12/5 15:20
 */
@Data
public class Poi {
    private String keyword;
    private Double leftX;
    private Double rightX;
    private Double topY;
    private Double downY;
    private Double width;
    private Integer size;
}
