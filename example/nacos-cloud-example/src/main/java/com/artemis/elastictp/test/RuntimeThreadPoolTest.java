package com.artemis.elastictp.test;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

/**
 * 线程池运行时测试用例
 * <p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RuntimeThreadPoolTest {

    private final ThreadPoolExecutor elasticTpProducer;
    private final ThreadPoolExecutor elasticTpConsumer;

    private static final int MAX_TASK = Integer.MAX_VALUE;

    // 使用更安全的线程池构造
    private final ExecutorService simulationExecutor = new ThreadPoolExecutor(
            2, 2,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(),
            new ThreadFactory() {
                private int count = 0;

                public Thread newThread(Runnable r) {
                    return new Thread(r, "simulator-thread-" + count++);
                }
            }
    );

    @PostConstruct
    public void test() {
        simulationExecutor.submit(() -> simulateHighActiveThreadUsage());

        simulationExecutor.submit(() -> simulateQueueUsageHigh());
    }

    /**
     * 模拟活跃线程数占比高的情况
     */
    @SneakyThrows
    private void simulateHighActiveThreadUsage() {
        for (int i = 0; i < MAX_TASK; i++) {
            TimeUnit.MILLISECONDS.sleep(10);
            try {
                elasticTpProducer.execute(() -> {
                    try {
                        TimeUnit.MILLISECONDS.sleep(60); // 模拟长时间执行
                    } catch (InterruptedException e) {
                        System.out.println("Thread " + Thread.currentThread().getName() + " interrupted");
                        Thread.currentThread().interrupt();
                    } catch (Exception ignored) {
                        System.out.println("Thread " + Thread.currentThread().getName() + " Exception");
                    }
                });
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * 模拟阻塞队列占比高的情况
     */
    @SneakyThrows
    private void simulateQueueUsageHigh() {
        for (int i = 0; i < MAX_TASK; i++) {
            TimeUnit.MILLISECONDS.sleep(10);
            try {
                elasticTpConsumer.execute(() -> {
                    try {
                        TimeUnit.MILLISECONDS.sleep(60); // 模拟长时间执行
                    } catch (InterruptedException e) {
                        System.out.println("Thread " + Thread.currentThread().getName() + " interrupted");
                        Thread.currentThread().interrupt();
                    }
                });
            } catch (Exception ignored) {
            }
        }
    }
}
