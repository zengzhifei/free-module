package com.stoicfree.free.module.autoconfigure;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.google.common.collect.Maps;
import com.stoicfree.free.module.core.jdbc.config.JdbcProperties;
import com.stoicfree.free.module.core.jdbc.sharding.ShardingThreadLocal;

import cn.hutool.core.util.ClassUtil;

/**
 * @author zengzhifei
 * @date 2023/2/3 17:14
 */
@Configuration
@ConditionalOnExpression("${free.jdbc.enable:false}")
@EnableConfigurationProperties(JdbcProperties.class)
public class JdbcModuleAutoConfiguration {
    @Autowired
    private JdbcProperties jdbcProperties;

    @Bean
    public TableNameHandler tableNameHandler() {
        return (sql, tableName) -> tableName.replace("{}", ShardingThreadLocal.get(tableName).toString());
    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(TableNameHandler tableNameHandler) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        DynamicTableNameInnerInterceptor dynamicTableNameInnerInterceptor = new DynamicTableNameInnerInterceptor();
        Map<String, TableNameHandler> tableNameHandlerMap = Maps.newHashMap();

        Set<Class<?>> classes = ClassUtil.scanPackageByAnnotation(jdbcProperties.getEntityPackage(), TableName.class);
        for (Class<?> clazz : classes) {
            String tableName = clazz.getDeclaredAnnotation(TableName.class).value();
            if (tableName.contains("{}")) {
                tableNameHandlerMap.put(tableName, tableNameHandler);
            }
        }
        dynamicTableNameInnerInterceptor.setTableNameHandlerMap(tableNameHandlerMap);
        // 分表插件
        interceptor.addInnerInterceptor(dynamicTableNameInnerInterceptor);
        // 分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
