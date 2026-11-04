package com.ronogar.appointment_system.services.auth;

import com.ronogar.appointment_system.exceptions.TooManyRequestsException;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginRateLimiter {

    private static final int MAX_ATTEMPTS = 5;
    private static final long WINDOW_MILLIS = 60_000;
    private static final int CLEANUP_THRESHOLD = 10_000;

    private final Map<String, Window> windows = new ConcurrentHashMap<>();

    public void checkAllowed(String key) {
        long now = System.currentTimeMillis();

        if (windows.size() > CLEANUP_THRESHOLD) {
            windows.values().removeIf(window -> now - window.start() >= WINDOW_MILLIS);
        }

        Window window = windows.compute(key, (k, current) -> {
            if (current == null || now - current.start() >= WINDOW_MILLIS) {
                return new Window(now, 1);
            }
            return new Window(current.start(), current.count() + 1);
        });

        if (window.count() > MAX_ATTEMPTS) {
            throw new TooManyRequestsException("Too many login attempts. Try again in a minute");
        }
    }

    private record Window(long start, int count) {
    }
}