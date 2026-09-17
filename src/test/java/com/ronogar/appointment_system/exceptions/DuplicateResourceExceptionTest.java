package com.ronogar.appointment_system.exceptions;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class DuplicateResourceExceptionTest {

    @Test
     void testDuplicateResourceException(){

        DuplicateResourceException exception = new DuplicateResourceException("Duplicate resource");

        assertEquals("Duplicate resource", exception.getMessage());

    }

}
