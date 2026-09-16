package com.ronogar.appointment_system.exceptions;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class InvalidAccountStateExceptionTest {

    @Test
    void shouldStoreTheMessagePassedIn(){
        InvalidAccountStateException exception = new InvalidAccountStateException("you need to delete your professional profile first");

        assertEquals("you need to delete your professional profile first", exception.getMessage());
    }
}
