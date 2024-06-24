package com.belajar.java.resilience4j;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.function.Supplier;
//import java.util.function.Supplier;

@Slf4j
public class RetryConfigTest {

    private String hello() {
        log.info("Call hello");
        throw new IllegalArgumentException("Ups error");
    }

    @Test
    void testRetryConfig() {
        RetryConfig retryConfig = RetryConfig.custom()
                .maxAttempts(5)
                .waitDuration(Duration.ofSeconds(2))
                .retryExceptions(IllegalArgumentException.class)
                .build();

        Retry retry = Retry.of("ak47", retryConfig);

        Supplier<String> supplier = Retry.decorateSupplier(retry, this::hello);
        supplier.get();
    }
}
