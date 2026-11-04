package com.ronogar.appointment_system.services;

import com.ronogar.appointment_system.exceptions.TooManyRequestsException;
import com.ronogar.appointment_system.services.auth.LoginRateLimiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LoginRateLimiterTest {

    private LoginRateLimiter loginRateLimiter;

    @BeforeEach
    void setUp() {
        loginRateLimiter = new LoginRateLimiter();
    }

    @Test
    void checkAllowed_withinLimit_doesNotThrow() {
        for (int i = 0; i < 5; i++) {
            assertDoesNotThrow(() -> loginRateLimiter.checkAllowed("1.1.1.1"));
        }
    }

    @Test
    void checkAllowed_exceedsLimit_throwsTooManyRequests() {
        for (int i = 0; i < 5; i++) {
            loginRateLimiter.checkAllowed("1.1.1.1");
        }

        assertThrows(TooManyRequestsException.class,
                () -> loginRateLimiter.checkAllowed("1.1.1.1"));
    }

    @Test
    void checkAllowed_differentKeys_areCountedSeparately() {
        for (int i = 0; i < 5; i++) {
            loginRateLimiter.checkAllowed("1.1.1.1");
        }

        assertDoesNotThrow(() -> loginRateLimiter.checkAllowed("2.2.2.2"));
    }
}