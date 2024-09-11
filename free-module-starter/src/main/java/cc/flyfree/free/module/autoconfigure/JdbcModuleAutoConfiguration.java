package cc.flyfree.free.module.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;

import cc.flyfree.free.module.core.jdbc.config.JdbcProperties;
import cc.flyfree.free.module.core.jdbc.sharding.ShardingThreadLocal;

/**
 * @author zengzhifei
 * @date 2023/2/3 17:14
 */
@Configuration
@ConditionalOnExpression("${free.jdbc.enable:false}")
@EnableConfigurationProperties(JdbcProperties.class)
public class JdbcModuleAutoConfiguration {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        DynamicTableNameInnerInterceptor dynamicTableNameInnerInterceptor = new DynamicTableNameInnerInterceptor();
        TableNameHandler handler = (sql, tableName) -> {
            if (tableName.contains("{}")) {
                return tableName.replace("{}", ShardingThreadLocal.get(tableName));
            } else {
                return tableName;
            }
        };
        dynamicTableNameInnerInterceptor.setTableNameHandler(handler);
        // 分表插件
        interceptor.addInnerInterceptor(dynamicTableNameInnerInterceptor);
        // 分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
