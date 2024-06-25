package com.belajar.java.resilience4j;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class CircuitBreakerTest {

    public void callMe() {
        log.info("Call Me");
        throw new IllegalArgumentException("Ups Error");
    }

    @Test
    void testCircuitBreaker() {
        CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("ak47");

        for (int i = 0; i < 200; i++) {
            try {
                Runnable runnable = CircuitBreaker.decorateRunnable(circuitBreaker, this::callMe);
                runnable.run();
            } catch (Exception e) {
                log.error("Ups {}", e.getMessage());
            }
        }
    }

    @Test
    void testCircuitBreakerConfig() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .failureRateThreshold(10f)
                .slidingWindowSize(10)
                .minimumNumberOfCalls(10)
                .build();
        CircuitBreaker circuitBreaker = CircuitBreaker.of("ak47", config);

        for (int i = 0; i < 100; i++) {
            try {
                Runnable runnable = CircuitBreaker.decorateRunnable(circuitBreaker, this::callMe);
                runnable.run();
            } catch (Exception e) {
                log.error("Ups {}", e.getMessage());
            }
        }
    }
}
