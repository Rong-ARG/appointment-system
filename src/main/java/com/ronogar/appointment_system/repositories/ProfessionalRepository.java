package com.ronogar.appointment_system.repositories;

import com.ronogar.appointment_system.models.Professional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface ProfessionalRepository extends JpaRepository<Professional, Long> {

    Optional<Professional> findByAccountEmail(String email);
    List<Professional> findByLastName(String lastName);
    List<Professional> findBySpecialty(String specialty);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Professional p where p.id = :id")
    Optional<Professional> findByIdForUpdate(@Param("id") Long id);
}
