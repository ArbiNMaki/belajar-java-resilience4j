package com.belajar.java.resilience4j;

import io.github.resilience4j.retry.Retry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

@Slf4j
public class RetryTest {

    void callMe() {
        log.info("Try call me");
        throw new IllegalArgumentException("Ups error");
    }

    @Test
    void testCreateNewRetry() {
        Retry retry = Retry.ofDefaults("ak47");
        Runnable runnable = Retry.decorateRunnable(retry, this::callMe);
        runnable.run();
    }

    private String hello() {
        log.info("Call say hello");
        throw new IllegalArgumentException("Ups error say hello");
    }

    @Test
    void testCreateSupplier() {
        Retry retry = Retry.ofDefaults("ak47");
        Supplier<String> supplier = Retry.decorateSupplier(retry, this::hello);
        supplier.get();
    }
}
