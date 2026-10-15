package com.ronogar.appointment_system.repositories;

import com.ronogar.appointment_system.enums.AppointmentStatus;
import com.ronogar.appointment_system.models.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByUserId(Long id);
    List<Appointment> findByProfessionalId(Long id);
    boolean existsByProfessionalIdAndDateTimeAndStatusIn(
            Long professionalId, LocalDateTime dateTime, Collection<AppointmentStatus> statuses);
}
