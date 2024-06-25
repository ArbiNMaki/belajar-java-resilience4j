package com.belajar.java.resilience4j;

import io.github.resilience4j.decorators.Decorators;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;

@Slf4j
public class DecoratorsTest {

    @SneakyThrows
    public void slow() {
        log.info("Slow");
        Thread.sleep(1_000L);
        throw new IllegalArgumentException("Error");
    }

    @Test
    void testDecorators() throws InterruptedException {
        RateLimiter rateLimiter = RateLimiter.of("ak47-ratelimiter", RateLimiterConfig.custom()
                .limitForPeriod(5)
                .limitRefreshPeriod(Duration.ofMinutes(1))
                .build());
        Retry retry = Retry.of("ak47-retry", RetryConfig.custom()
                .maxAttempts(10)
                .waitDuration(Duration.ofMillis(10))
                .build());
        Runnable runnable = Decorators.ofRunnable(this::slow)
                .withRetry(retry)
                .withRateLimiter(rateLimiter)
                .decorate();

        for (int i = 0; i < 100; i++) {
            new Thread(runnable).start();
        }

        Thread.sleep(10_000L);
    }
}
