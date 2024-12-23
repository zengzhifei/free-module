package cc.flyfree.free.module.core.common.util;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import cn.hutool.core.img.ColorUtil;
import cn.hutool.core.img.FontUtil;
import cn.hutool.core.img.ImgUtil;

/**
 * @author zengzhifei
 * @date 2024/12/16 11:56
 */
public class ImageUtils {
    public static String text2Base64Image(String text, int width, int height) {
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = bufferedImage.createGraphics();
        // 启用抗锯齿
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 启用文本抗锯齿
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        // 设置高质量渲染
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setColor(ColorUtil.randomColor());
        g2.fillRect(0, 0, width, height);
        // 设置文本
        g2.setFont(FontUtil.createSansSerifFont(Math.min(width, height) / 3));
        g2.setColor(Color.WHITE);
        FontMetrics fontMetrics = g2.getFontMetrics();
        // 计算位置
        int textWidth = fontMetrics.stringWidth(text);
        int textHeight = fontMetrics.getAscent();
        int x = (width - textWidth) / 2;
        int y = (height + textHeight) / 2;
        g2.drawString(text, x, y);

        return ImgUtil.toBase64DataUri(bufferedImage, ImgUtil.IMAGE_TYPE_JPG);
    }
}
