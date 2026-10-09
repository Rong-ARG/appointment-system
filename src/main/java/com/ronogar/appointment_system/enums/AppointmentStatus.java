package com.ronogar.appointment_system.enums;

public enum AppointmentStatus {

    PENDING, CONFIRMED, CANCELLED;

    public boolean canTransitionTo(AppointmentStatus target) {
        return switch (this) {
            case PENDING -> target == CONFIRMED || target == CANCELLED;
            case CONFIRMED -> target == CANCELLED;
            case CANCELLED -> false;
        };
    }
}