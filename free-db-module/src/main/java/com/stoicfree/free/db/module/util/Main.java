package com.stoicfree.free.db.module.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.fill.Column;

/**
 * @author zengzhifei
 * @date 2023/2/12 17:48
 */
public class Main {
    public static void main(String[] args) {
        FastAutoGenerator.create("jdbc:mysql://10.138.44.197:8306/dr_common?useUnicode=true&characterEncoding=UTF-8"
                        + "&zeroDateTimeBehavior=convertToNull&noAccessToProcedureBodies=true&autoReconnect=true"
                        + "&allowMultiQueries=true", "mapp", "mapp")
                // 全局配置
                .globalConfig((scanner, builder) -> builder.author(scanner.apply("请输入作者名称？")).fileOverride())
                // 包配置
                .packageConfig((scanner, builder) -> builder.parent(scanner.apply("请输入包名？")))
                // 策略配置
                .strategyConfig(
                        (scanner, builder) -> builder.addInclude(getTables(scanner.apply("请输入表名，多个英文逗号分隔？所有输入 all")))
                                .controllerBuilder().enableRestStyle().enableHyphenStyle()
                                .entityBuilder().enableLombok().addTableFills(
                                        new Column("create_time", FieldFill.INSERT)
                                ).build())
                /*
                    模板引擎配置，默认 Velocity 可选模板引擎 Beetl 或 Freemarker
                   .templateEngine(new BeetlTemplateEngine())
                   .templateEngine(new FreemarkerTemplateEngine())
                 */
                .execute();
    }

    // 处理 all 情况
    protected static List<String> getTables(String tables) {
        return "all".equals(tables) ? Collections.emptyList() : Arrays.asList(tables.split(","));
    }
}
