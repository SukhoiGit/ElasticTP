<div align="center">
  <img src=".idea/icon.png" alt="ElasticTP Logo" width="120" height="120">
  
  <h1>🚀 ElasticTP</h1>
  
  <p><strong>Dynamic Thread Pool Management Framework</strong></p>
  
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
  <b>🎉 A Lightweight, High-Performance Dynamic Thread Pool Management Framework</b><br>
  Supports dynamic thread pool parameter adjustment, real-time monitoring, intelligent alarms, and multi-platform notifications
</p>

<div align="center">

English | [简体中文](README.md)

</div>

---

## 📖 Introduction

ElasticTP (Elastic ThreadPool) is a dynamic thread pool management framework based on configuration centers, designed to help developers better manage and utilize thread pool resources. It provides enhanced features such as dynamic adjustment of thread pool parameters, runtime monitoring, and exception alarms, making thread pool usage more flexible, controllable, and secure.

### 💡 Project Origin

After reading Meituan's technical article [《Java Thread Pool Implementation Principles and Practices at Meituan》](https://tech.meituan.com/2020/04/02/java-pooling-pratice-in-meituan.html), I deeply realized that thread pools are widely used in actual work scenarios. However, JDK native thread pools have the following pain points:

- ⚠️ **Difficult Parameter Configuration** - JDK native thread pool parameters are difficult to configure correctly in one go
- 📉 **Lack of Monitoring** - Lack of effective monitoring to understand thread pool runtime status and resource consumption
- 🔧 **High Adjustment Cost** - Thread pool parameter adjustments require application restarts, affecting business continuity
- 🚨 **Missing Alarm Mechanism** - Unable to detect thread pool anomalies in time

**Cases**:

**Case 1**: 2018 XX Page Display Interface Mass Call Degradation:

**Incident Description**: XX page display interface experienced massive call degradation, with numbers ranging from dozens to hundreds.

**Incident Cause**: The service display interface's internal logic used thread pools for parallel computing. Due to poor traffic estimation, the maximum core size was set too small, causing massive RejectedExecutionException and triggering interface degradation conditions, as shown below:

