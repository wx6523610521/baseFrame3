# BaseFrame3

基于 Spring Boot 3 + Spring Security 6 + JWT 的企业级快速开发基础框架。

## 项目简介

BaseFrame3 是一个 **Spring Boot 3.3.3** 多模块项目，集成 **Spring Security 6 + JWT** 无状态认证体系、**MyBatis-Plus** ORM、**Knife4j** 接口文档、**Quartz** 动态定时任务等功能，用于快速启动企业级 Java Web 项目开发。

- **仓库地址**: [Gitee](https://gitee.com/ChnCyL/basic-project.git)
- **当前版本**: 0.0.1-SNAPSHOT

## 技术栈

| 类别 | 技术 | 版本 |
|------|------|------|
| 基础框架 | Spring Boot | 3.3.3 |
| JDK | Java | 17 |
| 安全认证 | Spring Security 6 + JWT (jjwt) | 0.12.5 |
| ORM | MyBatis-Plus | 3.5.7 |
| 数据库 | MySQL + HikariCP 连接池 | - |
| 缓存 | Redis (Lettuce 连接池) | - |
| 接口文档 | Knife4j (OpenAPI 3) | 4.5.0 |
| 定时任务 | Quartz Scheduler | - |
| JSON | FastJSON2 | 2.0.52 |
| 工具库 | Commons Lang3, Commons Pool2 | 3.15.0 / 2.12.0 |
| 代码简化 | Lombok | - |
| 验证码 | Easy Captcha | 1.6.2 |
| 密码加密 | BCrypt | - |
| PDF | iText + iText Asian | 5.5.13.3 |
| Office | Apache POI | 4.1.2 |
| Markdown | Flexmark | 0.62.2 |
| HTML解析 | Jsoup | 1.14.3 |

## 项目结构

```
basicProject
├── base/                          # 基础核心模块
│   └── src/main/java/work/chncyl/base/
│       ├── global/
│       │   ├── config/            # 配置（Redis、Swagger/Knife4j）
│       │   ├── constant/          # 常量定义
│       │   ├── enums/             # 枚举
│       │   ├── exception/         # 全局异常处理
│       │   ├── handler/           # 处理器（过滤器异常等）
│       │   ├── task/              # 动态定时任务（controller/service/mapper）
│       │   └── tools/             # 工具类
│       │       ├── cache/         # 本地缓存（限时/限次/高级缓存）
│       │       ├── classTool/     # 类工具
│       │       ├── file/          # 文件处理（PDF/资源文件）
│       │       ├── requestTool/   # 请求封装（可重复读取Body）
│       │       └── result/        # 统一响应 ApiResult
│       └── security/              # 安全模块
│           ├── annotation/        # @AnonymousAccess 匿名访问注解
│           ├── config/            # SpringSecurity 配置、弱密码检测
│           ├── entity/            # 登录用户实体
│           ├── filter/            # JWT过滤器、验证码过滤器、自定义登录过滤器
│           ├── mapper/            # 用户详情 Mapper
│           ├── processor/         # 认证提供器
│           └── utils/             # JWT工具、密码校验工具
├── main/                          # 启动入口模块
│   └── src/main/java/work/chncyl/main/
│       ├── MainApplication.java   # Spring Boot 启动类
│       ├── authentication/        # 认证控制器
│       └── flow/                  # 流程（预留）
│   └── src/main/resources/
│       ├── application.yml        # 主配置
│       ├── application-dev.yml    # 开发环境配置
│       └── application-prod.yml   # 生产环境配置
├── service/                       # 业务服务模块（依赖base，业务代码在此编写）
│   └── src/main/java/work/chncyl/service/
├── pom.xml                        # 父 POM（依赖管理 + Maven Profile）
└── .gitignore
```

### 模块依赖关系

```
main ──> base
service ──> base
```

- **base**: 核心基础模块，提供鉴权、工具类、异常处理、动态定时任务等公共能力，不包含启动类（`spring-boot-maven-plugin` 配置 `skip: true`）
- **main**: 启动入口，包含 `MainApplication` 启动类和登录认证控制器，依赖 `base`
- **service**: 业务模块骨架，业务代码应在 `service` 模块中编写，同样依赖 `base`

## 核心功能

### 1. 安全认证

- **无状态 JWT 认证**：基于 HS256 签名，Token 存储在 Redis，支持过期时间配置
- **过滤器链**：`VerifyCodeFilter` -> `CustomRequestFilter` -> `JwtAuthenticationFilter` -> `CustomUsernamePasswordAuthenticationFilter`
- **@AnonymousAccess 注解**：标记控制器或方法为匿名可访问，框架启动时自动扫描注册
- **弱密码检测**：可配置开启，登录时校验密码强度
- **BCrypt 密码加密**
- **验证码支持**：登录可启用图形验证码（Easy Captcha）

### 2. 动态定时任务

基于 Quartz + 数据库持久化的动态定时任务管理：

- `SpringScheduledTask` — 任务实体（持久化到数据库）
- `DynamicTaskManager` — 动态添加/移除任务
- `MySchedulingConfigurer` — 自定义调度配置
- 提供 CRUD 接口管理定时任务

### 3. 统一响应

`ApiResult<T>` 统一封装所有接口返回：

```json
{
  "status": true,
  "message": "success",
  "code": 200,
  "data": {},
  "timestamp": 1715600000000,
  "detail": null
}
```

内置快捷方法：`ApiResult.OK(data)`、`ApiResult.error401(msg)`、`ApiResult.error404(msg)` 等。

### 4. API 文档（Knife4j）

- 自动生成 OpenAPI 3 文档
- 访问路径：`/doc.html`（Knife4j UI）或 `/swagger-ui.html`
- 支持分组配置
- 可通过 `swagger.enabled` 控制是否启用
- 可通过 `knife4j.basic` 启用登录认证保护文档

### 5. 工具类

`base/global/tools/` 提供丰富的工具支持：

- **RedisUtils**: Redis 操作封装
- **MailUtils**: 邮件发送
- **HashCrypto**: 哈希加密
- **RegexUtils**: 正则校验
- **SpringUtils**: Spring 上下文获取 Bean
- **SessionUtils**: Session 工具
- **AuthenticateUtils**: 认证工具
- **Cache 系列**: TimeLimitCache（限时缓存）、UsageLimitCache（限次缓存）、AdvancedUsageLimitCache（高级缓存）

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

### 1. 克隆项目

```bash
git clone https://gitee.com/ChnCyL/basic-project.git
cd basic-project
```

### 2. 配置数据库

修改 `main/src/main/resources/application-dev.yml`：

```yaml
spring:
  datasource:
    username: root
    password: your_password
    url: jdbc:mysql://localhost:3306/chncylDb?characterEncoding=UTF-8&useUnicode=true&useSSL=false&serverTimezone=Asia/Shanghai
  data:
    redis:
      host: your_redis_host
      port: 6379
      password: your_redis_password
```

### 3. 配置 JWT 密钥

在 `application-dev.yml` 中修改：

```yaml
security:
  secret-key-256: your-256-bit-secret-key  # 至少32字节
  expire-seconds: 3600                      # Token 过期时间（秒）
```

### 4. 启动项目

```bash
# 在项目根目录执行
mvn clean install -P dev
cd main
mvn spring-boot:run
```

或者直接运行 `main` 模块下的 `MainApplication` 启动类。

### 5. 访问接口文档

启动成功后，控制台会打印接口文档地址：

```
本地:   http://localhost:8080/doc.html
外部:   http://<your-ip>:8080/doc.html
```

## 配置说明

### Maven 环境切换

父 POM 内置 `dev` 和 `prod` 两个 Profile，默认激活 `dev`：

```bash
# 开发环境（默认）
mvn clean package

# 生产环境
mvn clean package -P prod
```

### 主要配置项（application-dev.yml）

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `spring.datasource.*` | 数据库连接配置 | - |
| `spring.data.redis.*` | Redis 连接配置 | - |
| `spring.mail.*` | 邮件服务配置 | - |
| `security.secret-key-256` | JWT 签名密钥（至少32字节） | 内置默认值 |
| `security.expire-seconds` | Token 过期时间（秒） | 3600 |
| `security.can-redeployed` | 是否允许 Token 挪用 | true |
| `swagger.enabled` | 是否启用接口文档 | true |
| `weak-password-check.enable` | 是否启用弱密码检测 | false |
| `knife4j.basic.enable` | 文档页是否启用登录认证 | false |
| `mybatis-plus.configuration.log-impl` | SQL 日志输出 | StdOutImpl |

### 逻辑删除

MyBatis-Plus 已全局配置逻辑删除字段 `isDeleted`：
- 删除值: `1`
- 未删除值: `0`

## 业务开发指南

业务代码请在 **service 模块** 中编写，遵循以下分层结构：

```
service/src/main/java/work/chncyl/service/
├── controller/    # 控制器
├── service/       # 服务接口与实现
├── mapper/        # MyBatis Mapper
├── entity/        # 实体类
└── dto/           # 数据传输对象
```

### 匿名接口标记

如需某个接口无需认证即可访问，使用 `@AnonymousAccess` 注解：

```java
@AnonymousAccess
@GetMapping("/public/data")
public ApiResult<?> getPublicData() {
    return ApiResult.OK(data);
}
```

## 部署

### Jar 包部署

```bash
mvn clean package -P prod
java -jar main/target/main-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### Docker 部署（参考）

```dockerfile
FROM openjdk:17-jdk-slim
COPY main/target/main-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar", "--spring.profiles.active=prod"]
```

## License

本项目基于 [LICENSE](./LICENSE) 文件进行许可。
