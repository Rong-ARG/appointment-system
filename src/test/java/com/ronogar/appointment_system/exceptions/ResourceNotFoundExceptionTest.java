package com.ronogar.appointment_system.exceptions;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class ResourceNotFoundExceptionTest {

    @Test
    public void testMessage() {

        ResourceNotFoundException exception = new ResourceNotFoundException("Resource not found");
        assertEquals("Resource not found", exception.getMessage());
    }
}
