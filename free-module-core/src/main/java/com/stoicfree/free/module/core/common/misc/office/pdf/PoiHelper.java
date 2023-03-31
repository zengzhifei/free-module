package com.stoicfree.free.module.core.common.misc.office.pdf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.TextPosition;
import org.springframework.beans.BeanUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zengzhifei
 * @date 2022/12/5 15:13
 */
@Slf4j
public class PoiHelper {
    private final Map<Integer, List<LineTextPosition>> pageLineTextPositionsMap = new HashMap<>();
    private final PDDocument document;

    public PoiHelper(PDDocument document) {
        this.document = document;
    }

    public List<KeywordPosition> getKeywordPositions(int page, String keyword) {
        return getKeywordPositions(page, -1, keyword);
    }

    public List<KeywordPosition> getKeywordPositions(int page, int line, String keyword) {
        List<KeywordPosition> keywordPositions = new ArrayList<>();

        // 获取页面行字符信息
        List<LineTextPosition> lineTextPositions = getLineTextPositions(page);

        // 转换页面全字符信息
        List<TextPosition> textPositions = lineTextPositions.stream()
                .filter(e -> line <= 0 || e.getLine() == line)
                .map(LineTextPosition::getTextPositions)
                .flatMap(Collection::stream).collect(Collectors.toList());

        // 如果总长度小于关键词，则返回空
        if (textPositions.size() < keyword.length()) {
            return keywordPositions;
        }

        // 使用滑动窗口计算符合的关键词坐标信息
        Deque<TextPosition> keywordPositionDeque = new LinkedList<>();

        // 计算第一组窗口
        for (int i = 0; i < keyword.length(); i++) {
            keywordPositionDeque.offer(textPositions.get(i));
        }

        // 判断第一组窗口是否等于关键词
        String firstText = keywordPositionDeque.stream().map(TextPosition::getUnicode).collect(Collectors.joining());
        if (keyword.equals(firstText)) {
            keywordPositions.add(build(page, keywordPositionDeque));
        }

        // 从下个窗口继续计算
        for (int i = keyword.length(); i < textPositions.size(); i++) {
            // 删除第一个元素
            keywordPositionDeque.poll();
            // 追加后一个元素
            keywordPositionDeque.offer(textPositions.get(i));
            // 判断当前创建是否等于关键词
            String currText = keywordPositionDeque.stream().map(TextPosition::getUnicode).collect(Collectors.joining());
            if (keyword.equals(currText)) {
                keywordPositions.add(build(page, keywordPositionDeque));
            }
        }

        return keywordPositions;
    }

    public List<KeywordPosition.CharPosition> getPagePositions(int page) {
        return getPagePositions(page, -1);
    }

    public List<KeywordPosition.CharPosition> getPagePositions(int page, int line) {
        List<LineTextPosition> lineTextPositions = getLineTextPositions(page);
        return lineTextPositions.stream().filter(e -> line <= 0 || e.getLine() == line)
                .map(LineTextPosition::getTextPositions)
                .flatMap(Collection::stream).map(this::convert)
                .collect(Collectors.toList());
    }

    public List<LineTextPosition> getLineTextPositions(int page) {
        List<LineTextPosition> lineTextPositions = pageLineTextPositionsMap.get(page);
        if (CollectionUtils.isNotEmpty(lineTextPositions)) {
            return lineTextPositions;
        }

        try {
            LineTextStripper stripper = new LineTextStripper(document, page);
            List<LineTextPosition> positions = stripper.getLineTextPositions();
            pageLineTextPositionsMap.put(page, positions);
            return positions;
        } catch (Exception e) {
            log.error("PoiHelper getTextRenderInfos error, page = {}", page, e);
        }

        return new ArrayList<>();
    }

    public String print(int page) {
        StringJoiner joiner = new StringJoiner("\n");

        List<LineTextPosition> lineTextPositions = getLineTextPositions(page);
        for (LineTextPosition lineTextPosition : lineTextPositions) {
            StringJoiner lineJoiner = new StringJoiner("\t|\t");
            lineJoiner.add(String.format("第%2s行", lineTextPosition.getLine()));
            for (TextPosition position : lineTextPosition.getTextPositions()) {
                lineJoiner.add(String.format("(%7.3f, %7.3f)\t%s\t(%7.3f, %7.3f)", position.getX(), position.getY(),
                        position.getUnicode(), position.getEndX(), position.getEndY()));
            }
            joiner.add(lineJoiner.toString());
        }

        return joiner.toString();
    }

    private KeywordPosition build(int page, Deque<TextPosition> keywordPositionDeque) {
        String keyword = keywordPositionDeque.stream().map(TextPosition::getUnicode).collect(Collectors.joining());
        List<KeywordPosition.CharPosition> charPositions = keywordPositionDeque.stream()
                .map(this::convert).collect(Collectors.toList());
        return KeywordPosition.builder().page(page).keyword(keyword).charPositions(charPositions).build();
    }

    private KeywordPosition.CharPosition convert(TextPosition textPosition) {
        KeywordPosition.CharPosition charPosition = new KeywordPosition.CharPosition();
        BeanUtils.copyProperties(textPosition, charPosition);
        charPosition.setFontSizePt((int) textPosition.getFontSizeInPt());
        charPosition.setMaxHeight(textPosition.getHeightDir());
        charPosition.setDirection(textPosition.getDir());
        return charPosition;
    }
}
