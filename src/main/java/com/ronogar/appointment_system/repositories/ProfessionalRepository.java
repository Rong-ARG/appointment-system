package com.ronogar.appointment_system.repositories;

import com.ronogar.appointment_system.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfessionalRepository extends JpaRepository<User, Long> {
}
