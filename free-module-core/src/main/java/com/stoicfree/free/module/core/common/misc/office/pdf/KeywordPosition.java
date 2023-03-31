package com.stoicfree.free.module.core.common.misc.office.pdf;

import java.util.List;

import lombok.Builder;
import lombok.Data;

/**
 * @author zengzhifei
 * @date 2022/12/15 22:09
 */
@Data
@Builder
public class KeywordPosition {
    private int page;
    private String keyword;
    private List<CharPosition> charPositions;

    @Data
    public static class CharPosition {
        private float endX;
        private float endY;
        private float maxHeight;
        private int rotation;
        private float x;
        private float y;
        private float pageHeight;
        private float pageWidth;
        private float widthOfSpace;
        private float fontSize;
        private int fontSizePt;
        private String unicode;
        private float direction;
        private float xScale;
        private float yScale;
        private float xDirAdj;
        private float yDirAdj;
        private float widthDirAdj;
    }
}
