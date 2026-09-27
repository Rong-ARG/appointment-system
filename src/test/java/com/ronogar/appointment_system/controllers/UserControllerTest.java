package com.ronogar.appointment_system.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ronogar.appointment_system.config.CustomUserDetailsService;
import com.ronogar.appointment_system.config.SecurityConfig;
import com.ronogar.appointment_system.dtos.user.UserRequestDTO;
import com.ronogar.appointment_system.dtos.user.UserResponseDTO;
import com.ronogar.appointment_system.services.auth.JwtService;
import com.ronogar.appointment_system.services.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
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

    @Test
    @WithMockUser
    void getUserById_validId_returns200() throws Exception {

        Long userId = 1L;

        UserResponseDTO userResponseDTO = new UserResponseDTO(1L, "Juan", "Bartolini", "juan@gmail.com", "123521");

        when(userService.getUserById(userId)).thenReturn(userResponseDTO);

        mockMvc.perform(get("/api/users/{id}", userId).with(user("testUser")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId));

    }

    @Test
    void getAllUsers_asAdmin_returns200() throws Exception {
        List<UserResponseDTO> userResponseDTOList = List.of(
                new UserResponseDTO(1L, "Juan", "Bartolini", "juan@gmail.com", "123521")
        );

        when(userService.getAllUsers()).thenReturn(userResponseDTOList);

        mockMvc.perform(get("/api/users").with(user("adminUser").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("juan@gmail.com"));
    }

    @Test
    void getAllUsers_asUser_returns403() throws Exception {

        mockMvc.perform(get("/api/users").with(user("testUser").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void getUsersByEmail_validEmail_returns200() throws Exception {

        String email = "Coffeman@gmail.com";
        UserResponseDTO userResponseDTO = new UserResponseDTO(1L,"Juan","Rodriguez", "Coffeman@gmail.com", "1212");

        when(userService.getUserByEmail(email)).thenReturn(userResponseDTO);

        mockMvc.perform(get("/api/users/email/{email}", email).with(user("testUser").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    void getUsersByLastName_validLastName_returns200() throws Exception {
        String lastName = "Rodriguez";
        List<UserResponseDTO> userResponseDTOList = List.of(
                new UserResponseDTO(1L, "Juan", "Rodriguez", "juan@gmail.com", "1212")
        );

        when(userService.getUserByLastName(lastName)).thenReturn(userResponseDTOList);

        mockMvc.perform(get("/api/users/lastname/{lastName}", lastName).with(user("testUser").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].lastName").value(lastName));
    }

}