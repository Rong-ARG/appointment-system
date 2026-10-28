package com.ronogar.appointment_system.enums;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppointmentStatusTest {

    @ParameterizedTest
    @CsvSource({
            "PENDING, CONFIRMED",
            "PENDING, CANCELLED",
            "CONFIRMED, CANCELLED"
    })
    void canTransitionTo_returnsTrue_whenTransitionIsAllowed(AppointmentStatus from, AppointmentStatus to) {
        assertTrue(from.canTransitionTo(to));
    }

    @ParameterizedTest
    @CsvSource({
            "PENDING, PENDING",
            "CONFIRMED, PENDING",
            "CONFIRMED, CONFIRMED"
    })
    void canTransitionTo_returnsFalse_whenTransitionIsNotAllowed(AppointmentStatus from, AppointmentStatus to) {
        assertFalse(from.canTransitionTo(to));
    }

    @ParameterizedTest
    @EnumSource(AppointmentStatus.class)
    void canTransitionTo_returnsFalse_whenCurrentStatusIsCancelled(AppointmentStatus target) {
        assertFalse(AppointmentStatus.CANCELLED.canTransitionTo(target));
    }
}