![](https://oss.open8gu.com/1df932840b31f41931bb69e16be2932844240.png)

**Case 2**: 2018 XX Business Service Unavailable S2 Level Incident

**Incident Description**: XX business service execution time was too long, causing overall timeout as an upstream service, with massive downstream service call failures.

**Incident Cause**: The service's internal request processing logic used thread pools for resource isolation. Due to excessively long queue settings, the maximum thread count became ineffective. When request volume increased, massive tasks accumulated in the queue, task execution time became too long, ultimately causing massive downstream service call timeout failures. The diagram is as follows:

![](https://oss.open8gu.com/668e3c90f4b918bfcead2f4280091e9757284.png)

To solve these problems, this configuration center-based dynamic thread pool framework was designed and implemented. ElasticTP supports online dynamic adjustment of thread pool core parameters, runtime status monitoring and threshold alarms, compatible with mainstream configuration centers such as Nacos/Apollo, achieving hot updates and unified management of thread pool parameters.

**Special thanks to Meituan's technical team for this excellent article, which provided valuable insights and practical guidance for this project!** 🙏

---

## ✨ Core Features

### 🎯 Dynamic Parameter Adjustment
- ✅ Dynamic adjustment of core pool size (corePoolSize)
- ✅ Dynamic adjustment of maximum pool size (maximumPoolSize)
- ✅ Dynamic adjustment of queue capacity (queueCapacity)
- ✅ Dynamic adjustment of keep-alive time (keepAliveTime)
- ✅ Dynamic switching of rejection policies (rejectedExecutionHandler)
- ✅ Parameter changes take effect in real-time without application restart

### 📊 Runtime Monitoring
- 📈 Real-time monitoring of thread pool core metrics
  - Current active thread count
  - Historical maximum thread count
  - Current queue task count
  - Queue remaining capacity
  - Total completed tasks
  - Total rejected tasks
- 🔍 Support for custom monitoring metric extensions
- 📉 Monitoring data collection interface provided

### 🚨 Intelligent Alarms
- ⚠️ Queue capacity alarms (customizable threshold)
- ⚠️ Active thread count alarms (customizable threshold)
- ⚠️ Task rejection alarms
- 📢 DingTalk robot notification support
- 🔔 Custom alarm notification channels
- ⏰ Built-in alarm rate limiting to avoid alarm flooding

### 🔌 Configuration Center Integration
- ☁️ Perfect integration with Nacos (supports Nacos Cloud)
- 🌙 Perfect integration with Apollo
- 🔄 Automatic configuration refresh on changes
- 🎨 Support for YAML and Properties formats
- 🔧 Unified configuration management interface

### 🎨 Ease of Use
- 🏷️ Simple annotation-based programming: `@DynamicThreadPool`
- 🚦 One-click enablement: `@EnableElasticTp`
- 📦 Out-of-the-box Spring Boot Starter
- 📝 Detailed example projects
- 🎯 Zero-intrusion design, decoupled from business code

---

## 🏗️ Architecture Design

### Module Structure

```
ElasticTP
├── core                                    # Core module
│   ├── executor                           # Thread pool executor
│   ├── monitor                            # Monitoring module
│   ├── alarm                              # Alarm module
│   ├── notification                       # Notification service
│   └── parser                             # Configuration parser
├── spring-base                            # Spring base integration
│   ├── enable                             # Enable annotation
│   └── support                            # Spring support classes
├── starter                                # Spring Boot Starter
│   ├── common-spring-boot-starter        # Common auto-configuration
│   ├── apollo-spring-boot-starter        # Apollo integration
│   └── nacos-cloud-spring-boot-starter   # Nacos integration
└── example                                # Example projects
    ├── apollo-example                     # Apollo example
    └── nacos-cloud-example                # Nacos example
```

### Core Components

- **ElasticTpExecutor**: Enhanced thread pool executor supporting dynamic parameter adjustment and monitoring
- **ElasticTpRegistry**: Thread pool registry for unified management of all dynamic thread pools
- **ThreadPoolMonitor**: Thread pool monitor for regular collection of runtime metrics
- **ThreadPoolAlarmChecker**: Alarm checker that triggers alarms based on thresholds
- **NotifierDispatcher**: Notification dispatcher supporting multiple notification channels
- **AbstractDynamicThreadPoolRefresher**: Configuration refresh abstract class supporting multiple configuration centers

---

## 🚀 Quick Start

### Requirements

- JDK 17+
- Spring Boot 3.0.7+
- Maven 3.6+

### 1️⃣ Add Dependency

#### Using Nacos Configuration Center

```xml
<dependency>
    <groupId>com.artemis</groupId>
    <artifactId>nacos-cloud-spring-boot-starter</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

#### Using Apollo Configuration Center

```xml
<dependency>
    <groupId>com.artemis</groupId>
    <artifactId>apollo-spring-boot-starter</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### 2️⃣ Enable ElasticTP

Add `@EnableElasticTp` annotation to your Spring Boot application class:

```java
@SpringBootApplication
@EnableElasticTp
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

### 3️⃣ Configure Thread Pools

Create thread pool configuration in your configuration center (Nacos YAML format example):

```yaml
elastic-tp:
  # Enable ElasticTP
  enabled: true
  # Global alarm configuration
  alarm:
    alarm-types:
      - CAPACITY    # Capacity alarm
      - REJECT      # Rejection alarm
      - ACTIVE_SIZE # Active size alarm
    interval: 5     # Alarm interval (minutes)
  
  # Notification configuration
  notifiers:
    - platform: DING_TALK
      token: your-dingtalk-webhook-token
      secret: your-dingtalk-secret
  
  # Thread pool configuration
  executors:
    - thread-pool-id: order-pool          # Unique thread pool identifier
      core-pool-size: 10                   # Core pool size
      maximum-pool-size: 20                # Maximum pool size
      queue-capacity: 500                  # Queue capacity
      queue-type: ResizableCapacityLinkedBlockingQueue  # Queue type
      keep-alive-time: 60                  # Keep-alive time (seconds)
      rejected-handler: CallerRunsPolicy   # Rejection policy
      thread-name-prefix: order-thread-    # Thread name prefix
      allow-core-thread-timeout: false     # Allow core thread timeout
      
      # Alarm threshold configuration
      alarm:
        capacity-threshold: 80             # Capacity alarm threshold (%)
        active-size-threshold: 80          # Active size alarm threshold (%)
    
    - thread-pool-id: payment-pool
      core-pool-size: 8
      maximum-pool-size: 16
      queue-capacity: 300
      queue-type: LinkedBlockingQueue
      keep-alive-time: 60
      rejected-handler: AbortPolicy
      thread-name-prefix: payment-thread-
```

### 4️⃣ Create Dynamic Thread Pool

#### Method 1: Using `@DynamicThreadPool` Annotation (Recommended)

```java
@Configuration
public class ThreadPoolConfig {
    
    @Bean("orderPool")
    @DynamicThreadPool
    public ThreadPoolExecutor orderThreadPool() {
        // The thread pool returned here will be automatically managed and enhanced
        // Parameters will be read from configuration center (matched by thread-pool-id)
        return null;
    }
    
    @Bean("paymentPool")
    @DynamicThreadPool
    public ThreadPoolExecutor paymentThreadPool() {
        return null;
    }
}
```

#### Method 2: Manual Creation

```java
@Configuration
public class ThreadPoolConfig {
    
    @Bean
    public ThreadPoolExecutor orderThreadPool() {
        // Get from registry using configured thread pool ID
        return ElasticTpRegistry.getExecutor("order-pool");
    }
}
```

### 5️⃣ Use Thread Pool

```java
@Service
public class OrderService {
    
    @Resource
    private ThreadPoolExecutor orderPool;
    
    public void processOrder(Order order) {
        orderPool.execute(() -> {
            // Order processing logic
            System.out.println("Processing order: " + order.getId());
        });
    }
}
```

---

## 📊 Monitoring Example

ElasticTP provides rich monitoring metrics that you can view as follows:

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

Monitoring data example:

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

## 🚨 Alarm Configuration

### Alarm Types

ElasticTP supports the following three alarm types:

| Alarm Type | Description | Trigger Condition |
|-----------|-------------|-------------------|
| **CAPACITY** | Queue capacity alarm | Queue usage exceeds threshold |
| **ACTIVE_SIZE** | Active thread alarm | Active thread ratio exceeds threshold |
| **REJECT** | Task rejection alarm | Task rejection occurs |

### DingTalk Alarm Configuration

1. Create a DingTalk group robot and obtain webhook token and secret
2. Configure notification information in configuration center
3. Alarm messages will be automatically sent to DingTalk group

Alarm message example:

```
【ElasticTP Alarm】
Thread Pool: order-pool
Alarm Type: Queue Capacity Alarm
Current Queue Usage: 85%
Queue Capacity: 500
Current Task Count: 425
Alarm Time: 2025-12-29 10:00:00
```

![](https://oss.open8gu.com/image-20250525171313388.png)

---

## 🔧 Dynamic Parameter Adjustment

Simply modify the configuration in the configuration center without restarting the application, and parameters will take effect in real-time:

### Before Adjustment
```yaml
- thread-pool-id: order-pool
  core-pool-size: 10
  maximum-pool-size: 20
  queue-capacity: 500
```

### After Adjustment
```yaml
- thread-pool-id: order-pool
  core-pool-size: 20        # ⬆️ Core pool size increased
  maximum-pool-size: 40     # ⬆️ Maximum pool size increased
  queue-capacity: 1000      # ⬆️ Queue capacity increased
```

ElasticTP will automatically detect configuration changes and apply new parameters, while sending change notifications.

![](https://oss.open8gu.com/image-20250525170844026.png)

---

## 📚 Example Projects

This project provides complete example code:

- **apollo-example**: Demonstrates how to integrate with Apollo configuration center
- **nacos-cloud-example**: Demonstrates how to integrate with Nacos configuration center

Example code is located in the `example` directory and can be run directly.

---

## 🛠️ Technology Stack

- **Core Framework**: Spring Boot 3.0.7, Spring Cloud 2022.0.3
- **Configuration Centers**: Nacos 2022.0.0.0-RC2, Apollo 2.1.0
- **Utility Libraries**: Hutool 5.8.25, Fastjson2 2.0.53
- **Build Tool**: Maven

---

## 🤝 Contributing

Contributions, issues, and suggestions are welcome!

1. Fork this repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.html).

---

## 📧 Contact

- **Author**: Artemis
- **Email**: originblue062@gmail.com
- **GitHub**: https://github.com/SukhoiGit
- **Blog**: https://sukhoigit.github.io

---

## 🙏 Acknowledgments

### Special Thanks

The inspiration for this project came from [Meituan's Technical Team](https://tech.meituan.com/)'s excellent article [《Java Thread Pool Implementation Principles and Practices at Meituan》](https://tech.meituan.com/2020/04/02/java-pooling-pratice-in-meituan.html).

This article provides an in-depth yet accessible introduction to thread pool implementation principles and practical applications in real business scenarios, offering valuable insights and references for the design and implementation of this project. Heartfelt thanks to Meituan's technical team! 🙏

---

<div align="center">

**If this project helps you, please give it a ⭐️ Star!**

Made with ❤️ by Artemis

</div>