package com.ronogar.appointment_system.services;

import com.ronogar.appointment_system.exceptions.DuplicateResourceException;
import com.ronogar.appointment_system.models.Account;
import com.ronogar.appointment_system.repositories.AccountRepository;
import com.ronogar.appointment_system.services.account.AccountEmailValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountEmailValidatorTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountEmailValidator accountEmailValidator;

    @Test
    void verifyAvailable_throwsDuplicateResourceException_WhenEmailBelongsToAnotherAccount() {
        Account current = new Account();
        current.setId(1L);
        Account other = new Account();
        other.setId(2L);

        when(accountRepository.findByEmail("taken@gmail.com")).thenReturn(Optional.of(other));

        assertThrows(DuplicateResourceException.class,
                () -> accountEmailValidator.verifyAvailable("taken@gmail.com", current));
    }

    @Test
    void verifyAvailable_doesNotThrow_WhenEmailIsTheCurrentAccounts() {
        Account current = new Account();
        current.setId(1L);

        when(accountRepository.findByEmail("mine@gmail.com")).thenReturn(Optional.of(current));

        assertDoesNotThrow(() -> accountEmailValidator.verifyAvailable("mine@gmail.com", current));
    }

    @Test
    void verifyAvailable_doesNotThrow_WhenEmailIsFree() {
        Account current = new Account();
        current.setId(1L);

        when(accountRepository.findByEmail("free@gmail.com")).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> accountEmailValidator.verifyAvailable("free@gmail.com", current));
    }
}