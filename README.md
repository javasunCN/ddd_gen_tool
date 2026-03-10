# 技术框架生成工具

## 项目概述

本项目是一个基于SpringBoot的领域驱动微服务技术框架生成工具，旨在帮助开发者快速搭建符合领域驱动设计（DDD）思想的微服务架构。通过本工具，开发者可以快速生成一个结构清晰、职责明确的SpringBoot微服务项目，包含完整的领域驱动设计实现。

## 技术栈

| 类别 | 技术 | 版本 |
|------|------|------|
| 后端 | Java | 21+ |
| 框架 | Spring Boot | 4.x+ |
| ORM | MyBatis | 3.5.x+ |
| 数据库 | MySQL | 8.x+ |
| 工具 | Lombok | 1.18+ |
| 工具 | Hutool | 7.0.x |
| 模板引擎 | StringTemplate4 | - |
| 构建工具 | Maven | 3.x |
| 前端 | Bootstrap | 5.3.3 |
| 前端 | jQuery | 4.0.0 |

## 项目结构

```
/gen_tool
├── src/
│   ├── main/
│   │   ├── java/com/gen/
│   │   │   ├── config/          # 配置相关
│   │   │   ├── generate/        # 生成器核心逻辑
│   │   │   │   ├── build/       # 构建模块
│   │   │   │   ├── commom/      # 通用工具
│   │   │   │   ├── controller/  # 生成器接口
│   │   │   │   └── dto/         # 数据传输对象
│   │   │   ├── st4/             # StringTemplate4相关
│   │   │   ├── ui/              # 前端界面
│   │   │   └── GenToolApplication.java  # 应用启动类
│   │   └── resources/
│   │       ├── static/          # 静态资源
│   │       ├── templates/       # 模板文件
│   │       │   ├── st4/         # StringTemplate4模板
│   │       │   └── home.html    # 前端首页
│   │       └── application.yaml # 应用配置
│   └── test/                    # 测试代码
├── .gitignore
├── pom.xml
└── README.md
```

## 核心功能

1. **领域驱动设计实现**：生成符合DDD思想的项目结构，包含聚合根、实体、值对象、领域服务、领域事件等核心概念
2. **分层架构**：生成清晰的分层架构，包括北向网关层、领域层、南向网关层和基础设施层
3. **设计模式**：集成策略模式、工厂模式等常用设计模式
4. **事件驱动**：实现领域事件机制，支持事件发布和订阅
5. **模块化设计**：生成模块化的项目结构，便于维护和扩展
6. **代码生成**：基于StringTemplate4模板引擎，快速生成代码
7. **可视化界面**：提供简单的Web界面，方便用户操作

## 生成的项目结构

生成的项目包含以下模块：

| 模块名称 | 职责 | 主要组件 |
|---------|------|----------|
| application | 应用配置和启动 | Application.java, application.yaml |
| common | 通用组件 | 注解、工具类、异常处理 |
| domain | 核心业务逻辑 | 聚合根、实体、值对象、领域服务、领域事件、工厂、仓库接口 |
| dto | 数据传输对象 | 请求/响应对象 |
| north_gateway | 北向网关 | 应用服务、控制器、策略模式 |
| south_gateway | 南向网关 | 仓库实现、外部服务实现、基础设施 |
| doc | 文档 | 架构设计文档 |

## 使用方法

### 1. 启动应用

```bash
# 编译项目
mvn clean install

# 运行应用
mvn spring-boot:run
```

应用启动后，访问 `http://localhost:8088/` 进入Web界面。

### 2. 生成项目

在Web界面中，填写以下信息：
- 项目名称
- 项目路径
- 包名
- 模块名称

点击"生成"按钮，系统会在指定路径生成完整的项目结构。


## 生成的项目特点

### 架构设计

- **领域驱动设计（DDD）**：以领域模型为核心，将业务逻辑与技术实现分离
- **分层架构**：清晰的职责边界，便于维护和扩展
- **事件驱动**：通过领域事件实现松耦合的系统集成
- **依赖倒置**：通过接口分离实现依赖关系的解耦
- **策略模式**：通过策略模式处理不同类型的业务逻辑
- **工厂模式**：通过工厂模式创建复杂的领域对象

### 核心领域模型

- **聚合根**：OrderAggregate，负责订单的完整生命周期管理
- **实体**：OrderEntity、OrderProductEntity等
- **值对象**：Address、Money、OrderCode、ProductId等
- **领域服务**：OrderDomainService、OrderValidateService等
- **领域事件**：OrderCreatedEvent、OrderPaidEvent、OrderDeliveredEvent等
- **工厂**：OrderFactory、GeneralOrderFactory、VipOrderFactory等
- **仓库**：OrderRepository、OrderRepositoryImpl等

### 应用层设计

- **应用服务**：OrderApplicationService，协调领域对象完成业务操作
- **策略模式**：OrderStrategy、OrderStrategyFactory等
- **控制器**：OrderController，处理HTTP请求

### 基础设施层设计

- **外部服务实现**：CustomerServiceImpl、InventoryServiceImpl、PaymentServiceImpl等
- **数据访问**：OrderMapper、Order等

## 部署和运行

### 环境要求

- **JDK**：21+
- **MySQL**：8.x+
- **Maven**：3.6+

### 部署方案

- **开发环境**：本地开发，使用application-dev.yaml配置
- **测试环境**：测试服务器，使用application-test.yaml配置
- **生产环境**：生产服务器，使用application-prod.yaml配置

## 扩展和定制

### 模板定制

可以通过修改 `src/main/resources/templates/st4/` 目录下的模板文件，定制生成的代码结构和内容。

### 功能扩展

可以通过扩展以下类来增加新的功能：

- `BuilderModule`：添加新的模块
- `BuilderTemplateUtil`：修改模板处理逻辑
- `GenerateController`：添加新的API接口

## 监控与日志

- **日志框架**：SLF4J + Logback
- **监控指标**：订单创建成功率、支付成功率、系统响应时间等
- **告警机制**：异常告警、性能告警等

## 安全设计

- **认证授权**：基于SA-token的认证授权机制
- **数据加密**：敏感数据加密存储
- **接口防护**：防止SQL注入、XSS攻击等
- **审计日志**：记录关键操作日志

## 总结

本技术框架生成工具采用领域驱动设计的思想，帮助开发者快速搭建结构清晰、职责明确的SpringBoot微服务项目。通过本工具生成的项目，具有以下特点：

1. **结构清晰**：分层架构，职责明确
2. **可维护性高**：模块化设计，便于维护和扩展
3. **可扩展性强**：通过设计模式和接口分离，支持功能扩展
4. **业务表达清晰**：通过领域模型，清晰表达业务逻辑
5. **松耦合**：通过事件驱动和依赖倒置，实现系统组件的松耦合

未来，本工具将继续完善，支持更多的领域模型和设计模式，为开发者提供更加便捷的微服务架构生成能力。