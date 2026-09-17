package com.ronogar.appointment_system.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TokenInvalidExceptionTest {

    @Test
    void tokenInvalidExceptionTest() {

        TokenInvalidException tokenInvalidException = new TokenInvalidException("Token invalid");

        assertEquals("Token invalid", tokenInvalidException.getMessage());
    }
}
