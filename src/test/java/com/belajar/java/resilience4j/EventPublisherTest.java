package com.belajar.java.resilience4j;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

@Slf4j
public class EventPublisherTest {

    private String hello() {
        throw new IllegalArgumentException("Ups");
    }

    @Test
    void testEventPublisher() {
        Retry retry = Retry.ofDefaults("ak47");
        retry.getEventPublisher().onRetry(_ -> log.info("Try to Retry"));

        try {
            Supplier<String> supplier = Retry.decorateSupplier(retry, this::hello);
            supplier.get();
        } catch (Exception e) {
            System.out.println(retry.getMetrics().getNumberOfFailedCallsWithRetryAttempt());
            System.out.println(retry.getMetrics().getNumberOfFailedCallsWithoutRetryAttempt());
            System.out.println(retry.getMetrics().getNumberOfSuccessfulCallsWithRetryAttempt());
            System.out.println(retry.getMetrics().getNumberOfSuccessfulCallsWithoutRetryAttempt());
        }
    }

    @Test
    void testRetryRegistry() {
        RetryRegistry registry = RetryRegistry.ofDefaults();
        registry.getEventPublisher()
                .onEntryAdded(event -> log.info("New Retry Added {}", event.getAddedEntry().getName()));

        registry.retry("ak47");
        registry.retry("config");
    }
}
