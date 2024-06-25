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
import java.util.function.Supplier;

@Slf4j
public class FallbackTest {

    @SneakyThrows
    public String sayHello() {
        log.info("Say Hello");
        Thread.sleep(1_000L);
        throw new IllegalArgumentException("Ups");
    }

    @Test
    void testFallback() {
        RateLimiter rateLimiter = RateLimiter.of("ak47-ratelimiter", RateLimiterConfig.custom()
                .limitForPeriod(5)
                .limitRefreshPeriod(Duration.ofMinutes(1))
                .build());
        Retry retry = Retry.of("ak47-retry", RetryConfig.custom()
                .maxAttempts(10)
                .waitDuration(Duration.ofMillis(10))
                .build());

        Supplier<String> supplier = Decorators.ofSupplier(this::sayHello)
                .withRetry(retry)
                .withRateLimiter(rateLimiter)
                .withFallback(_ -> "Hello Guest")
                .decorate();

        System.out.println(supplier.get());
    }
}
