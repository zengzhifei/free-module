package cc.flyfree.free.module.core.common.misc.office.pdf;

import java.util.List;

import org.apache.pdfbox.text.TextPosition;

import lombok.Builder;
import lombok.Data;

/**
 * @author zengzhifei
 * @date 2022/12/15 22:30
 */
@Data
@Builder
public class LineTextPosition {
    private int line;
    private List<TextPosition> textPositions;
}
