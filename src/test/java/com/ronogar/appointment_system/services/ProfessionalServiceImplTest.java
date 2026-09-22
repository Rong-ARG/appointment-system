package com.ronogar.appointment_system.services;

import com.ronogar.appointment_system.dtos.professional.ProfessionalPatchDTO;
import com.ronogar.appointment_system.dtos.professional.ProfessionalRequestDTO;
import com.ronogar.appointment_system.dtos.professional.ProfessionalResponseDTO;
import com.ronogar.appointment_system.dtos.professional.ProfessionalSelfRequestDTO;
import com.ronogar.appointment_system.enums.Role;
import com.ronogar.appointment_system.exceptions.DuplicateResourceException;
import com.ronogar.appointment_system.exceptions.InvalidAccountStateException;
import com.ronogar.appointment_system.exceptions.ResourceNotFoundException;
import com.ronogar.appointment_system.models.Account;
import com.ronogar.appointment_system.models.Appointment;
import com.ronogar.appointment_system.models.Professional;
import com.ronogar.appointment_system.models.User;
import com.ronogar.appointment_system.repositories.AccountRepository;
import com.ronogar.appointment_system.repositories.AppointmentRepository;
import com.ronogar.appointment_system.repositories.ProfessionalRepository;
import com.ronogar.appointment_system.services.auth.CurrentUserService;
import com.ronogar.appointment_system.services.professional.ProfessionalServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProfessionalServiceImplTest {


    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ProfessionalRepository professionalRepository;

    @InjectMocks
    private ProfessionalServiceImpl professionalService;

    @Mock
    private CurrentUserService currentUserService;

    @Test
    void getAllProfessionals_returnsAllProfessionals_WhenAllProfessionalsExist() {

        Account account = new Account();
        account.setId(1L);
        account.setEmail("try@gmail.com");

        Professional professional = new Professional();
        professional.setId(1L);
        professional.setSpecialty("Welder");
        professional.setAccount(account);

        when(professionalRepository.findAll()).thenReturn(List.of(professional));

        List<ProfessionalResponseDTO> result = professionalService.getAllProfessionals();

        assertEquals(1, result.size());
        assertEquals("Welder", result.getFirst().getSpecialty());
    }

    @Test
    void getProfessionalById_returnsProfessionalById_WhenProfessionalExists() {
        Account account = new Account();
        account.setId(1L);

        Professional professional = new Professional();
        professional.setId(1L);
        professional.setAccount(account);

        when(professionalRepository.findById(1L)).thenReturn(Optional.of(professional));

        ProfessionalResponseDTO result = professionalService.getProfessionalById(1L);

        assertEquals(1L, result.getId());

    }

    @Test
    void getProfessionalById_ThrowsResourceNotFoundException_WhenProfessionalDoesNotExist() {

        when(professionalRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> professionalService.getProfessionalById(1L));
    }

    @Test
    void getProfessionalByEmail_returnsProfessionalByEmail_WhenProfessionalExists() {

        Account account = new Account();
        account.setId(1L);
        account.setEmail("looking4Job@gmail.com");

        Professional professional = new Professional();
        professional.setId(1L);
        professional.setAccount(account);

        when(professionalRepository.findByAccountEmail(account.getEmail())).thenReturn(Optional.of(professional));

        ProfessionalResponseDTO result = professionalService.getProfessionalByEmail(account.getEmail());

        assertEquals("looking4Job@gmail.com", result.getEmail());

    }

    @Test
    void getProfessionalByEmail_ThrowsResourceNotFoundException_WhenProfessionalDoesNotExist() {

        when(professionalRepository.findByAccountEmail("Something@gmail.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> professionalService.getProfessionalByEmail("Something@gmail.com"));
    }

    @Test
    void getProfessionalByLastName_returnsProfessionalByLastName_WhenProfessionalExists() {
        Account account = new Account();
        account.setId(1L);

        Professional professional = new Professional();
        professional.setId(1L);
        professional.setAccount(account);
        professional.setLastName("Smith");

        when(professionalRepository.findByLastName("Smith")).thenReturn(List.of(professional));

        List<ProfessionalResponseDTO> result = professionalService.getProfessionalByLastName("Smith");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Smith", result.getFirst().getLastName());
    }

    @Test
    void getProfessionalByLastName_ThrowsResourceNotFoundException_WhenProfessionalDoesNotExist() {

        when(professionalRepository.findByLastName("Smith")).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class,
                () -> professionalService.getProfessionalByLastName("Smith"));
    }

    @Test
    void getProfessionalBySpecialty_returnsProfessionalBySpecialty_WhenAProfessionalHaveThatSpecialty() {

        Account account = new Account();
        account.setId(1L);

        Professional professional = new Professional();
        professional.setId(1L);
        professional.setAccount(account);
        professional.setSpecialty("Welder");

        when(professionalRepository.findBySpecialty("Welder")).thenReturn(List.of(professional));

        List<ProfessionalResponseDTO> result = professionalService.getProfessionalsBySpecialty("Welder");

        assertNotNull(result);
        assertEquals("Welder", result.getFirst().getSpecialty());

    }

    @Test
    void getProfessionalBySpecialty_ThrowsResourceNotFoundException_WhenSpecialtyDoesNotExist() {
        when(professionalRepository.findBySpecialty("Welder")).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class,
                () -> professionalService.getProfessionalsBySpecialty("Welder"));
    }

    @Test
    void createProfessional_returnsCreatedProfessional_WhenCreatedSuccessfully() {

        ProfessionalRequestDTO professionalRequestDTO = new ProfessionalRequestDTO();
        professionalRequestDTO.setFirstName("Will");
        professionalRequestDTO.setLastName("Smith");
        professionalRequestDTO.setEmail("Something@gmail.com");

        User user = new User();

        Account account = new Account();
        account.setId(1L);
        account.setEmail("Something@gmail.com");
        account.setUser(user);
        account.setRoles(new HashSet<>(Set.of(Role.USER)));

        when(accountRepository.findByEmail("Something@gmail.com")).thenReturn(Optional.of(account));
        when(professionalRepository.save(any(Professional.class))).then(i -> i.getArgument(0));
        when(accountRepository.save(any(Account.class))).then(i -> i.getArgument(0));

        ProfessionalResponseDTO result = professionalService.createProfessional(professionalRequestDTO);

        assertNotNull(result);
    }

    @Test
    void createProfessional_ThrowsResourceNotFoundException_WhenAccountDoesNotExist() {

        ProfessionalRequestDTO professionalRequestDTO = new ProfessionalRequestDTO();
        professionalRequestDTO.setEmail("Something@gmail.com");

        when(accountRepository.findByEmail("Something@gmail.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> professionalService.createProfessional(professionalRequestDTO));
    }

    @Test
    void createProfessional_ThrowsInvalidAccountStateException_WhenAccountNotHaveUser() {

        ProfessionalRequestDTO professionalRequestDTO = new ProfessionalRequestDTO();
        professionalRequestDTO.setEmail("Something@gmail.com");

        Account account = new Account();
        account.setId(1L);

        when(accountRepository.findByEmail("Something@gmail.com")).thenReturn(Optional.of(account));

        assertThrows(InvalidAccountStateException.class,
                () -> professionalService.createProfessional(professionalRequestDTO));

    }

    @Test
    void createOwnProfessionalProfile_returnsCreated_whenSuccessful() {

        ProfessionalSelfRequestDTO professionalSelfRequestDTO = new ProfessionalSelfRequestDTO();

        User user = new User();

        Account account = new Account();
        account.setId(1L);
        account.setEmail("something@gmail.com");
        account.setUser(user);
        account.setRoles(new HashSet<>(Set.of(Role.USER)));

        when(accountRepository.findByEmail("something@gmail.com")).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));
        when(professionalRepository.save(any(Professional.class))).thenAnswer(i -> i.getArgument(0));

        ProfessionalResponseDTO result = professionalService.createOwnProfessionalProfile("something@gmail.com", professionalSelfRequestDTO);

        assertNotNull(result);
    }

    @Test
    void createOwnProfessionalProfile_ThrowsResourceNotFoundException_WhenAccountNotExist() {

        ProfessionalSelfRequestDTO professionalSelfRequestDTO = new ProfessionalSelfRequestDTO();

        when(accountRepository.findByEmail("Something@gmail.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> professionalService.createOwnProfessionalProfile("Something@gmail.com", professionalSelfRequestDTO));
    }

    @Test
    void createOwnProfessionalProfile_ThrowsInvalidAccountStateException_WhenAccountNotHaveUser() {

        ProfessionalSelfRequestDTO professionalSelfRequestDTO = new ProfessionalSelfRequestDTO();

        Account account = new Account();
        account.setId(1L);
        account.setEmail("Something@gmail.com");

        when(accountRepository.findByEmail("Something@gmail.com")).thenReturn(Optional.of(account));

        assertThrows(InvalidAccountStateException.class,
                () -> professionalService.createOwnProfessionalProfile("Something@gmail.com", professionalSelfRequestDTO));

    }

    @Test
    void createOwnProfessionalProfile_ThrowsDuplicateAccountStateException_WhenAccountAlreadyHaveAProfessional() {

        ProfessionalSelfRequestDTO professionalSelfRequestDTO = new ProfessionalSelfRequestDTO();

        Professional professional = new Professional();
        professional.setId(1L);

        User user = new User();

        Account account = new Account();
        account.setId(1L);
        account.setEmail("Something@gmail.com");
        account.setProfessional(professional);
        account.setUser(user);
        account.setRoles(new HashSet<>(Set.of(Role.USER)));

        when(accountRepository.findByEmail("Something@gmail.com")).thenReturn(Optional.of(account));

        assertThrows(DuplicateResourceException.class,
                () -> professionalService.createOwnProfessionalProfile("Something@gmail.com", professionalSelfRequestDTO));

    }

    @Test
    void updateProfessional_updatesFields_whenSuccessful() {

        ProfessionalRequestDTO professionalRequestDTO = new ProfessionalRequestDTO();
        professionalRequestDTO.setEmail("SomethingElse@gmail.com");
        professionalRequestDTO.setFirstName("Juan");
        professionalRequestDTO.setLastName("Torres");
        professionalRequestDTO.setPassword("1234");
        professionalRequestDTO.setPhone("61654652");

        Account account = new Account();
        account.setId(1L);
        account.setEmail("Something@gmail.com");


        Professional professional = new Professional();
        professional.setId(1L);
        professional.setAccount(account);


        when(professionalRepository.findById(1L)).thenReturn(Optional.of(professional));
        when(currentUserService.getAuthenticatedAccount()).thenReturn(account);
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));
        when(professionalRepository.save(any(Professional.class))).thenAnswer(i -> i.getArgument(0));
        when(passwordEncoder.encode("1234")).thenReturn("12345");

        professionalService.updateProfessional(1L, professionalRequestDTO);

        assertEquals("Juan", professional.getFirstName());
        assertEquals("Torres", professional.getLastName());
        assertEquals("12345", account.getPassword());
        assertEquals("SomethingElse@gmail.com", account.getEmail());
        verify(accountRepository).save(account);
        verify(professionalRepository).save(professional);
    }

    @Test
    void updateProfessional_ThrowsResourceNotFoundException_WhenProfessionalNotFound() {

        when(professionalRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> professionalService.updateProfessional(1L, new ProfessionalRequestDTO()));
    }

    @Test
    void updateProfessional_ThrowsAccessDenied_WhenAccountsNotMatching() {

        Account account = new Account();
        account.setId(1L);
        account.setEmail("Something@gmail.com");

        Professional professional = new Professional();
        professional.setId(1L);
        professional.setAccount(account);


        Account accountAuth = new Account();
        accountAuth.setId(2L);
        accountAuth.setEmail("SomethingElse@gmail.com");

        when(professionalRepository.findById(1L)).thenReturn(Optional.of(professional));
        when(currentUserService.getAuthenticatedAccount()).thenReturn(accountAuth);

        assertThrows(AccessDeniedException.class,
                () -> professionalService.updateProfessional(1L, new ProfessionalRequestDTO()));
    }

    @Test
    void deleteProfessional_deletesAccount_whenSuccessful() {

        Account account = new Account();
        account.setId(1L);
        account.setEmail("Something@gmail.com");

        Professional professional = new Professional();
        professional.setId(1L);
        professional.setAccount(account);

        when(professionalRepository.findById(1L)).thenReturn(Optional.of(professional));
        when(currentUserService.getAuthenticatedAccount()).thenReturn(account);
        when(appointmentRepository.findByProfessionalId(1L)).thenReturn(List.of());

        professionalService.deleteProfessional(1L);

        verify(professionalRepository).deleteById(1L);
        verify(accountRepository).delete(account);

    }

    @Test
    void deleteProfessional_ThrowsResourceNotFoundException_WhenProfessionalNotFound() {

        when(professionalRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> professionalService.deleteProfessional(1L));
    }

    @Test
    void deleteProfessional_ThrowsAccessDenied_WhenAccountsNotMatching() {

        Account accountAuth = new Account();
        accountAuth.setId(2L);

        Account account = new Account();
        account.setId(1L);

        Professional professional = new Professional();
        professional.setId(1L);
        professional.setAccount(account);

        when(professionalRepository.findById(1L)).thenReturn(Optional.of(professional));
        when(currentUserService.getAuthenticatedAccount()).thenReturn(accountAuth);

        assertThrows(AccessDeniedException.class,
                () -> professionalService.deleteProfessional(1L));
    }

    @Test
    void deleteProfessional_ThrowsInvalidAccountStateException_WhenAccountHaveAppointment() {
        Account account = new Account();
        account.setId(1L);

        Account accountAuth = new Account();
        accountAuth.setId(1L);

        Professional professional = new Professional();
        professional.setId(1L);
        professional.setAccount(account);

        when(professionalRepository.findById(1L)).thenReturn(Optional.of(professional));
        when(currentUserService.getAuthenticatedAccount()).thenReturn(accountAuth);
        when(appointmentRepository.findByProfessionalId(1L)).thenReturn(List.of(new Appointment()));

        assertThrows(InvalidAccountStateException.class,
                () -> professionalService.deleteProfessional(1L));
    }

    @Test
    void deleteProfessional_RemoveUserRole_WhenSuccessfullyDeleted() {

        Account account = new Account();
        account.setId(1L);
        account.setUser(new User());
        account.setRoles(new HashSet<>(Set.of(Role.PROFESSIONAL, Role.USER)));

        Account accountAuth = new Account();
        accountAuth.setId(1L);

        Professional professional = new Professional();
        professional.setId(1L);
        professional.setAccount(account);

        when(professionalRepository.findById(1L)).thenReturn(Optional.of(professional));
        when(currentUserService.getAuthenticatedAccount()).thenReturn(accountAuth);
        when(appointmentRepository.findByProfessionalId(1L)).thenReturn(List.of());
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));

        professionalService.deleteProfessional(1L);

        assertTrue(account.getRoles().contains(Role.USER));
        assertFalse(account.getRoles().contains(Role.PROFESSIONAL));
        assertNull(account.getProfessional());
        verify(accountRepository).save(account);
    }

    @Test
    void patchProfessional_ReturnsPatchedProfessional_WhenSuccessfullyPatched() {

        ProfessionalPatchDTO professionalPatchDTO = new ProfessionalPatchDTO();
        professionalPatchDTO.setPhone("6516548");
        professionalPatchDTO.setFirstName("John");
        professionalPatchDTO.setLastName("Cena");
        professionalPatchDTO.setSpecialty("coffee man");

        Professional professional = new Professional();
        professional.setId(1L);

        Account account = new Account();
        account.setId(1L);
        professional.setAccount(account);

        when(professionalRepository.findById(1L)).thenReturn(Optional.of(professional));
        when(currentUserService.getAuthenticatedAccount()).thenReturn(account);
        when(professionalRepository.save(any(Professional.class))).thenAnswer(i -> i.getArgument(0));

        professionalService.patchProfessional(1L, professionalPatchDTO);

        assertEquals(professionalPatchDTO.getPhone(), professional.getPhone());
        assertEquals("John", professional.getFirstName());
        assertEquals("Cena", professional.getLastName());
        assertEquals("coffee man", professional.getSpecialty());

    }

    @Test
    void patchProfessional_ThrowsResourceNotFoundException_WhenProfessionalNotFound() {

        when(professionalRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> professionalService.patchProfessional(1L, new ProfessionalPatchDTO()));
    }

    @Test
    void patchProfessional_ThrowsAccessDenied_WhenAccountsNotMatching() {
        Account account = new Account();
        account.setId(1L);
        Account accountAuth = new Account();
        accountAuth.setId(2L);
        Professional professional = new Professional();
        professional.setId(1L);
        professional.setAccount(account);

        when(professionalRepository.findById(1L)).thenReturn(Optional.of(professional));
        when(currentUserService.getAuthenticatedAccount()).thenReturn(accountAuth);

        assertThrows(AccessDeniedException.class,
                () -> professionalService.patchProfessional(1L, new ProfessionalPatchDTO()));
    }

    @Test
    void patchProfessional_ReturnsPatchedEmail_WhenSuccessfullyPatched() {

        ProfessionalPatchDTO professionalPatchDTO = new ProfessionalPatchDTO();
        professionalPatchDTO.setEmail("NeedACoffe@gmail.com");

        Account account = new Account();
        account.setId(1L);

        Professional professional = new Professional();
        professional.setId(1L);
        professional.setAccount(account);


        when(professionalRepository.findById(1L)).thenReturn(Optional.of(professional));
        when(currentUserService.getAuthenticatedAccount()).thenReturn(account);
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));

        professionalService.patchProfessional(1L, professionalPatchDTO);

        verify(accountRepository).save(account);
        assertEquals("NeedACoffe@gmail.com", account.getEmail());

    }
}
