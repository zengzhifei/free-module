package com.stoicfree.free.module.core.jdbc.config;

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
    public HikariDataSource buildHikariDataSource() {
        HikariDataSource dataSource = DataSourceBuilder.create().type(HikariDataSource.class).build();
        dataSource.setConnectionInitSql("set names utf8mb4");
        dataSource.setAutoCommit(true);
        dataSource.setConnectionTimeout(30000);
        dataSource.setMinimumIdle(20);
        dataSource.setIdleTimeout(180000);
        dataSource.setConnectionTestQuery("SELECT 1");
        dataSource.setMaximumPoolSize(300);
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setMaxLifetime(300000);
        return dataSource;
    }

    /**
     * 创建Mybatis的连接会话工厂实例
     */
    public MybatisSqlSessionFactoryBean buildMybatisSqlSessionFactoryBean(DataSource dataSource,
                                                                          MybatisPlusInterceptor interceptor) {
        MybatisSqlSessionFactoryBean sessionFactory = new MybatisSqlSessionFactoryBean();
        // 设置数据源bean
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setPlugins(interceptor);
        return sessionFactory;
    }
}
