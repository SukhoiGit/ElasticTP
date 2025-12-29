<div align="center">
  <img src=".idea/icon.png" alt="ElasticTP Logo" width="120" height="120">
  
  <h1>🚀 ElasticTP</h1>
  
  <p><strong>动态线程池管理框架</strong></p>
  
  <p>
    <a href="https://github.com/SukhoiGit/ElasticTP">
      <img src="https://img.shields.io/badge/ElasticTP-v1.0-blue.svg" alt="ElasticTP Version">
    </a>
    <a href="https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html">
      <img src="https://img.shields.io/badge/JDK-17+-green.svg" alt="JDK Version">
    </a>
    <a href="https://spring.io/projects/spring-boot">
      <img src="https://img.shields.io/badge/Spring%20Boot-3.0.7-brightgreen.svg" alt="Spring Boot">
    </a>
    <a href="https://github.com/SukhoiGit/ElasticTP/blob/main/LICENSE">
      <img src="https://img.shields.io/badge/license-Apache%202-4EB1BA.svg" alt="License">
    </a>
  </p>
</div>

<p align="center">
  <b>🎉 一个轻量级、高性能的动态线程池管理框架</b><br>
  支持线程池参数动态调整、实时监控、智能告警与多平台通知
</p>

<div align="center">

[English](README_EN.md) | 简体中文

</div>

---

## 📖 项目简介

ElasticTP（Elastic ThreadPool）是一个基于配置中心的动态线程池管理框架，旨在帮助开发者更好地管理和使用线程池资源。它提供了线程池参数的动态调整、运行时监控、异常告警等增强功能，让线程池的使用更加灵活、可控和安全。

### 💡 项目起源

