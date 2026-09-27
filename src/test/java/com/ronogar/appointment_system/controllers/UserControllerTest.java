package com.ronogar.appointment_system.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ronogar.appointment_system.config.CustomUserDetailsService;
import com.ronogar.appointment_system.dtos.user.UserRequestDTO;
import com.ronogar.appointment_system.dtos.user.UserResponseDTO;
import com.ronogar.appointment_system.services.auth.JwtService;
import com.ronogar.appointment_system.services.user.UserService;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createUser_validRequest_returns201() throws Exception {
        UserRequestDTO userRequestDTO = new UserRequestDTO(
                "Juan", "Bartolini", "1234"
                , "JuanBartolini@gmail.com", "123521");

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(userRequestDTO);

        when(userService.createUser(any(UserRequestDTO.class))).thenReturn(new UserResponseDTO(
                1L, "Juan", "Bartolini", "JuanBartolini@gmail.com", "123521"));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("JuanBartolini@gmail.com"));
    }

    @Test
    void createUser_invalidRequest_returns400() throws Exception {
        UserRequestDTO userRequestDTO = new UserRequestDTO(
                "Juan", "Bartolini", "1234"
                , "JuanBartolini", "123521");

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(userRequestDTO);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }
}