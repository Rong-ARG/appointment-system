package com.ronogar.appointment_system.exceptions;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class InvalidAppointmentStateExceptionTest {

    @Test
    void shouldStoreTheMessagePassedIn(){
        InvalidAppointmentStateException exception = new InvalidAppointmentStateException("cancelled appointments cannot change status");

        assertEquals("cancelled appointments cannot change status", exception.getMessage());
    }
}