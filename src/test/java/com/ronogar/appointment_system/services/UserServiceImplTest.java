package com.ronogar.appointment_system.services;

import com.ronogar.appointment_system.dtos.user.UserResponseDTO;
import com.ronogar.appointment_system.models.Account;
import com.ronogar.appointment_system.models.User;
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

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private UserServiceImpl userService;

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
}
