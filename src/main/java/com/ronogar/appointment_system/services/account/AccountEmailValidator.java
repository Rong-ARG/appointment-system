package com.ronogar.appointment_system.services.account;

import com.ronogar.appointment_system.exceptions.DuplicateResourceException;
import com.ronogar.appointment_system.models.Account;
import com.ronogar.appointment_system.repositories.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountEmailValidator {

    private final AccountRepository accountRepository;

    public void verifyAvailable(String email, Account currentAccount) {
        accountRepository.findByEmail(email)
                .filter(found -> !found.getId().equals(currentAccount.getId()))
                .ifPresent(found -> {
                    throw new DuplicateResourceException("Email " + email + " is already in use");
                });
    }
}