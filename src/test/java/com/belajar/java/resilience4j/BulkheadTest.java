package com.belajar.java.resilience4j;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.bulkhead.ThreadPoolBulkhead;
import io.github.resilience4j.bulkhead.ThreadPoolBulkheadConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

@Slf4j
public class BulkheadTest {

    private AtomicLong counter = new AtomicLong(0L);

    @SneakyThrows
    public void slow() {
        long value = counter.incrementAndGet();
        log.info("Slow : {}", value);
        Thread.sleep(1_000L);
    }

    @Test
    void testSemaphore() throws InterruptedException {
        Bulkhead bulkhead = Bulkhead.ofDefaults("ak47");

        for (int i = 0; i < 1000; i++) {
            Runnable runnable = Bulkhead.decorateRunnable(bulkhead, this::slow);
            new Thread(runnable).start();
        }
        Thread.sleep(10_000L);
    }

    @Test
    void testThreadPool() {
        log.info(String.valueOf(Runtime.getRuntime().availableProcessors()));
        ThreadPoolBulkhead bulkhead = ThreadPoolBulkhead.ofDefaults("ak47");

        for (int i = 0; i < 1000; i++) {
            Supplier<CompletionStage<Void>> runnable =
                    ThreadPoolBulkhead.decorateRunnable(bulkhead, this::slow);
            runnable.get();
        }
    }

    @Test
    void testSemaphoreConfig() throws InterruptedException {
        BulkheadConfig config = BulkheadConfig.custom()
                .maxConcurrentCalls(5)
                .maxWaitDuration(Duration.ofSeconds(5))
                .build();
        Bulkhead bulkhead = Bulkhead.of("ak47", config);

        for (int i = 0; i < 10; i++) {
            Runnable runnable = Bulkhead.decorateRunnable(bulkhead, this::slow);
            new Thread(runnable).start();
        }

        Thread.sleep(10_000L);
    }

    @Test
    void testThreadPoolConfig() throws InterruptedException {
        log.info(String.valueOf(Runtime.getRuntime().availableProcessors()));
        ThreadPoolBulkheadConfig config = ThreadPoolBulkheadConfig.custom()
                .maxThreadPoolSize(5)
                .coreThreadPoolSize(5)
                .queueCapacity(1)
                .build();
        ThreadPoolBulkhead bulkhead = ThreadPoolBulkhead.of("ak47", config);

        for (int i = 0; i < 20; i++) {
            Supplier<CompletionStage<Void>> runnable =
                    ThreadPoolBulkhead.decorateRunnable(bulkhead, this::slow);
            runnable.get();
        }

        Thread.sleep(10_000L);
    }
}
