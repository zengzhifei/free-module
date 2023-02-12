package com.stoicfree.free.db.module.config;

import javax.sql.DataSource;

import org.springframework.boot.jdbc.DataSourceBuilder;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zengzhifei
 * @date 2023/2/12 17:08
 */
@Slf4j
public abstract class AbstractDataSourceConfig {
    /**
     * 指定为数据源
     */
    public DataSource dataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    /**
     * 创建Mybatis的连接会话工厂实例
     */
    public MybatisSqlSessionFactoryBean mybatisSqlSessionFactoryBean(DataSource dataSource,
                                                                     MybatisPlusInterceptor interceptor) {
        MybatisSqlSessionFactoryBean sessionFactory = new MybatisSqlSessionFactoryBean();
        // 设置数据源bean
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setPlugins(interceptor);
        return sessionFactory;
    }
}
