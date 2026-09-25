package com.ronogar.appointment_system.services;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.ronogar.appointment_system.exceptions.TokenInvalidException;
import com.ronogar.appointment_system.services.auth.JwtService;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

public class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "privateKey", "test-secret-key-12345");
        ReflectionTestUtils.setField(jwtService, "userGenerator", "appointment-system");
        ReflectionTestUtils.setField(jwtService, "expirationTime", 3600000L);
    }

    @Test
    void createToken_validAuthentication_returnsNonEmptyToken() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user@test.com");

        String token = jwtService.createToken(authentication);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void validateToken_validToken_returnsDecodedJwtWithCorrectSubject() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user@test.com");
        String token = jwtService.createToken(authentication);

        DecodedJWT decodedJWT = jwtService.validateToken(token);

        assertEquals("user@test.com", decodedJWT.getSubject());
    }

    @Test
    void validateToken_invalidToken_throwsTokenInvalidException() {
        String fakeToken = "this.is-not.a-valid-token";

        assertThrows(TokenInvalidException.class, () -> jwtService.validateToken(fakeToken));
    }

    @Test
    void validateToken_tokenWithDifferentIssuer_throwsTokenInvalidException() {
        JwtService otherIssuerJwtService = new JwtService();
        ReflectionTestUtils.setField(otherIssuerJwtService, "privateKey", "test-secret-key-12345");
        ReflectionTestUtils.setField(otherIssuerJwtService, "userGenerator", "different-issuer");
        ReflectionTestUtils.setField(otherIssuerJwtService, "expirationTime", 3600000L);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user@test.com");
        String tokenWithDifferentIssuer = otherIssuerJwtService.createToken(authentication);

        assertThrows(TokenInvalidException.class, () -> jwtService.validateToken(tokenWithDifferentIssuer));
    }
}