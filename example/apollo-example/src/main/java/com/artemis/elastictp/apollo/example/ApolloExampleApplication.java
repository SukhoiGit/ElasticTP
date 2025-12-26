package com.artemis.elastictp.apollo.example;

import com.artemis.elastictp.spring.base.enable.EnableElasticTp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Apollo 配置中心示例应用程序
 * <p>
 * 项目启动成功后，修改 Apollo 配置文件中的动态线程池配置，观察控制台是否有日志打印
 * 示例日志打印：
 * [elasticTp-producer] Dynamic thread pool parameter changed:
 * corePoolSize: 12 => 12
 * maximumPoolSize: 24 => 24
 * capacity: 10000 => 10000
 * keepAliveTime: 19999 => 9999
 * rejectedType: CallerRunsPolicy => CallerRunsPolicy
 * allowCoreThreadTimeOut: false => false
 * <p>
 */
@EnableElasticTp
@SpringBootApplication
public class ApolloExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApolloExampleApplication.class, args);
    }
}
