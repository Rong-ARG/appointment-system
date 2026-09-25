package com.ronogar.appointment_system.services;

import com.ronogar.appointment_system.dtos.auth.AuthRequestDTO;
import com.ronogar.appointment_system.dtos.auth.AuthResponseDTO;
import com.ronogar.appointment_system.services.auth.AuthService;
import com.ronogar.appointment_system.services.auth.JwtService;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;


@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void login_validCredentials_returnsToken() {
        AuthRequestDTO authRequestDTO = new AuthRequestDTO();
        authRequestDTO.setEmail("something@gmail.com");
        authRequestDTO.setPassword("12345");

        Authentication authentication = mock(Authentication.class);
        String expectedToken = "coffe-jwt-token";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(jwtService.createToken(authentication)).thenReturn(expectedToken);

        AuthResponseDTO response = authService.login(authRequestDTO);

        assertNotNull(response);
        assertEquals(expectedToken, response.getToken());

    }

    @Test
    void login_invalidCredentials_throwsBadCredentialsException(){

        AuthRequestDTO authRequestDTO = new AuthRequestDTO();
        authRequestDTO.setEmail("something@gmail.com");
        authRequestDTO.setPassword("12345");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> authService.login(authRequestDTO));
    }
}