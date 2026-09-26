package com.ronogar.appointment_system.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ronogar.appointment_system.config.CustomUserDetailsService;
import com.ronogar.appointment_system.dtos.auth.AuthRequestDTO;
import com.ronogar.appointment_system.dtos.auth.AuthResponseDTO;
import com.ronogar.appointment_system.services.auth.AuthService;
import com.ronogar.appointment_system.services.auth.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void login_validRequest_returns200AndToken() throws Exception {

        AuthRequestDTO authRequestDTO= new AuthRequestDTO();
        authRequestDTO.setEmail("Something@gmail.com");
        authRequestDTO.setPassword("1234");

        ObjectMapper mapper = new ObjectMapper();
        String jsonBody = mapper.writeValueAsString(authRequestDTO);

        when(authService.login(any(AuthRequestDTO.class))).thenReturn(new AuthResponseDTO("coffe-jwt-key"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("coffe-jwt-key"));

    }

    @Test
    void login_invalidRequest_returns401() throws Exception {
        AuthRequestDTO authRequestDTO= new AuthRequestDTO();
        authRequestDTO.setEmail("looking4Job@gmail.com");
        authRequestDTO.setPassword("1234");

        ObjectMapper mapper = new ObjectMapper();
        String jsonBody = mapper.writeValueAsString(authRequestDTO);

        when(authService.login(any(AuthRequestDTO.class))).thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isUnauthorized());

    }
}
