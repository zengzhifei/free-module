package cc.flyfree.free.module.core.mvc.config;

import lombok.Data;

/**
 * @author zengzhifei
 * @date 2023/2/11 14:46
 */
@Data
public class CaptchaProperties {
    /**
     * 验证码样式:line,circle,shear,gif
     */
    private String style = "line";
    /**
     * 验证码区域宽度
     */
    private int width = 200;
    /**
     * 验证码区域高度
     */
    private int height = 100;
    /**
     * 验证码字符数量
     */
    private int codeCount = 4;
    /**
     * 线条验证码干扰元素个数
     */
    private int lineCount = 20;
    /**
     * 圆圈验证码干扰元素个数
     */
    private int circleCount = 20;
    /**
     * 扭曲验证码干扰线宽度
     */
    private int shearThickness = 4;
}
