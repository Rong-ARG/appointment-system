package com.ronogar.appointment_system.repositories;

import com.ronogar.appointment_system.models.Professional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface ProfessionalRepository extends JpaRepository<Professional, Long> {

    Optional<Professional> findByAccountEmail(String email);
    List<Professional> findByLastName(String lastName);
}
