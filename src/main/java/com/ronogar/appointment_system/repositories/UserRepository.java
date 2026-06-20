package com.ronogar.appointment_system.repositories;

import com.ronogar.appointment_system.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    List<User> findByLastName(String lastName);

}
