package com.jeel.portfolio.service;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class SubmissionRateLimiter {

    private final Map<String, Instant> lastSubmission = new ConcurrentHashMap<>();

    public boolean tryAcquire(String key, Duration window) {
        Instant now = Instant.now();
        AtomicBoolean allowed = new AtomicBoolean(false);

        lastSubmission.compute(key, (k, previous) -> {
            if (previous == null || Duration.between(previous, now).compareTo(window) >= 0) {
                allowed.set(true);
                return now;
            }
            allowed.set(false);
            return previous;
        });

        return allowed.get();
    }

    public Optional<Instant> lastSeen(String key) {
        return Optional.ofNullable(lastSubmission.get(key));
    }
}
