# 一个自由配置各种模块的Spring starter。

## 引入maven依赖

```xml

<dependency>
    <groupId>com.stoicfree</groupId>
    <artifactId>free-module-starter</artifactId>
    <version>lastest.version</version>
</dependency>
```

## common模块通用功能

#### aop日志拦截

```java

@Aspect
@Component
public class ProviderLogAdvice extends AbstractInterfaceLogAdvice {
    @Override
    @Around("execution(* com.demo.biz.aop..*(..)) "
            + "&& !@annotation(com.stoicfree.free.common.module.aop.AspectIgnore)")
    public Object advice(ProceedingJoinPoint proceedingJoinPoint) {
        super.setExceptionConsumer(e -> {
            System.out.println("InterfaceLogAdvice error:" + e.getMessage());
        });
        return super.around(proceedingJoinPoint);
    }
}
```

#### eventbus发布订阅

```java

@Slf4j
@Service
public class EventBusServiceImpl extends AbstractEventBus implements EventBusService {
    @Override
    public <T> void subscribe(EventBusMessage<T> eventBusMessage) {
        log.info(GsonUtil.toJson(eventBusMessage));
    }

    @Override
    public void test() {

    }
}
```

#### exception handler 全局异常捕获

```java

@RestControllerAdvice
public class BizExceptionHandler extends AbstractExceptionHandler {
}
```

#### mybatis-plug generator 代码生成器

```java
public class Generator {
    public static void main(String[] args) {
        AutoGenerator.generate(AutoGenerator.GenerateConf.builder()
                .url("jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=UTF-8"
                        + "&zeroDateTimeBehavior=convertToNull&noAccessToProcedureBodies=true&autoReconnect=true"
                        + "&allowMultiQueries=true")
                .username("123")
                .password("123")
                .tablePrefix(new String[] {"abc_"})
                .parentPackage("com.demo.orm")
                .build());
    }
}
```

#### log4j2 统一日志配置

```java

@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        System.setProperty("log4j.configurationFile", "clog/log4j2.xml");
        SpringApplication.run(DemoApplication.class, args);
    }
}
```

## mvc 模块

```yaml
free:
  mvc:
    enable: true
```

## db 模块

```yaml
free:
  db:
    enable: true
    entity-package: com.demo.orm.entity

db:
  demo:
    jdbc-url: jdbc:mysql://localhost:3306/test?
    useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&noAccessToProcedureBodies=true&autoReconnect=true&allowMultiQueries=true
    username: 123
    password: 123
```

```java

@Configuration
@MapperScan(
        basePackages = "com.demo.orm.mapper", sqlSessionFactoryRef = "demoDataSqlSessionFactory"
)
@Slf4j
public class DataSourceConfig extends AbstractDataSourceConfig {
    @Autowired
    private MybatisPlusInterceptor mybatisPlusInterceptor;

    /**
     * 指定为数据源
     *
     * @return 数据源
     */
    @Bean(name = "demoDataSource")
    @ConfigurationProperties(prefix = "db.demo")
    public HikariDataSource dataSource() {
        return super.buildHikariDataSource();
    }

    /**
     * 创建Mybatis的连接会话工厂实例
     */
    @Bean(name = "demoDataSqlSessionFactory")
    public MybatisSqlSessionFactoryBean mybatisSqlSessionFactoryBean(
            @Qualifier("demoDataSource") DataSource dataSource) {
        return super.buildMybatisSqlSessionFactoryBean(dataSource, mybatisPlusInterceptor);
    }
}
```

## es 模块

```yaml
free:
  es:
    enable: true

es:
  demo:
    host: 127.0.0.1
    port: 8200
    user: user
    password: 123
    index: demo_index
    retries: 3
```

```java

@Configuration
public class EsConfig extends AbstractEsConfig {
    @Bean
    @ConfigurationProperties(prefix = "es.demo")
    public EsClientConfig demoEsClientConfig() {
        return new EsClientConfig();
    }

    @Bean
    public EsService<Goods, GoodsQuery> goodsEsService(@Qualifier("demoEsClientConfig") EsClientConfig config) {
        RestHighLevelClient client = super.buildRestHighLevelClient(config);
        return new EsService<>(client, config, Goods.class);
    }
}
```

## redis 模块

```yaml
free:
  redis:
    enable: true
    log: true

redis:
  demo:
    host: 127.0.0.1
    port: 6379
    password: 123
    jedis-pool:
      max-idle: 5
      min-idle: 3
```

```java

@Configuration
public class RedisConfig extends AbstractRedisConfig {
    @Bean
    @ConfigurationProperties(prefix = "redis.demo")
    public RedisClientConfig redisClientConfig() {
        return new RedisClientConfig();
    }

    @Bean
    public RedisClientFactory redisClientFactory(RedisClientConfig redisClientConfig) {
        JedisPool jedisPool = super.buildJedisPool(redisClientConfig);
        return super.buildRedisClientFactory(jedisPool);
    }
}
```