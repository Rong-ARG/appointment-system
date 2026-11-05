package com.ronogar.appointment_system.integration;

import com.ronogar.appointment_system.TestcontainersConfiguration;
import com.ronogar.appointment_system.dtos.appointment.AppointmentPatchDTO;
import com.ronogar.appointment_system.enums.AppointmentStatus;
import com.ronogar.appointment_system.enums.Role;
import com.ronogar.appointment_system.exceptions.InvalidAppointmentStateException;
import com.ronogar.appointment_system.models.Account;
import com.ronogar.appointment_system.models.Appointment;
import com.ronogar.appointment_system.models.Professional;
import com.ronogar.appointment_system.models.User;
import com.ronogar.appointment_system.repositories.AccountRepository;
import com.ronogar.appointment_system.repositories.AppointmentRepository;
import com.ronogar.appointment_system.repositories.ProfessionalRepository;
import com.ronogar.appointment_system.repositories.UserRepository;
import com.ronogar.appointment_system.services.appointment.AppointmentService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class AppointmentStatusConcurrencyTest {

    private static final int ROUNDS = 20;
    private static final String PROFESSIONAL_EMAIL = "professional@test.com";
    private static final String CLIENT_EMAIL = "client@test.com";

    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProfessionalRepository professionalRepository;
    @Autowired
    private AppointmentRepository appointmentRepository;

    private Professional professional;
    private User client;

    @BeforeEach
    void setUp() {
        Account professionalAccount = new Account();
        professionalAccount.setEmail(PROFESSIONAL_EMAIL);
        professionalAccount.setPassword("irrelevant");
        professionalAccount.setRoles(new HashSet<>(Set.of(Role.PROFESSIONAL)));
        professionalAccount = accountRepository.save(professionalAccount);

        Professional newProfessional = new Professional();
        newProfessional.setFirstName("Pro");
        newProfessional.setLastName("Test");
        newProfessional.setSpecialty("Welder");
        newProfessional.setAvailable(true);
        newProfessional.setAccount(professionalAccount);
        professional = professionalRepository.save(newProfessional);

        Account clientAccount = new Account();
        clientAccount.setEmail(CLIENT_EMAIL);
        clientAccount.setPassword("irrelevant");
        clientAccount.setRoles(new HashSet<>(Set.of(Role.USER)));
        clientAccount = accountRepository.save(clientAccount);

        User newClient = new User();
        newClient.setFirstName("Client");
        newClient.setLastName("Test");
        newClient.setAccount(clientAccount);
        client = userRepository.save(newClient);
    }

    @AfterEach
    void cleanUp() {
        appointmentRepository.deleteAll();
        userRepository.deleteAll();
        professionalRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    void patchAppointment_concurrentConfirmAndCancel_neverLosesTheCancellation() throws Exception {

        ExecutorService executor = Executors.newFixedThreadPool(2);

        try {
            for (int round = 0; round < ROUNDS; round++) {
                Long appointmentId = savePendingAppointment(round).getId();

                CountDownLatch ready = new CountDownLatch(2);
                CountDownLatch start = new CountDownLatch(1);

                Future<Throwable> confirm = submitPatch(executor, ready, start,
                        PROFESSIONAL_EMAIL, Role.PROFESSIONAL, appointmentId, AppointmentStatus.CONFIRMED);
                Future<Throwable> cancel = submitPatch(executor, ready, start,
                        CLIENT_EMAIL, Role.USER, appointmentId, AppointmentStatus.CANCELLED);

                ready.await();
                start.countDown();

                Throwable confirmError = confirm.get(30, TimeUnit.SECONDS);
                Throwable cancelError = cancel.get(30, TimeUnit.SECONDS);

                assertAllowed(confirmError, round);
                assertAllowed(cancelError, round);
                assertTrue(confirmError == null || cancelError == null,
                        "Round " + round + ": at least one request must succeed");

                AppointmentStatus finalStatus = appointmentRepository.findById(appointmentId)
                        .orElseThrow().getStatus();

                if (cancelError == null) {
                    assertEquals(AppointmentStatus.CANCELLED, finalStatus,
                            "Round " + round + ": a successful cancellation was lost");
                } else {
                    assertEquals(AppointmentStatus.CONFIRMED, finalStatus,
                            "Round " + round + ": cancel failed, so the confirmation must stand");
                }
            }
        } finally {
            executor.shutdownNow();
        }
    }

    private Future<Throwable> submitPatch(ExecutorService executor, CountDownLatch ready, CountDownLatch start,
                                          String email, Role role, Long appointmentId, AppointmentStatus status) {
        return executor.submit(() -> {
            authenticateAs(email, role);
            try {
                ready.countDown();
                start.await();
                appointmentService.patchAppointment(appointmentId, new AppointmentPatchDTO(status));
                return null;
            } catch (Throwable error) {
                return error;
            } finally {
                SecurityContextHolder.clearContext();
            }
        });
    }

    private void assertAllowed(Throwable error, int round) {
        if (error != null
                && !(error instanceof OptimisticLockingFailureException)
                && !(error instanceof InvalidAppointmentStateException)) {
            fail("Round " + round + ": unexpected error: " + error);
        }
    }

    private Appointment savePendingAppointment(int round) {
        Appointment appointment = new Appointment();
        appointment.setUser(client);
        appointment.setProfessional(professional);
        appointment.setStatus(AppointmentStatus.PENDING);
        appointment.setDateTime(LocalDateTime.of(2030, 1, 15, 8, 0).plusMinutes(30L * round));
        return appointmentRepository.save(appointment);
    }

    private void authenticateAs(String email, Role role) {
        UserDetails principal = org.springframework.security.core.userdetails.User
                .withUsername(email)
                .password("irrelevant")
                .roles(role.name())
                .build();
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(
                principal, null, principal.getAuthorities()));
        SecurityContextHolder.setContext(context);
    }
}