在阅读美团技术团队的文章[《Java线程池实现原理及其在美团业务中的实践》](https://tech.meituan.com/2020/04/02/java-pooling-pratice-in-meituan.html)后，深刻认识到在实际工作中线程池的使用场景非常广泛。然而，JDK 原生线程池存在以下痛点：

- ⚠️ **参数配置困难** - JDK 原生线程池参数难以一次性配置到位
- 📉 **缺乏监控手段** - 缺乏有效的监控手段来了解线程池的运行状态和资源消耗情况
- 🔧 **调整成本高** - 线程池参数调整需要重启应用，影响业务连续性
- 🚨 **告警机制缺失** - 无法及时发现线程池异常情况

**案例**：

**Case1**：2018年XX页面展示接口大量调用降级：

**事故描述**：XX页面展示接口产生大量调用降级，数量级在几十到上百。

**事故原因**：该服务展示接口内部逻辑使用线程池做并行计算，由于没有预估好调用的流量，导致最大核心数设置偏小，大量抛出RejectedExecutionException，触发接口降级条件，示意图如下：

![](https://oss.open8gu.com/1df932840b31f41931bb69e16be2932844240.png)



**Case2**：2018年XX业务服务不可用S2级故障

**事故描述**：XX业务提供的服务执行时间过长，作为上游服务整体超时，大量下游服务调用失败。

**事故原因**：该服务处理请求内部逻辑使用线程池做资源隔离，由于队列设置过长，最大线程数设置失效，导致请求数量增加时，大量任务堆积在队列中，任务执行时间过长，最终导致下游服务的大量调用超时失败。示意图如下：

![](https://oss.open8gu.com/668e3c90f4b918bfcead2f4280091e9757284.png)

为解决上述问题，设计并实现了这个基于配置中心构建的动态线程池框架。ElasticTP 支持线程池核心参数的在线动态调整、运行时状态监控与阈值告警，兼容主流配置中心如 Nacos/Apollo，实现线程池参数热更新与统一管理。

**特别感谢美团技术团队的这篇优秀文章，为本项目提供了宝贵的思路和实践指导！** 🙏

---

## ✨ 核心特性

### 🎯 动态参数调整
- ✅ 支持核心线程数（corePoolSize）动态调整
- ✅ 支持最大线程数（maximumPoolSize）动态调整
- ✅ 支持队列容量（queueCapacity）动态调整
- ✅ 支持线程存活时间（keepAliveTime）动态调整
- ✅ 支持拒绝策略（rejectedExecutionHandler）动态切换
- ✅ 参数变更实时生效，无需重启应用

### 📊 运行时监控
- 📈 实时监控线程池核心指标
  - 当前活跃线程数
  - 历史最大线程数
  - 队列当前任务数
  - 队列剩余容量
  - 已完成任务总数
  - 拒绝任务总数
- 🔍 支持自定义监控指标扩展
- 📉 提供监控数据采集接口

### 🚨 智能告警
- ⚠️ 队列容量告警（支持自定义阈值）
- ⚠️ 活跃线程数告警（支持自定义阈值）
- ⚠️ 拒绝任务告警
- 📢 支持钉钉机器人通知
- 🔔 支持自定义告警通知渠道
- ⏰ 内置告警频率限制，避免告警轰炸

### 🔌 配置中心集成
- ☁️ 完美集成 Nacos（支持 Nacos Cloud）
- 🌙 完美集成 Apollo
- 🔄 配置变更自动刷新
- 🎨 支持 YAML、Properties 多种配置格式
- 🔧 提供统一的配置管理接口

### 🎨 易用性
- 🏷️ 简洁的注解式编程：`@DynamicThreadPool`
- 🚦 一键启用：`@EnableElasticTp`
- 📦 开箱即用的 Spring Boot Starter
- 📝 详细的示例项目
- 🎯 零侵入式设计，与业务代码解耦

---

## 🏗️ 架构设计

### 模块结构

```
ElasticTP
├── core                                    # 核心模块
│   ├── executor                           # 线程池执行器
│   ├── monitor                            # 监控模块
│   ├── alarm                              # 告警模块
│   ├── notification                       # 通知服务
│   └── parser                             # 配置解析器
├── spring-base                            # Spring 基础集成
│   ├── enable                             # 启用注解
│   └── support                            # Spring 支持类
├── starter                                # Spring Boot Starter
│   ├── common-spring-boot-starter        # 通用自动配置
│   ├── apollo-spring-boot-starter        # Apollo 集成
│   └── nacos-cloud-spring-boot-starter   # Nacos 集成
└── example                                # 示例项目
    ├── apollo-example                     # Apollo 示例
    └── nacos-cloud-example                # Nacos 示例
```

### 核心组件

- **ElasticTpExecutor**：增强的线程池执行器，支持动态参数调整和监控
- **ElasticTpRegistry**：线程池注册中心，统一管理所有动态线程池
- **ThreadPoolMonitor**：线程池监控器，定时采集运行时指标
- **ThreadPoolAlarmChecker**：告警检查器，根据阈值触发告警
- **NotifierDispatcher**：通知分发器，支持多种通知渠道
- **AbstractDynamicThreadPoolRefresher**：配置刷新抽象类，支持多种配置中心

---

## 🚀 快速开始

### 环境要求

- JDK 17+
- Spring Boot 3.0.7+
- Maven 3.6+

### 1️⃣ 添加依赖

#### 使用 Nacos 配置中心

```xml
<dependency>
    <groupId>com.artemis</groupId>
    <artifactId>nacos-cloud-spring-boot-starter</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

#### 使用 Apollo 配置中心

```xml
<dependency>
    <groupId>com.artemis</groupId>
    <artifactId>apollo-spring-boot-starter</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### 2️⃣ 启用 ElasticTP

在 Spring Boot 启动类上添加 `@EnableElasticTp` 注解：

```java
@SpringBootApplication
@EnableElasticTp
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

### 3️⃣ 配置线程池

在配置中心创建线程池配置（以 Nacos YAML 格式为例）：

```yaml
elastic-tp:
  # 是否启用 ElasticTP
  enabled: true
  # 全局告警配置
  alarm:
    alarm-types:
      - CAPACITY    # 容量告警
      - REJECT      # 拒绝告警
      - ACTIVE_SIZE # 活跃度告警
    interval: 5     # 告警间隔（分钟）
  
  # 通知配置
  notifiers:
    - platform: DING_TALK
      token: your-dingtalk-webhook-token
      secret: your-dingtalk-secret
  
  # 线程池配置
  executors:
    - thread-pool-id: order-pool          # 线程池唯一标识
      core-pool-size: 10                   # 核心线程数
      maximum-pool-size: 20                # 最大线程数
      queue-capacity: 500                  # 队列容量
      queue-type: ResizableCapacityLinkedBlockingQueue  # 队列类型
      keep-alive-time: 60                  # 线程存活时间（秒）
      rejected-handler: CallerRunsPolicy   # 拒绝策略
      thread-name-prefix: order-thread-    # 线程名前缀
      allow-core-thread-timeout: false     # 是否允许核心线程超时
      
      # 告警阈值配置
      alarm:
        capacity-threshold: 80             # 容量告警阈值（%）
        active-size-threshold: 80          # 活跃度告警阈值（%）
    
    - thread-pool-id: payment-pool
      core-pool-size: 8
      maximum-pool-size: 16
      queue-capacity: 300
      queue-type: LinkedBlockingQueue
      keep-alive-time: 60
      rejected-handler: AbortPolicy
      thread-name-prefix: payment-thread-
```

### 4️⃣ 创建动态线程池

#### 方式一：使用 `@DynamicThreadPool` 注解（推荐）

```java
@Configuration
public class ThreadPoolConfig {
    
    @Bean("orderPool")
    @DynamicThreadPool
    public ThreadPoolExecutor orderThreadPool() {
        // 此处返回的线程池会被自动托管和增强
        // 参数将从配置中心读取（通过 thread-pool-id 匹配）
        return null;
    }
    
    @Bean("paymentPool")
    @DynamicThreadPool
    public ThreadPoolExecutor paymentThreadPool() {
        return null;
    }
}
```

#### 方式二：手动创建

```java
@Configuration
public class ThreadPoolConfig {
    
    @Bean
    public ThreadPoolExecutor orderThreadPool() {
        // 使用配置的线程池 ID 从注册中心获取
        return ElasticTpRegistry.getExecutor("order-pool");
    }
}
```

### 5️⃣ 使用线程池

```java
@Service
public class OrderService {
    
    @Resource
    private ThreadPoolExecutor orderPool;
    
    public void processOrder(Order order) {
        orderPool.execute(() -> {
            // 处理订单逻辑
            System.out.println("Processing order: " + order.getId());
        });
    }
}
```

---

## 📊 监控示例

ElasticTP 提供了丰富的监控指标，您可以通过以下方式查看：

```java
@RestController
@RequestMapping("/monitor")
public class MonitorController {
    
    @GetMapping("/thread-pool/{poolId}")
    public ThreadPoolMonitorDTO getMonitorData(@PathVariable String poolId) {
        return ThreadPoolMonitor.getMonitorData(poolId);
    }
}
```

监控数据示例：

```json
{
  "threadPoolId": "order-pool",
  "corePoolSize": 10,
  "maximumPoolSize": 20,
  "activeCount": 8,
  "largestPoolSize": 15,
  "taskCount": 5000,
  "completedTaskCount": 4950,
  "queueSize": 50,
  "queueCapacity": 500,
  "queueRemainingCapacity": 450,
  "rejectCount": 5,
  "timestamp": "2025-12-29T10:00:00"
}
```

---

## 🚨 告警配置

### 告警类型

ElasticTP 支持以下三种告警类型：

| 告警类型 | 说明 | 触发条件 |
|---------|------|---------|
| **CAPACITY** | 队列容量告警 | 队列使用率超过阈值 |
| **ACTIVE_SIZE** | 活跃线程告警 | 活跃线程数占比超过阈值 |
| **REJECT** | 拒绝任务告警 | 发生任务拒绝 |

### 钉钉告警配置

1. 创建钉钉群机器人，获取 webhook token 和 secret
2. 在配置中心配置通知信息
3. 告警消息将自动发送到钉钉群

告警消息示例：

```
【ElasticTP 告警】
线程池：order-pool
告警类型：队列容量告警
当前队列使用率：85%
队列容量：500
当前任务数：425
告警时间：2025-12-29 10:00:00
```

![](https://oss.open8gu.com/image-20250525171313388.png)

---

## 🔧 动态参数调整

只需在配置中心修改配置，无需重启应用，参数即可实时生效：

### 调整前
```yaml
- thread-pool-id: order-pool
  core-pool-size: 10
  maximum-pool-size: 20
  queue-capacity: 500
```

### 调整后
```yaml
- thread-pool-id: order-pool
  core-pool-size: 20        # ⬆️ 核心线程数增加
  maximum-pool-size: 40     # ⬆️ 最大线程数增加
  queue-capacity: 1000      # ⬆️ 队列容量增加
```

ElasticTP 会自动检测配置变更并应用新参数，同时发送变更通知。

![](https://oss.open8gu.com/image-20250525170844026.png)

---

## 📚 示例项目

本项目提供了完整的示例代码：

- **apollo-example**：演示如何集成 Apollo 配置中心
- **nacos-cloud-example**：演示如何集成 Nacos 配置中心

示例代码位于 `example` 目录下，可以直接运行体验。

---

## 🛠️ 技术栈

- **核心框架**：Spring Boot 3.0.7、Spring Cloud 2022.0.3
- **配置中心**：Nacos 2022.0.0.0-RC2、Apollo 2.1.0
- **工具类库**：Hutool 5.8.25、Fastjson2 2.0.53
- **构建工具**：Maven

---

## 🤝 贡献指南

欢迎贡献代码、提出问题或建议！

1. Fork 本仓库
2. 创建您的特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交您的更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启一个 Pull Request

---

## 📄 开源协议

本项目采用 [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.html) 开源协议。

---

## 📧 联系方式

- **作者**：Artemis
- **Email**：originblue062@gmail.com
- **GitHub**：https://github.com/SukhoiGit
- **Blog**: https://sukhoigit.github.io

---

## 🙏 致谢

### 特别感谢

本项目的灵感来源于 [美团技术团队](https://tech.meituan.com/) 的优秀文章[《Java线程池实现原理及其在美团业务中的实践》](https://tech.meituan.com/2020/04/02/java-pooling-pratice-in-meituan.html)。

该文章深入浅出地介绍了线程池的实现原理以及在实际业务场景中的应用实践，为本项目的设计和实现提供了宝贵的思路和参考。在此向美团技术团队表示衷心的感谢！🙏

---

<div align="center">

**如果这个项目对您有帮助，请给个 ⭐️ Star 支持一下！**

Made with ❤️ by Artemis

</div>