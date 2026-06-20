package com.ronogar.appointment_system.repositories;

import com.ronogar.appointment_system.models.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
}
