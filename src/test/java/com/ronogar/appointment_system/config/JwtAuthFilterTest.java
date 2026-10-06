package com.ronogar.appointment_system.config;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.ronogar.appointment_system.exceptions.TokenInvalidException;
import com.ronogar.appointment_system.services.auth.JwtService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtAuthFilterTest {

    @Mock
    private FilterChain filterChain;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private JwtAuthFilter jwtAuthFilter;

    @AfterEach
    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilter_noAuthorizationHeader_continuesWithoutAuthentication() throws Exception {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        MockHttpServletResponse mockResponse = new MockHttpServletResponse();

        jwtAuthFilter.doFilter(mockRequest, mockResponse, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(mockRequest, mockResponse);
        verifyNoInteractions(jwtService);
    }

    @Test
    void doFilter_headerWithoutBearerPrefix_continuesWithoutAuthentication() throws Exception {

        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        MockHttpServletResponse mockResponse = new MockHttpServletResponse();

        mockRequest.addHeader("Authorization","SomeToken 90213nuoisah8");
        jwtAuthFilter.doFilter(mockRequest, mockResponse, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(mockRequest, mockResponse);
        verifyNoInteractions(jwtService);
    }

    @Test
    void doFilter_validToken_authenticatesUser()  throws Exception {

        String token = "valid.jwt.token";
        String email = "Martin@gmail.com";

        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        MockHttpServletResponse mockResponse = new MockHttpServletResponse();
        mockRequest.addHeader("Authorization" , "Bearer " + token);

        DecodedJWT decodedJWT = mock(DecodedJWT.class);
        when(decodedJWT.getSubject()).thenReturn(email);

        UserDetails userDetails = User.withUsername(email)
                .password("password")
                .roles("USER")
                .build();

        when(jwtService.validateToken(token)).thenReturn(decodedJWT);
        when(customUserDetailsService.loadUserByUsername(email)).thenReturn(userDetails);

        jwtAuthFilter.doFilter(mockRequest, mockResponse, filterChain);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(authentication);
        assertSame(userDetails, authentication.getPrincipal());
        verify(filterChain).doFilter(mockRequest, mockResponse);
    }

    @Test
    void doFilter_invalidToken_clearsContextAndContinues() throws Exception {
        String token = "invalid.jwt.token";

        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        MockHttpServletResponse mockResponse = new MockHttpServletResponse();
        mockRequest.addHeader("Authorization", "Bearer " + token);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("someone", null, List.of()));

        when(jwtService.validateToken(token)).thenThrow(new TokenInvalidException("Token is invalid"));

        jwtAuthFilter.doFilter(mockRequest, mockResponse, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(mockRequest, mockResponse);
        verifyNoInteractions(customUserDetailsService);
    }

    @Test
    void doFilter_validTokenButUserNotFound_clearsContextAndContinues() throws Exception {
        String token = "valid.jwt.token";
        String email = "Deleted@gmail.com";

        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        MockHttpServletResponse mockResponse = new MockHttpServletResponse();
        mockRequest.addHeader("Authorization", "Bearer " + token);

        DecodedJWT decodedJWT = mock(DecodedJWT.class);
        when(decodedJWT.getSubject()).thenReturn(email);

        when(jwtService.validateToken(token)).thenReturn(decodedJWT);
        when(customUserDetailsService.loadUserByUsername(email))
                .thenThrow(new UsernameNotFoundException("User not found"));

        jwtAuthFilter.doFilter(mockRequest, mockResponse, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(mockRequest, mockResponse);
    }
}
