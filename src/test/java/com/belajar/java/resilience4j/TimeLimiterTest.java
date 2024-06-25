package com.belajar.java.resilience4j;

import io.github.resilience4j.timelimiter.TimeLimiter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

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
}
