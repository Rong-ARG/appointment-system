package com.ronogar.appointment_system.services;

import com.ronogar.appointment_system.dtos.appointment.AppointmentResponseDTO;
import com.ronogar.appointment_system.exceptions.ResourceNotFoundException;
import com.ronogar.appointment_system.models.Account;
import com.ronogar.appointment_system.models.Appointment;
import com.ronogar.appointment_system.models.Professional;
import com.ronogar.appointment_system.models.User;
import com.ronogar.appointment_system.repositories.AppointmentRepository;
import com.ronogar.appointment_system.repositories.ProfessionalRepository;
import com.ronogar.appointment_system.repositories.UserRepository;
import com.ronogar.appointment_system.services.appointment.AppointmentServiceImpl;
import static org.junit.jupiter.api.Assertions.*;

import com.ronogar.appointment_system.services.auth.CurrentUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.*;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceImplTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private ProfessionalRepository professionalRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    @Test
    void getAppointment_ReturnsAppointment_WhenAppointmentIsPresent() {

        Account account = new Account();
        account.setId(1L);
        account.setEmail("CoffeMan@gmail.com");


        User user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setAccount(account);

        Professional professional = new Professional();
        professional.setId(1L);
        professional.setAccount(account);

        Appointment appointment = new Appointment();
        appointment.setUser(user);
        appointment.setProfessional(professional);


        when(appointmentRepository.findAll()).thenReturn(List.of(appointment));

        List<AppointmentResponseDTO> result = appointmentService.getAppointments();

        assertEquals(1, result.size());
        assertEquals("John", result.getFirst().getUser().getFirstName());

    }

    @Test
    void getMyAppointments_returnsAppointments_whenPresent() {

        Account account = new Account();
        account.setEmail("coffeeman@gmail.com");

        User user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setAccount(account);

        Professional professional = new Professional();
        professional.setId(1L);
        professional.setAccount(account);

        Appointment appointment = new Appointment();
        appointment.setUser(user);
        appointment.setProfessional(professional);

        when(currentUserService.getAuthenticatedUser()).thenReturn(user);
        when(appointmentRepository.findByUserId(1L)).thenReturn(List.of(appointment));

        List<AppointmentResponseDTO> result = appointmentService.getMyAppointments();

        assertEquals(1, result.size());
    }

    @Test
    void getAppointmentProfessionals_returnsAppointments_whenProfessionalIsPresent() {

        Professional professional = new Professional();
        professional.setId(1L);

        Account account = new Account();
        account.setId(1L);
        account.setEmail("coffeeman@gmail.com");
        account.setProfessional(professional);
        professional.setAccount(account);

        User user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setAccount(account);

        Appointment appointment = new Appointment();
        appointment.setUser(user);
        appointment.setProfessional(professional);

        when(currentUserService.getAuthenticatedAccount()).thenReturn(account);
        when(appointmentRepository.findByProfessionalId(1L)).thenReturn(List.of(appointment));

        List<AppointmentResponseDTO> result = appointmentService.getAppointmentOfProfessionals();

        assertEquals(1, result.size());

    }

    @Test
    void getAppointmentProfessionals_ThrowsAccessDeniedException_WhenProfessionalIsNotPresent() {
        Account account = new Account();
        account.setId(1L);

        when(currentUserService.getAuthenticatedAccount()).thenReturn(account);

        assertThrows(AccessDeniedException.class
                , () -> appointmentService.getAppointmentOfProfessionals());
    }

    @Test
    void getAppointmentById_ReturnsAppointment_WhenAppointmentIsPresent() {

        Account account = new Account();
        account.setId(1L);

        Account account2 = new Account();
        account2.setId(2L);

        Professional professional = new Professional();
        professional.setId(2L);
        professional.setAccount(account2);

        User user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setAccount(account);

        Appointment appointment = new Appointment();
        appointment.setUser(user);
        appointment.setProfessional(professional);

        when(currentUserService.getAuthenticatedAccount()).thenReturn(account);
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        AppointmentResponseDTO result = appointmentService.getAppointmentById(1L);

        assertNotNull(result);
        assertEquals("John", result.getUser().getFirstName());

    }

    @Test
    void getAppointmentById_ThrowsResourceNotFoundException_WhenAppointmentIsNotPresent() {

        when(appointmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> appointmentService.getAppointmentById(1L));
    }

    @Test
    void getAppointmentById_ThrowsAccessDenied_WhenNeitherUserNorProfessional() {

        Account authAccount = new Account();
        authAccount.setId(1L);

        Account otherAccount1 = new Account();
        otherAccount1.setId(2L);

        Account otherAccount2 = new Account();
        otherAccount2.setId(3L);

        User user = new User();
        user.setId(1L);
        user.setAccount(otherAccount1);

        Professional professional = new Professional();
        professional.setId(1L);
        professional.setAccount(otherAccount2);

        Appointment appointment = new Appointment();
        appointment.setUser(user);
        appointment.setProfessional(professional);

        when(currentUserService.getAuthenticatedAccount()).thenReturn(authAccount);
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        assertThrows(AccessDeniedException.class,
                () -> appointmentService.getAppointmentById(1L));
    }


}
