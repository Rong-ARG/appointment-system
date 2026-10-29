package com.ronogar.appointment_system.config;

import com.ronogar.appointment_system.enums.Role;
import com.ronogar.appointment_system.models.Account;
import com.ronogar.appointment_system.repositories.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private CustomUserDetailsService service;

    @Test
    void loadUserByUsername_existingEmail_returnsUserDetailsWithRolePrefix() {
        Account account = new Account();
        account.setEmail("a@test.com");
        account.setPassword("hashed");
        account.setRoles(Set.of(Role.USER, Role.PROFESSIONAL));
        when(accountRepository.findByEmail("a@test.com")).thenReturn(Optional.of(account));

        UserDetails result = service.loadUserByUsername("a@test.com");

        assertEquals("a@test.com", result.getUsername());
        assertEquals("hashed", result.getPassword());
        assertEquals(Set.of("ROLE_USER", "ROLE_PROFESSIONAL"),
                result.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                        .collect(java.util.stream.Collectors.toSet()));
    }

    @Test
    void loadUserByUsername_unknownEmail_throwsUsernameNotFound() {
        when(accountRepository.findByEmail("x@test.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("x@test.com"));
    }
}