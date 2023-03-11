package com.stoicfree.free.module.core.common.support.pdf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import com.google.common.collect.Lists;

/**
 * @author zengzhifei
 * @date 2022/12/15 22:30
 */
public class LineTextStripper extends PDFTextStripper {
    private int line = 1;
    private final List<LineTextPosition> lineTextPositions = new ArrayList<>();

    public LineTextStripper(PDDocument document, int page) throws IOException {
        super.setSortByPosition(true);
        super.setStartPage(page);
        super.setEndPage(page);

        Writer outputStream = new OutputStreamWriter(new ByteArrayOutputStream());
        super.writeText(document, outputStream);
    }

    public List<LineTextPosition> getLineTextPositions() {
        return lineTextPositions;
    }

    @Override
    protected void writeString(String text, List<TextPosition> textPositions) {
        if (CollectionUtils.isEmpty(textPositions)) {
            return;
        }
        TextPosition previous = textPositions.get(0);
        List<TextPosition> positions = Lists.newArrayList(previous);
        for (int i = 1; i < textPositions.size(); i++) {
            TextPosition current = textPositions.get(i);
            if (current.getX() < previous.getEndX() && StringUtils.isBlank(current.getUnicode())) {
                continue;
            }
            positions.add(current);
            previous = current;
        }
        LineTextPosition lineTextPosition = LineTextPosition.builder().line(line).textPositions(positions).build();
        this.lineTextPositions.add(lineTextPosition);
        line++;
    }
}
