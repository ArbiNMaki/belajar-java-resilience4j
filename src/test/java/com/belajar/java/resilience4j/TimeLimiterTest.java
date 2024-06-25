package com.belajar.java.resilience4j;

import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
public class TimeLimiterTest {

    @SneakyThrows
    public String slow() {
        log.info("Slow");
        Thread.sleep(5000L);
        return "Arbi";
    }

    @Test
    void testTimeLimiter() throws Exception {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> future = executorService.submit(this::slow);

        TimeLimiter limiter = TimeLimiter.ofDefaults("ak47");
        Callable<String> callable = TimeLimiter.decorateFutureSupplier(limiter, () -> future);

        callable.call();
    }

    @Test
    void testTimeLimiterConfig() throws Exception {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> future = executorService.submit(this::slow);

        TimeLimiterConfig config = TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(20))
                .cancelRunningFuture(true)
                .build();

        TimeLimiter limiter = TimeLimiter.of("ak47", config);
        Callable<String> callable = TimeLimiter.decorateFutureSupplier(limiter, () -> future);

        callable.call();
    }

    @Test
    void testTimeLimiterRegistry() throws Exception {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> future = executorService.submit(this::slow);

        TimeLimiterConfig config = TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(20))
                .cancelRunningFuture(true)
                .build();
        TimeLimiterRegistry registry = TimeLimiterRegistry.ofDefaults();
        registry.addConfiguration("config", config);
        TimeLimiter limiter = registry.timeLimiter("ak47", config);
        Callable<String> callable = TimeLimiter.decorateFutureSupplier(limiter, () -> future);

        callable.call();
    }
}
