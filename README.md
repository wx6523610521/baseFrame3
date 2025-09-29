# BaseFrame3

## 介绍

基于SpringBoot框架构建的基础项目框架，集成SpringSecurity+JWT安全认证机制，用于项目快速起步开发。

本项目采用模块化设计，便于扩展和维护，适用于企业级应用开发。

> 本项目托管于 [Gitee](https://gitee.com/ChnCyL/base-frame2_security.git)
> [Github](https://github.com/wx6523610521/BaseFrame_security.git)

## 技术栈

- 核心框架：Spring Boot 2.7.18
- 安全框架：Spring Security 5.x
- 认证方式：JWT (JSON Web Tokens)
- 持久层框架：MyBatis Plus 3.5.14
- 数据库：MySQL (mysql-connector-j)
- 缓存：Redis
- 接口文档：Knife4j (Swagger增强版)
- 工具类：Fastjson2、Apache Commons系列
- 构建工具：Maven

## 项目结构

```
basicProject
├── base          # 基础模块，包含通用工具类、安全配置、全局异常处理等
├── main          # 主启动模块，负责整合各业务模块并启动应用
└── service       # 业务逻辑模块(待开发)
```

### 模块说明

#### base模块
基础核心模块，包含：
- 全局异常处理
- 统一返回结果封装
- Redis配置及工具类
- Spring Security安全配置
- JWT工具类
- 请求包装器(支持多次读取请求体)
- 工具类集合(邮件、加密、正则表达式等)
- API文档配置(Knife4j)

#### main模块
主启动模块，负责：
- 应用启动入口
- 整合其他模块
- 应用基本配置

## 功能特性

- [x] 基于JWT的安全认证与授权机制
- [x] 用户密码BCrypt加密存储
- [x] RESTful API风格设计
- [x] 统一响应结果封装
- [x] 全局异常处理机制
- [x] Redis缓存集成
- [x] 邮件服务支持
- [x] 定时任务支持(Quartz)
- [x] WebSocket支持
- [x] 接口文档自动生成(Knife4j)
- [x] 验证码校验功能
- [x] 多环境配置(dev/prod)

## 快速开始

### 环境要求

- JDK 1.8+
- Maven 3.6+
- MySQL 5.7+
- Redis

### 配置说明

1. 导入项目到IDE(推荐IntelliJ IDEA)
2. 修改数据库配置文件 `main/src/main/resources/application-dev.yml`
3. 修改Redis配置(如需要)
4. 执行数据库脚本(如有)

### 启动项目

```bash
# 进入项目根目录
cd basicProject

# Maven清理并编译
mvn clean install

# 运行主模块
mvn spring-boot:run -pl main
```

或者直接运行 `MainApplication.java`

### 打包和运行方式

项目支持多种打包方式以满足不同的部署需求：

#### 1. 打包普通jar+依赖

使用以下命令生成普通jar包和分离的依赖jar包：

```bash
mvn clean package -DskipTests -P separate-jar
```

此命令会生成：
- `main/target/BaseFrameSecurity.jar` - 普通jar包（不包含依赖）
- `main/target/libs` 目录 - 包含所有依赖的jar包

运行方式(target目录下)：
```bash
java -cp "BaseFrameSecurity.jar;libs/*" work.chncyl.main.MainApplication
```

#### 2. 打包两种格式

使用以下命令同时生成两种格式的jar包：

```bash
mvn clean package -DskipTests -P both-jar
```

此命令默认会同时生成：
- `main/target/BaseFrameSecurity-exec.jar` - 可直接运行的jar包（包含所有依赖）
- `main/target/BaseFrameSecurity.jar` - 普通jar包（不包含依赖）
- `main/target/libs` 目录 - 包含所有依赖的jar包


### 访问接口文档

项目启动后，可通过以下地址访问接口文档：

```
http://localhost:7685/doc.html
```

## 配置项说明

| 配置项 | 默认值 | 说明 |
| ------ | ------ | ---- |
| server.port | 7685 | 应用端口号 |
| spring.profiles.active | dev | 默认激活的配置环境 |
| spring.datasource.* | - | 数据库连接配置 |
| spring.redis.* | - | Redis连接配置 |

## 安全机制

系统采用Spring Security结合JWT实现安全认证：

1. 用户登录成功后颁发JWT Token
2. 后续请求需在Header中携带Token
3. 系统通过Filter拦截并验证Token有效性
4. 支持匿名访问接口配置(@AnonymousAccess注解)

## 开发规范

1. 使用统一的API返回格式`ApiResult`
2. 异常处理遵循全局异常处理机制
3. 接口文档使用Swagger注解进行描述
4. 数据库操作使用MyBatis Plus

## 扩展说明

项目预留了service模块用于业务开发，开发者可以在此基础上：

1. 创建新的业务模块
2. 添加自己的业务逻辑
3. 集成第三方服务
4. 扩展安全认证机制

## 贡献者

- ChnCyL

## License

Apache License 2.0