package com.artemis.elastictp.configuration;

import com.artemis.elastictp.core.executor.support.BlockingQueueTypeEnum;
import com.artemis.elastictp.core.toolkit.ThreadPoolExecutorBuilder;
import com.artemis.elastictp.spring.base.DynamicThreadPool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 动态线程池配置
 * <p>
 */
@Configuration
public class DynamicThreadPoolConfiguration {

    @Bean
    @DynamicThreadPool
    public ThreadPoolExecutor elasticTpProducer() {
        return ThreadPoolExecutorBuilder.builder()
                .threadPoolId("elasticTp-producer")
                .corePoolSize(2)
                .maximumPoolSize(4)
                .keepAliveTime(9999L)
                .awaitTerminationMillis(5000L)
                .workQueueType(BlockingQueueTypeEnum.SYNCHRONOUS_QUEUE)
                .threadFactory("elasticTp-producer_")
                .rejectedHandler(new ThreadPoolExecutor.CallerRunsPolicy())
                .dynamicPool()
                .build();
    }

    @Bean
    @DynamicThreadPool
    public ThreadPoolExecutor elasticTpConsumer() {
        return ThreadPoolExecutorBuilder.builder()
                .threadPoolId("elasticTp-consumer")
                .corePoolSize(4)
                .maximumPoolSize(6)
                .keepAliveTime(9999L)
                .workQueueType(BlockingQueueTypeEnum.SYNCHRONOUS_QUEUE)
                .threadFactory("elasticTp-consumer_")
                .rejectedHandler(new ThreadPoolExecutor.CallerRunsPolicy())
                .dynamicPool()
                .build();
    }
}
