package com.ronogar.appointment_system.integration;

import com.ronogar.appointment_system.TestcontainersConfiguration;
import com.ronogar.appointment_system.dtos.appointment.AppointmentRequestDTO;
import com.ronogar.appointment_system.enums.Role;
import com.ronogar.appointment_system.exceptions.DuplicateResourceException;
import com.ronogar.appointment_system.models.Account;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class AppointmentBookingConcurrencyTest {

    private static final int CLIENTS = 5;

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
    private final List<String> clientEmails = new ArrayList<>();

    @BeforeEach
    void setUp() {
        Account professionalAccount = new Account();
        professionalAccount.setEmail("professional@test.com");
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

        for (int i = 0; i < CLIENTS; i++) {
            String email = "client" + i + "@test.com";

            Account account = new Account();
            account.setEmail(email);
            account.setPassword("irrelevant");
            account.setRoles(new HashSet<>(Set.of(Role.USER)));
            account = accountRepository.save(account);

            User user = new User();
            user.setFirstName("Client" + i);
            user.setLastName("Test");
            user.setAccount(account);
            userRepository.save(user);

            clientEmails.add(email);
        }
    }

    @AfterEach
    void cleanUp() {
        appointmentRepository.deleteAll();
        userRepository.deleteAll();
        professionalRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    void createAppointment_concurrentRequestsForSameSlot_onlyOneSucceeds() throws Exception {

        AppointmentRequestDTO request = new AppointmentRequestDTO();
        request.setProfessionalId(professional.getId());
        request.setDateTime(LocalDateTime.of(2030, 1, 15, 10, 30));

        ExecutorService executor = Executors.newFixedThreadPool(CLIENTS);
        CountDownLatch ready = new CountDownLatch(CLIENTS);
        CountDownLatch start = new CountDownLatch(1);

        List<Future<Throwable>> results = new ArrayList<>();
        for (String email : clientEmails) {
            results.add(executor.submit(() -> {
                authenticateAs(email);
                try {
                    ready.countDown();
                    start.await();
                    appointmentService.createAppointment(request);
                    return null;
                } catch (Throwable error) {
                    return error;
                } finally {
                    SecurityContextHolder.clearContext();
                }
            }));
        }

        ready.await();
        start.countDown();

        int successes = 0;
        int duplicates = 0;
        for (Future<Throwable> result : results) {
            Throwable error = result.get(30, TimeUnit.SECONDS);
            if (error == null) {
                successes++;
            } else if (error instanceof DuplicateResourceException) {
                duplicates++;
            } else {
                fail("Unexpected error: " + error);
            }
        }
        executor.shutdown();

        assertEquals(1, successes);
        assertEquals(CLIENTS - 1, duplicates);
        assertEquals(1, appointmentRepository.count());
    }

    private void authenticateAs(String email) {

        UserDetails principal = org.springframework.security.core.userdetails.User
                .withUsername(email)
                .password("irrelevant")
                .roles("USER")
                .build();
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        context.setAuthentication(new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getAuthorities()));
        SecurityContextHolder.setContext(context);
    }
}