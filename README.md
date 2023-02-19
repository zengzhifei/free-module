# 一个自由配置各种模块的Spring starter。

## 引入maven依赖

```xml

<dependency>
    <groupId>com.stoicfree</groupId>
    <artifactId>free-module-starter</artifactId>
    <version>lastest.version</version>
</dependency>
```

## 常用注解

```
// 接口防重调用, 依赖redis
@RepeatLimit 

// aop日志拦截忽略接口
@AspectIgnore

// gson序列化忽略字段
@GsonIgnore

// es查询规则，依赖ES
@EsQuery

// es ID
@EsID

// mvc auth
@Auth

// mvc login
@Login(username = "#username", password = "#password")
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
    security:
      enable: true
      token-key: fr_token
      expires: 7d
      exclude-paths: /test/register,/test/login
```

```java

@Configuration
public class SecurityConfig {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MvcProperties mvcProperties;

    @Bean
    public SecurityUserService<User> securityUserService() {
        UserColumn<User> userColumn = UserColumn.<User>builder()
                .username(User::getUsername)
                .password(User::getPassword)
                .uuid(User::getUuid)
                .enable(User::getEnable)
                .roles(User::getRoles)
                .build();
        return new SecurityUserService<>(userMapper, userColumn, mvcProperties.getSecurity());
    }
}
```

```java

@Slf4j
@RestController
@RequestMapping("/test")
@Auth(roles = "admin", excludeRoles = "user")
public class TestController {
    @Autowired
    private SecurityUserService<User> securityUserService;

    @PostMapping("/register")
    public Result<Boolean> register(User user) {
        user.setEnable(true);
        securityUserService.register(user);
        return Result.ok(true);
    }

    @PostMapping("/login")
    public Result<Boolean> login(String username, String password, HttpServletRequest request,
                                 HttpServletResponse response) {
        securityUserService.login(username, password, request, response);
        return Result.ok(true);
    }

    @PostMapping("/login2")
    @Login(username = "#username", password = "#password")
    public Result<Boolean> login(String username, String password) {
        System.out.println("login2");
        return Result.ok(true);
    }

    @PostMapping("/changePassword")
    public Result<Boolean> changePassword(String username, String oldPassword, String newPassword) {
        securityUserService.changePassword(username, oldPassword, newPassword);
        return Result.ok(true);
    }

    @PostMapping("/updateRoles")
    public Result<Boolean> updateRoles(String username, String[] roles) {
        securityUserService.updateRoles(username, Arrays.stream(roles).collect(Collectors.toSet()));
        return Result.ok(true);
    }

    @PostMapping("/updateEnable")
    public Result<Boolean> updateEnable(String username, boolean enable) {
        securityUserService.updateEnable(username, enable);
        return Result.ok(true);
    }
}
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

```java

@AllArgsConstructor
public enum RedisKeyEnum implements IRedisKeyEnum {
    DEMO("demo", 10);

    private final String key;
    private final Integer expires;

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public Integer getExpires() {
        return expires;
    }
}
```