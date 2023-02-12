package com.stoicfree.free.db.module.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zengzhifei
 * @date 2023/2/12 17:48
 */
public class AutoGenerator {
    public static void generate(GenerateConf conf) {
        FastAutoGenerator.create(conf.getUrl(), conf.getUsername(), conf.getPassword())
                // 全局配置
                .globalConfig((scanner, builder) -> {
                    String outputModule = StringUtils.defaultIfBlank(conf.getOutputModule(), "");
                    builder.fileOverride().disableOpenDir()
                            .outputDir(System.getenv("PWD") + "/" + outputModule + "/src/main/java")
                            .author(scanner.apply("请输入作者名称？"));
                })
                // 包配置
                .packageConfig((scanner, builder) -> {
                    builder.parent(conf.getParentPackage());
                })
                // 策略配置
                .strategyConfig((scanner, builder) -> {
                    String[] tablePrefix = conf.getTablePrefix() != null ? conf.getTablePrefix() : new String[0];
                    String[] ignoreColumns = conf.getIgnoreColumns() != null ? conf.getIgnoreColumns() : new String[0];
                    builder.addTablePrefix(tablePrefix)
                            .addInclude(getTables(scanner.apply("请输入表名，多个英文逗号分隔？所有输入all")))
                            .entityBuilder().disableSerialVersionUID().enableLombok().addIgnoreColumns(ignoreColumns)
                            .mapperBuilder().enableMapperAnnotation()
                            .serviceBuilder().formatServiceFileName("%sService")
                            .build();
                })
                // 模版配置
                .templateConfig((scanner, builder) -> {
                    builder.controller("").mapperXml("").build();
                })
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }

    private static List<String> getTables(String tables) {
        return "all".equals(tables) ? Collections.emptyList() : Arrays.asList(tables.split(","));
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GenerateConf {
        private String url;
        private String username;
        private String password;
        private String outputModule;
        private String parentPackage;
        private String[] ignoreColumns;
        private String[] tablePrefix;
    }
}
