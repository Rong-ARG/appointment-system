package com.ronogar.appointment_system.services;

import com.ronogar.appointment_system.models.Account;
import com.ronogar.appointment_system.models.User;
import com.ronogar.appointment_system.repositories.AccountRepository;
import com.ronogar.appointment_system.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import com.ronogar.appointment_system.services.auth.CurrentUserService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CurrentUserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private AccountRepository accountRepository;
    @InjectMocks
    private CurrentUserService currentUserService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private void authenticateAs(String email) {
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(email)
                .password("password")
                .roles("USER")
                .build();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
        );
    }

    @Test
    void getAuthenticatedUser_existingUser_returnsUser() {
        String email = "Martin@gmail.com";
        authenticateAs(email);

        User user = new User();
        user.setId(1L);

        when(userRepository.findByAccountEmail(email)).thenReturn(Optional.of(user));

        User result = currentUserService.getAuthenticatedUser();

        assertSame(user, result);
    }

    @Test
    void getAuthenticatedUser_noProfile_throwsAccessDenied() {
        String email = "Martin@gmail.com";
        authenticateAs(email);

        when(userRepository.findByAccountEmail(email)).thenReturn(Optional.empty());

        assertThrows(AccessDeniedException.class,
                () -> currentUserService.getAuthenticatedUser());
    }

    @Test
    void getAuthenticatedAccount_existingAccount_returnsAccount() {
        String email = "Martin@gmail.com";
        authenticateAs(email);

        Account account = new Account();
        account.setId(1L);

        when(accountRepository.findByEmail(email)).thenReturn(Optional.of(account));

        Account result = currentUserService.getAuthenticatedAccount();

        assertSame(account, result);
    }

    @Test
    void getAuthenticatedAccount_accountNotFound_throwsAccessDenied() {
        String email = "Martin@gmail.com";
        authenticateAs(email);

        when(accountRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(AccessDeniedException.class,
                () -> currentUserService.getAuthenticatedAccount());
    }
}
