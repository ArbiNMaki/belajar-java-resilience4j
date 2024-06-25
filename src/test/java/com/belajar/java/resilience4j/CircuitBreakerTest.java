package com.belajar.java.resilience4j;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
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
}
