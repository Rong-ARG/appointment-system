package com.ronogar.appointment_system.services.auth;

import com.ronogar.appointment_system.models.Account;
import com.ronogar.appointment_system.models.User;
import com.ronogar.appointment_system.repositories.AccountRepository;
import com.ronogar.appointment_system.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    public User getAuthenticatedUser() {

        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return userRepository.findByAccountEmail(userDetails.getUsername())
                .orElseThrow(() -> new AccessDeniedException("User with email " + userDetails.getUsername() + " not found"));
    }

    public Account getAuthenticatedAccount() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return accountRepository.findByEmail(userDetails.getUsername()).orElseThrow(()-> new AccessDeniedException("Account not found"));
    }
}
