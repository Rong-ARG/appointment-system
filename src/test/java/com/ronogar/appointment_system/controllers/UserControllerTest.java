package com.ronogar.appointment_system.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ronogar.appointment_system.config.CustomUserDetailsService;
import com.ronogar.appointment_system.config.SecurityConfig;
import com.ronogar.appointment_system.dtos.user.UserPatchDTO;
import com.ronogar.appointment_system.dtos.user.UserRequestDTO;
import com.ronogar.appointment_system.dtos.user.UserResponseDTO;
import com.ronogar.appointment_system.exceptions.ResourceNotFoundException;
import com.ronogar.appointment_system.services.auth.JwtService;
import com.ronogar.appointment_system.services.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.ronogar.appointment_system.testUtil.TestDataFactory.*;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void createUser_validRequest_returns201() throws Exception {

        UserResponseDTO responseDTO = userResponse();
        String email= responseDTO.getEmail();

        String json = objectMapper.writeValueAsString(userRequest());

        when(userService.createUser(any(UserRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    void createUser_invalidRequest_returns400() throws Exception {

        UserRequestDTO userRequestDTO = userRequest();
        userRequestDTO.setEmail("");

        String json = objectMapper.writeValueAsString(userRequestDTO);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserById_validId_returns200() throws Exception {

        UserResponseDTO responseDTO = userResponse();
        Long userId = responseDTO.getId();

        when(userService.getUserById(userId)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/users/{id}", userId).with(user("testUser")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId));

    }

    @Test
    void getUserById_invalidId_returns404() throws Exception {
        Long userId = 100L;
        when(userService.getUserById(userId)).thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(get("/api/users/{id}", userId)
                .with(user("testUser").roles("USER")))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllUsers_asAdmin_returns200() throws Exception {

        List<UserResponseDTO> userResponseDTOList = List.of(userResponse());

        when(userService.getAllUsers()).thenReturn(userResponseDTOList);

        mockMvc.perform(get("/api/users").with(user("adminUser").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value(userResponseDTOList.getFirst().getEmail()));
    }

    @Test
    void getAllUsers_asUser_returns403() throws Exception {

        mockMvc.perform(get("/api/users").with(user("testUser").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void getUsersByEmail_validEmail_returns200() throws Exception {

        UserResponseDTO responseDTO = userResponse();
        String email = responseDTO.getEmail();

        when(userService.getUserByEmail(email)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/users/email/{email}", email).with(user("testUser").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    void getUsersByEmail_invalidEmail_returns404() throws Exception {
        UserRequestDTO userRequestDTO = userRequest();
        String email = userRequestDTO.getEmail();

        when(userService.getUserByEmail(email)).thenThrow(new ResourceNotFoundException("User with that email not found"));

        mockMvc.perform(get("/api/users/email/{email}", email)
                .with(user("testUser").roles("USER")))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUsersByLastName_validLastName_returns200() throws Exception {
        UserResponseDTO responseDTO = userResponse();
        String lastName = responseDTO.getLastName();

        List<UserResponseDTO> userResponseDTOList = List.of(responseDTO);

        when(userService.getUserByLastName(lastName)).thenReturn(userResponseDTOList);

        mockMvc.perform(get("/api/users/lastname/{lastName}", lastName).with(user("testUser").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].lastName").value(lastName));
    }

    @Test
    void deleteUser_validId_returns204() throws Exception {
        Long userId = 1L;

        mockMvc.perform(delete("/api/users/{id}", userId).with(user("testUser").roles("USER")))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_invalidId_returns404() throws Exception {
        Long userId = 100L;

        doThrow(new ResourceNotFoundException("User with that email not found"))
                .when(userService).deleteUser(userId);

        mockMvc.perform(delete("/api/users/{id}", userId)
                .with(user("testUser").roles("USER")))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUser_validId_returns204() throws Exception {
        Long userId = 1L;

        String json = objectMapper.writeValueAsString(userRequest());

        mockMvc.perform(put("/api/users/{id}", userId).with(user("testUser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateUser_invalidId_returns404() throws Exception {
        Long userId = 100L;
        String json = objectMapper.writeValueAsString(userRequest());

        doThrow(new ResourceNotFoundException("User not found"))
                .when(userService).updateUser(eq(userId), any(UserRequestDTO.class));

        mockMvc.perform(put("/api/users/{id}", userId)
                        .with(user("testUser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void patchUser_validId_returns204() throws Exception {

        Long userId = 1L;

        String json = objectMapper.writeValueAsString(userPatch());

        mockMvc.perform(patch("/api/users/{id}", userId).with(user("testUser").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isNoContent());
    }

    @Test
    void patchUser_invalidId_returns404() throws Exception {
        Long userId = 100L;
        String json = objectMapper.writeValueAsString(userPatch());
        doThrow(new ResourceNotFoundException("User not found"))
                .when(userService).patchUser(eq(userId), any(UserPatchDTO.class));

        mockMvc.perform(patch("/api/users/{id}", userId)
                .with(user("testUser").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isNotFound());
    }

}