package com.ronogar.appointment_system.repositories;

import com.ronogar.appointment_system.enums.AppointmentStatus;
import com.ronogar.appointment_system.models.Appointment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @EntityGraph(attributePaths = {"user.account", "professional.account"})
    List<Appointment> findByUserId(Long id);

    @EntityGraph(attributePaths = {"user.account", "professional.account"})
    List<Appointment> findByProfessionalId(Long id);

    @Override
    @EntityGraph(attributePaths = {"user.account", "professional.account"})
    List<Appointment> findAll();

    @Override
    @EntityGraph(attributePaths = {"user.account", "professional.account"})
    Optional<Appointment> findById(Long id);

    boolean existsByProfessionalIdAndDateTimeAndStatusIn(
            Long professionalId, LocalDateTime dateTime, Collection<AppointmentStatus> statuses);
}