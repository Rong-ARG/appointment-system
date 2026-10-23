package com.ronogar.appointment_system.integration;

import com.ronogar.appointment_system.TestcontainersConfiguration;
import com.ronogar.appointment_system.enums.AppointmentStatus;
import com.ronogar.appointment_system.enums.Role;
import com.ronogar.appointment_system.models.Account;
import com.ronogar.appointment_system.models.Appointment;
import com.ronogar.appointment_system.models.Professional;
import com.ronogar.appointment_system.models.User;
import com.ronogar.appointment_system.repositories.AccountRepository;
import com.ronogar.appointment_system.repositories.AppointmentRepository;
import com.ronogar.appointment_system.repositories.ProfessionalRepository;
import com.ronogar.appointment_system.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class AppointmentSlotConstraintTest {

    private static final LocalDateTime SLOT = LocalDateTime.of(2030, 1, 15, 10, 30);

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProfessionalRepository professionalRepository;
    @Autowired
    private AppointmentRepository appointmentRepository;

    private User user;
    private Professional professional;

    @BeforeEach
    void setUp() {
        Account professionalAccount = new Account();
        professionalAccount.setEmail("slot-professional@test.com");
        professionalAccount.setPassword("irrelevant");
        professionalAccount.setRoles(new HashSet<>(Set.of(Role.PROFESSIONAL)));
        professionalAccount = accountRepository.save(professionalAccount);

        Professional newProfessional = new Professional();
        newProfessional.setAvailable(true);
        newProfessional.setAccount(professionalAccount);
        professional = professionalRepository.save(newProfessional);

        Account clientAccount = new Account();
        clientAccount.setEmail("slot-client@test.com");
        clientAccount.setPassword("irrelevant");
        clientAccount.setRoles(new HashSet<>(Set.of(Role.USER)));
        clientAccount = accountRepository.save(clientAccount);

        User newUser = new User();
        newUser.setAccount(clientAccount);
        user = userRepository.save(newUser);
    }

    @AfterEach
    void cleanUp() {
        appointmentRepository.deleteAll();
        userRepository.deleteAll();
        professionalRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    void save_throwsDataIntegrityViolation_whenTwoActiveAppointmentsShareProfessionalAndSlot() {
        appointmentRepository.saveAndFlush(appointment(AppointmentStatus.PENDING));

        assertThrows(DataIntegrityViolationException.class,
                () -> appointmentRepository.saveAndFlush(appointment(AppointmentStatus.CONFIRMED)));
    }

    @Test
    void save_allowsNewAppointment_whenPreviousOneInSameSlotWasCancelled() {
        appointmentRepository.saveAndFlush(appointment(AppointmentStatus.CANCELLED));
        appointmentRepository.saveAndFlush(appointment(AppointmentStatus.PENDING));

        assertEquals(2, appointmentRepository.count());
    }

    @Test
    void save_allowsSeveralCancelledAppointments_inTheSameSlot() {
        appointmentRepository.saveAndFlush(appointment(AppointmentStatus.CANCELLED));
        appointmentRepository.saveAndFlush(appointment(AppointmentStatus.CANCELLED));

        assertEquals(2, appointmentRepository.count());
    }

    private Appointment appointment(AppointmentStatus status) {
        Appointment appointment = new Appointment();
        appointment.setUser(user);
        appointment.setProfessional(professional);
        appointment.setDateTime(SLOT);
        appointment.setStatus(status);
        return appointment;
    }
}