package com.ronogar.appointment_system.services;

import com.ronogar.appointment_system.dtos.user.UserResponseDTO;
import com.ronogar.appointment_system.enums.Role;
import com.ronogar.appointment_system.exceptions.ResourceNotFoundException;
import com.ronogar.appointment_system.models.Account;
import com.ronogar.appointment_system.models.User;
import com.ronogar.appointment_system.repositories.AccountRepository;
import com.ronogar.appointment_system.repositories.UserRepository;
import com.ronogar.appointment_system.services.auth.CurrentUserService;
import com.ronogar.appointment_system.services.user.UserService;
import com.ronogar.appointment_system.services.user.UserServiceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void getAllUsers() {

        Account account = new Account();
        account.setEmail("iwannajob@gmail.com");


        User user = new User();
        user.setFirstName("Ariel");
        user.setAccount(account);

        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserResponseDTO> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals("Ariel", result.get(0).getFirstName());
    }

    @Test
    public void getAllUsers_returnsEmptyList_whenNoUsersExist() {

        when(userRepository.findAll()).thenReturn(List.of());

        List<UserResponseDTO> result = userService.getAllUsers();

        assertEquals(0, result.size());
    }

    @Test
    public void getUserById_returnsUser_WhenIsOwnProfile() {

        Account account = new Account();
        account.setEmail("Iwannajob@gmail.com");

        User user = new User();
        user.setId(1L);
        user.setFirstName("Juan");
        user.setAccount(account);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(currentUserService.getAuthenticatedUser()).thenReturn(user);

        Account authAccount = new Account();
        authAccount.setId(2L);
        when(currentUserService.getAuthenticatedAccount()).thenReturn(authAccount);


        UserResponseDTO result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals("Juan", result.getFirstName());
        assertEquals("Iwannajob@gmail.com", result.getEmail());

    }

    @Test
    void getUserById_throwsAccessDenied_whenNotOwnProfile() {
        User user = new User();
        user.setId(1L);

        User userdata = new User();
        userdata.setId(2L);

        Account account = new Account();
        account.setRoles(Set.of(Role.USER));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(currentUserService.getAuthenticatedUser()).thenReturn(userdata);
        when(currentUserService.getAuthenticatedAccount()).thenReturn(account);

        assertThrows(AccessDeniedException.class, () -> userService.getUserById(1L));
    }

    @Test
    public void getUserById_returnsUser_WhenIsAdminViewOtherProfile() {
        Account targetAccount = new Account();
        targetAccount.setEmail("Marcos@gmail.com");

        User user = new User();
        user.setId(1L);
        user.setFirstName("Juan");
        user.setAccount(targetAccount);

        User userdata = new User();
        userdata.setId(2L);

        Account account = new Account();
        account.setRoles(Set.of(Role.ADMIN));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(currentUserService.getAuthenticatedUser()).thenReturn(userdata);
        when(currentUserService.getAuthenticatedAccount()).thenReturn(account);

        UserResponseDTO result = userService.getUserById(1L);
        assertNotNull(result);
        assertEquals("Juan", result.getFirstName());

    }

    @Test
    public void getUserByEmail_returnsUser_WhenIsSearchUser() {
        Account account = new Account();
        account.setEmail("Iwannajob@gmail.c");

        User user = new User();
        user.setAccount(account);

        when(userRepository.findByAccountEmail("Iwannajob@gmail.c")).thenReturn(Optional.of(user));

        UserResponseDTO result = userService.getUserByEmail("Iwannajob@gmail.c");

        assertNotNull(result);
        assertEquals("Iwannajob@gmail.c", result.getEmail());
    }

    @Test
    public void getUserByEmail_throwsResourceNotFound_whenEmailNotFound() {

        when(userRepository.findByAccountEmail("Iwannajob@gmail.c")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserByEmail("Iwannajob@gmail.c"));
    }

    @Test
    public void getUserByLastname_returnsUser_WhenIsSearchUser() {

        Account account = new Account();
        account.setEmail("Iwannajob@gmail.c");

        User user = new User();
        user.setLastName("llanos");
        user.setAccount(account);

        when(userRepository.findByLastName("llanos")).thenReturn(List.of(user));

        List<UserResponseDTO> result = userService.getUserByLastName("llanos");

        assertNotNull(result);
        assertEquals("llanos", result.get(0).getLastName());


    }
}


