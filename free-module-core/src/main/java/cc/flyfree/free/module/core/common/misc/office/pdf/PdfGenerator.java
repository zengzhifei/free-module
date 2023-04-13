package cc.flyfree.free.module.core.common.misc.office.pdf;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.fontbox.ttf.TrueTypeCollection;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.rendering.PDFRenderer;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zengzhifei
 * @date 2022/11/22 16:01
 */
@Slf4j
public class PdfGenerator {
    private static BaseFont baseFont;
    private static TrueTypeCollection ttc;

    public static boolean checkTextInPDFont(String text) {
        try {
            getPDFont(null).getStringWidth(text);
            return true;
        } catch (Exception e) {
            log.error("", e);
        }
        return false;
    }

    public static String toImageBase64(byte[] imageBytes) {
        return Base64.encodeBase64String(imageBytes);
    }

    public static byte[] toImage(String pdfUrl, int page, float dpi) {
        URL url = URLUtil.url(pdfUrl);
        try (InputStream inputStream = URLUtil.getStream(url)) {
            byte[] bytes = IoUtil.readBytes(inputStream);
            return toImage(bytes, page, dpi);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] toImage(byte[] pdfBytes, int page, float dpi) {
        long start = System.currentTimeMillis();
        PDDocument document = null;
        ByteArrayOutputStream outputStream = null;
        try {
            // 载入pdf文档
            document = PDDocument.load(pdfBytes);
            // 读取PDF文件
            PDFRenderer renderer = new PDFRenderer(document);
            // pdf转图片
            BufferedImage bufferedImage = renderer.renderImageWithDPI(page - 1, dpi);
            // 生成图片流
            outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", outputStream);

            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("PdfGenerator toImage error", e);
            throw new RuntimeException(e);
        } finally {
            close(outputStream, document);
            log("PdfGenerator toImage cost time : {}", System.currentTimeMillis() - start);
        }
    }

    public static <T> String toBase64(byte[] pdfBytes) {
        return Base64.encodeBase64String(pdfBytes);
    }

    public static <T> byte[] outputWithForm(String templateFilePath, T formData) {
        ByteArrayOutputStream outputStream = fillPdfWithForm(templateFilePath, formData);
        return outputStream.toByteArray();
    }

    public static <T> byte[] outputWithPoi(String templateFilePath, T formData) {
        ByteArrayOutputStream outputStream = fillPdfWithPoi(templateFilePath, formData);
        return outputStream.toByteArray();
    }

    public static int getNumberOfPages(String pdfUrl) {
        long start = System.currentTimeMillis();
        PDDocument document = null;
        try {
            if (StringUtils.startsWithIgnoreCase(pdfUrl, "http")) {
                URL url = URLUtil.url(pdfUrl);
                try (InputStream inputStream = URLUtil.getStream(url)) {
                    document = PDDocument.load(inputStream);
                }
            } else {
                document = PDDocument.load(FileUtil.file(pdfUrl));
            }
            return document.getNumberOfPages();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            close(null, document);
            log("PdfGenerator getNumberOfPages cost time : {}", System.currentTimeMillis() - start);
        }
    }

    private static <T> ByteArrayOutputStream fillPdfWithForm(String templateFilePath, T formData) {
        long start = System.currentTimeMillis();
        PdfReader pdfReader = null;
        ByteArrayOutputStream outputStream = null;
        PdfStamper pdfStamper = null;
        try {
            // 读取PDF模版文件
            pdfReader = new PdfReader(templateFilePath);
            // 构建输出流
            outputStream = new ByteArrayOutputStream();
            // 构建PDF对象
            pdfStamper = new PdfStamper(pdfReader, outputStream);
            // 获取表单对象
            AcroFields form = pdfStamper.getAcroFields();
            // 设置表单字体
            form.addSubstitutionFont(getBaseFont());
            // 填充表单
            fillWithForm(pdfStamper, form, formData, null, null);
            // 设置pdf只读
            pdfStamper.setFormFlattening(true);

            return outputStream;
        } catch (Exception e) {
            log.error("PdfGenerator fillPdfWithForm error", e);
            throw new RuntimeException(e);
        } finally {
            close(pdfStamper, outputStream, pdfReader);
            log("PdfGenerator fillPdfWithForm cost time : {}", System.currentTimeMillis() - start);
        }
    }

    private static <T> ByteArrayOutputStream fillPdfWithPoi(String templateFilePath, T formData) {
        long start = System.currentTimeMillis();
        PDDocument document = null;
        ByteArrayOutputStream outputStream = null;
        try {
            // 读取PDF模版文件
            if (StringUtils.startsWithIgnoreCase(templateFilePath, "http")) {
                URL url = URLUtil.url(templateFilePath);
                try (InputStream inputStream = URLUtil.getStream(url)) {
                    document = PDDocument.load(inputStream);
                }
            } else {
                document = PDDocument.load(FileUtil.file(templateFilePath));
            }
            // 获取字体
            PDFont font = getPDFont(document);
            // 获取poi对象
            PoiHelper helper = new PoiHelper(document);
            // 填充poi
            fillWithPoi(document, font, helper, formData, null);
            // 构建输出流
            outputStream = new ByteArrayOutputStream();
            // 写入输出流
            document.save(outputStream);

            return outputStream;
        } catch (Exception e) {
            log.error("PdfGenerator fillPdfWithPoi error", e);
            throw new RuntimeException(e);
        } finally {
            close(outputStream, document);
            log("PdfGenerator fillPdfWithPoi cost time : {}", System.currentTimeMillis() - start);
        }
    }

    private static <T> void fillWithForm(PdfStamper stamper, AcroFields form, T formData,
                                         String parentKeyPrefix, String keySuffix) throws Exception {
        // 获取全部字段
        Field[] fields = ReflectUtil.getFieldsDirectly(formData.getClass(), true);
        // 相同字段，子类字段覆盖父类字段
        CollectionUtils.reverseArray(fields);

        // 填充表单
        for (Field field : fields) {
            // 字段解析
            field.setAccessible(true);
            Object value = field.get(formData);
            // 过滤无效字段
            if (Objects.isNull(value)) {
                continue;
            }

            // 注解解析
            PdfForm pdfForm = field.getDeclaredAnnotation(PdfForm.class);
            PdfForm.Type type = PdfForm.Type.TEXT;
            String keyPrefix = parentKeyPrefix;
            String key = field.getName();
            if (Objects.nonNull(pdfForm)) {
                type = pdfForm.type();
                keyPrefix = StringUtils.defaultIfBlank(pdfForm.keyPrefix(), keyPrefix);
                key = StringUtils.defaultIfBlank(pdfForm.key(), key);
            }
            // 拼接全字段名
            String fullKey = Stream.of(keyPrefix, key, keySuffix).filter(Objects::nonNull)
                    .collect(Collectors.joining());

            // 特殊字段类型处理
            Class<?> fieldType = field.getType();
            if (ClassUtils.isAssignable(fieldType, Collection.class, true)) {
                int i = 0;
                for (Object childValue : ((Collection<?>) value)) {
                    fillWithForm(stamper, form, childValue, keyPrefix, String.valueOf(++i));
                }
                continue;
            }
            if (value.getClass().getClassLoader() != null) {
                fillWithForm(stamper, form, value, keyPrefix, null);
                continue;
            }

            // 转换填充内容
            String content = Convert.toStr(value, "");
            // 打印关键信息
            log("PdfGenerator fillWithForm key = {}, value = {}, content = {}", fullKey, value, content);
            // 填充内容
            if (type.equals(PdfForm.Type.IMAGE)) {
                // 填充图片
                int pageNo = form.getFieldPositions(fullKey).get(0).page;
                Rectangle signRect = form.getFieldPositions(fullKey).get(0).position;
                float x = signRect.getLeft();
                float y = signRect.getBottom();
                // 读图片
                Image image = Image.getInstance(content);
                // 获取操作的页面
                PdfContentByte under = stamper.getOverContent(pageNo);
                // 根据域的大小缩放图片
                image.scaleToFit(signRect.getWidth(), signRect.getHeight());
                // 添加图片
                image.setAbsolutePosition(x, y);
                under.addImage(image);
            } else {
                // 填充文本
                form.setField(fullKey, content);
                // 指定字体
                form.setFieldProperty(fullKey, "textfont", getBaseFont(), null);
            }
        }
    }

    private static <T> void fillWithPoi(PDDocument document, PDFont font, PoiHelper helper, T formData, Integer index)
            throws Exception {
        // 获取全部字段
        Field[] fields = ReflectUtil.getFieldsDirectly(formData.getClass(), true);
        // 相同字段，子类字段覆盖父类字段
        CollectionUtils.reverseArray(fields);

        // 填充表单
        for (Field field : fields) {
            // 字段解析
            field.setAccessible(true);
            String fieldName = field.getName();
            Object value = field.get(formData);

            // 过滤无效字段
            if (Objects.isNull(value)) {
                continue;
            }

            // 特殊字段类型处理
            Class<?> fieldType = field.getType();
            if (ClassUtils.isAssignable(fieldType, Collection.class, true)) {
                int i = 0;
                for (Object childValue : ((Collection<?>) value)) {
                    fillWithPoi(document, font, helper, childValue, i++);
                }
                continue;
            }
            if (value.getClass().getClassLoader() != null) {
                fillWithPoi(document, font, helper, value, null);
                continue;
            }

            // 初始化填充内容
            String content = Convert.toStr(value, "");
            content = StrUtil.removeAllLineBreaks(content);
            if (StringUtils.isBlank(content)) {
                continue;
            }

            // 计算字符数量，如果报错，说明存在不支持的字符
            float contentLength;
            try {
                contentLength = getTextLength(font, content);
            } catch (Exception e) {
                log.error("fillWithPoi content[{}] is not support", content, e);
                continue;
            }

            // 注解解析
            PdfPoi poi = field.getDeclaredAnnotation(PdfPoi.class);
            if (poi == null) {
                continue;
            }

            // 校验页数设置
            int maxPage = document.getNumberOfPages();
            int[] pages = Arrays.stream(poi.page()).filter(page -> {
                if (page > maxPage) {
                    log.warn("fillWithPoi {} page[{}] must less max page[{}]", fieldName, page, maxPage);
                    return false;
                }
                return true;
            }).toArray();

            // 过滤无效页码设置
            if (pages.length == 0) {
                continue;
            }

            // 获取关键词所有坐标
            List<KeywordPosition> positions = new ArrayList<>();
            for (int page : pages) {
                List<KeywordPosition> keywordPositions = helper.getKeywordPositions(page, poi.line(), poi.keyword());
                positions.addAll(keywordPositions);
            }

            // 校验关键词顺序设置
            int keywordIndex = index != null ? index : poi.order() - 1;
            if (keywordIndex >= positions.size()) {
                log.warn("fillWithPoi {} keyword index[{}] must less [{}]", fieldName, keywordIndex, positions.size());
                continue;
            }

            // 获取对应顺序关键词坐标
            KeywordPosition keywordPosition = positions.get(keywordIndex);
            List<KeywordPosition.CharPosition> charPositions = keywordPosition.getCharPositions();

            // 获取参照字符坐标
            KeywordPosition.CharPosition referChar;
            if (poi.direction().equals(PdfPoi.Direction.AFTER)) {
                referChar = charPositions.get(charPositions.size() - 1);
            } else {
                referChar = charPositions.get(0);
            }

            // 获取行间距
            float lineSpacing = referChar.getMaxHeight() * 0.5f;

            // 获取参照字体大小
            float fontSize = referChar.getFontSize();

            // 动态计算字体大小
            float areaWidth = poi.areaWidth();
            if (areaWidth > 0 && (contentLength * fontSize) > areaWidth) {
                float areaHeight = poi.areaHeight() > 0 ? poi.areaHeight() : fontSize + lineSpacing;
                int excess = areaHeight / (fontSize + lineSpacing) > 1 ? 1 : 0;
                while (true) {
                    double maxContentSize = Math.floor(areaWidth / fontSize)
                            * Math.floor((areaHeight / (fontSize + lineSpacing)) - excess);
                    if (maxContentSize >= contentLength) {
                        break;
                    }
                    fontSize -= 0.25;
                }
            }

            // 获取字体大小设置
            if (poi.fontSize() > 0) {
                fontSize = poi.fontSize();
            }

            // 内容折行
            List<String> contents = new ArrayList<>();
            if (areaWidth > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                float lineWidth = 0;
                for (Character contentChar : content.toCharArray()) {
                    if (lineWidth >= areaWidth) {
                        contents.add(stringBuilder.toString());
                        stringBuilder = new StringBuilder();
                        lineWidth = 0;
                        continue;
                    }
                    stringBuilder.append(contentChar.toString());
                    lineWidth += fontSize * getTextLength(font, contentChar.toString());
                }
                if (stringBuilder.toString().length() > 0) {
                    contents.add(stringBuilder.toString());
                }
            } else {
                contents.add(content);
            }

            // 获取参照行
            String referLine = contents.get(0);

            // 计算坐标
            float x;
            float y;

            // 方向坐标计算
            if (poi.direction().equals(PdfPoi.Direction.UP)) {
                x = referChar.getX();
                y = referChar.getEndY() + fontSize + lineSpacing;
            } else if (poi.direction().equals(PdfPoi.Direction.DOWN)) {
                x = referChar.getX();
                y = referChar.getEndY() - fontSize - lineSpacing;
            } else if (poi.direction().equals(PdfPoi.Direction.BEFORE)) {
                float lineWidth = fontSize * getTextLength(font, referLine);
                x = referChar.getX() - lineWidth;
                y = referChar.getEndY();
                if (contents.size() > 1 && referChar.getFontSize() > fontSize) {
                    y = y + referChar.getFontSize() - fontSize;
                }
            } else {
                x = referChar.getEndX();
                y = referChar.getEndY();
                if (contents.size() > 1 && referChar.getFontSize() > fontSize) {
                    y = y + referChar.getFontSize() - fontSize;
                }
            }

            // 对齐方式计算
            float contentWidth = contentLength * fontSize;
            if (areaWidth > contentWidth) {
                if (poi.align().equals(PdfPoi.Align.LEFT)) {
                    float offsetX = areaWidth - contentWidth;
                    if (poi.direction().equals(PdfPoi.Direction.BEFORE)) {
                        x = x - offsetX;
                    }
                } else if (poi.align().equals(PdfPoi.Align.CENTER)) {
                    float offsetX = (areaWidth - contentWidth) / 2;
                    if (poi.direction().equals(PdfPoi.Direction.BEFORE)) {
                        x = x - offsetX;
                    } else if (poi.direction().equals(PdfPoi.Direction.AFTER)) {
                        x = x + offsetX;
                    }
                } else if (poi.align().equals(PdfPoi.Align.RIGHT)) {
                    float offsetX = areaWidth - contentWidth;
                    if (poi.direction().equals(PdfPoi.Direction.AFTER)) {
                        x = x + offsetX;
                    }
                }
            }

            // 偏移量计算
            x = x + poi.offsetX();
            y = y + poi.offsetY();

            // 获取页面内容流
            PDPage page = document.getPage(keywordPosition.getPage() - 1);
            PDPageContentStream contentStream = new PDPageContentStream(document, page,
                    PDPageContentStream.AppendMode.APPEND, true, false);

            // 开始填充
            contentStream.beginText();

            // 设置字体及字号
            contentStream.setFont(font, fontSize);
            contentStream.newLineAtOffset(x, y);
            contentStream.setLeading(fontSize + lineSpacing);
            for (String text : contents) {
                // 写入内容
                contentStream.showText(text);
                // 换行
                contentStream.newLine();
            }

            // 结束填充
            contentStream.endText();

            // 关闭页面内容流
            contentStream.close();

            // 打印关键信息
            log("第{}页, {}, {}: {} {}, x: {}, y: {}, size: {}",
                    keywordPosition.getPage(), poi.keyword(), fieldName, content, keywordIndex, x, y, fontSize);
        }
    }

    private static BaseFont getBaseFont() throws DocumentException, IOException {
        if (baseFont == null) {
            // 使用中文字体，在模板文件中设置字体为中文字体 Adobe 宋体 std L
            // font = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", BaseFont.EMBEDDED);
            baseFont = BaseFont.createFont("default/font/simsun.ttc,0", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        }
        return baseFont;
    }

    private static PDFont getPDFont(PDDocument document) throws IOException {
        if (ttc == null) {
            ttc = new TrueTypeCollection(ResourceUtil.getStream("default/font/simsun.ttc"));
        }
        return PDType0Font.load(document, ttc.getFontByName("SimSun"), true);
    }

    private static void close(PdfStamper stamper, ByteArrayOutputStream outputStream, PdfReader reader) {
        if (stamper != null) {
            try {
                stamper.close();
            } catch (Exception e) {
                log.error("PdfGenerator PdfStamper close error", e);
            }
        }
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (Exception e) {
                log.error("PdfGenerator outputStream close error", e);
            }
        }
        if (reader != null) {
            try {
                reader.close();
            } catch (Exception e) {
                log.error("PdfGenerator PdfReader close error", e);
            }
        }
    }

    private static void close(ByteArrayOutputStream outputStream, PDDocument document) {
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (Exception e) {
                log.error("PdfGenerator outputStream close error", e);
            }
        }
        if (document != null) {
            try {
                document.close();
            } catch (Exception e) {
                log.error("PdfGenerator PDDocument close error", e);
            }
        }
    }

    private static float getTextLength(PDFont font, String text) throws IOException {
        return font.getStringWidth(text) / 1000;
    }

    private static void log(String content, Object... objects) {
        log.info(content, objects);
    }
}
