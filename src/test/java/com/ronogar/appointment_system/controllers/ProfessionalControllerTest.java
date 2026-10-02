package com.ronogar.appointment_system.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ronogar.appointment_system.config.CustomUserDetailsService;
import com.ronogar.appointment_system.config.SecurityConfig;
import com.ronogar.appointment_system.dtos.professional.ProfessionalPatchDTO;
import com.ronogar.appointment_system.dtos.professional.ProfessionalRequestDTO;
import com.ronogar.appointment_system.dtos.professional.ProfessionalResponseDTO;
import com.ronogar.appointment_system.exceptions.ResourceNotFoundException;
import com.ronogar.appointment_system.services.auth.JwtService;
import com.ronogar.appointment_system.services.professional.ProfessionalService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.ronogar.appointment_system.testUtil.TestDataFactory.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProfessionalController.class)
@Import(SecurityConfig.class)
public class ProfessionalControllerTest {

    @MockitoBean
    private ProfessionalService professionalService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void getAllProfessionals_authenticatedUser_returns200() throws Exception {
        List<ProfessionalResponseDTO> professionalsList = List.of(professionalResponse());

        when(professionalService.getAllProfessionals()).thenReturn(professionalsList);

        mockMvc.perform(get("/api/professionals").with(user("user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("CoffeMan@gmail.com"));
    }

    @Test
    void getProfessionalById_validId_returns200() throws Exception {
        Long professionalId = 1L;

        when(professionalService.getProfessionalById(professionalId)).thenReturn(professionalResponse());

        mockMvc.perform(get("/api/professionals/{id}", professionalId).with(user("user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Rodriguez"));
    }

    @Test
    void getProfessionalById_invalidId_returns404() throws Exception {

        when(professionalService.getProfessionalById(1L))
                .thenThrow(new ResourceNotFoundException("Professional not found"));

        mockMvc.perform(get("/api/professionals/{id}", 1L)
                        .with(user("user").roles("USER")))
                .andExpect(status().isNotFound());
    }

    @Test
    void getProfessionalByEmail_validEmail_returns200() throws Exception {
        String email = "CoffeMan@gmail.com";

        when(professionalService.getProfessionalByEmail(email)).thenReturn(professionalResponse());

        mockMvc.perform(get("/api/professionals/email/{email}", email)
                        .with(user("user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("CoffeMan@gmail.com"));
    }

    @Test
    void getProfessionalByEmail_invalidEmail_returns404() throws Exception {

        String email = "Something@gmail.com";
        when(professionalService.getProfessionalByEmail(email))
                .thenThrow(new ResourceNotFoundException("Professional not found"));

        mockMvc.perform(get("/api/professionals/email/{email}", email)
                        .with(user("user").roles("USER")))
                .andExpect(status().isNotFound());
    }

    @Test
    void getProfessionalByLastName_validLastName_returns200() throws Exception {
        String lastName = "Rodriguez";
        List<ProfessionalResponseDTO> professional = List.of(professionalResponse());

        when(professionalService.getProfessionalByLastName(lastName)).thenReturn(professional);

        mockMvc.perform(get("/api/professionals/lastname/{lastname}", lastName)
                        .with(user("user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].lastName").value("Rodriguez"));
    }

    @Test
    void getProfessionalByLastName_invalidLastName_returns404() throws Exception {
        String lastName = "Rodriguez";

        when(professionalService.getProfessionalByLastName(lastName))
                .thenThrow(new ResourceNotFoundException("Professional not found"));

        mockMvc.perform(get("/api/professionals/lastname/{lastname}", lastName)
                        .with(user("user").roles("USER")))
                .andExpect(status().isNotFound());
    }

    @Test
    void getProfessionalBySpecialty_validSpecialty_returns200() throws Exception {
        String specialty = "welder";
        List<ProfessionalResponseDTO> professional = List.of(professionalResponse());

        when(professionalService.getProfessionalsBySpecialty(specialty)).thenReturn(professional);

        mockMvc.perform(get("/api/professionals/specialty/{specialty}", specialty)
                        .with(user("user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].specialty").value("welder"));
    }

    @Test
    void getProfessionalBySpecialty_noResults_returns200EmptyList() throws Exception {
        String specialty = "welder";

        when(professionalService.getProfessionalsBySpecialty(specialty)).thenReturn(List.of());

        mockMvc.perform(get("/api/professionals/specialty/{specialty}", specialty)
                        .with(user("user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void createProfessional_validRequest_returns201() throws Exception {

        String json = objectMapper.writeValueAsString(professionalRequest());

        when(professionalService.createProfessional(any(ProfessionalRequestDTO.class))).thenReturn(professionalResponse());

        mockMvc.perform(post("/api/professionals")
                        .with(user("user").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.specialty").value("welder"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("CoffeMan@gmail.com"));
    }

    @Test
    void createProfessional_anonymous_returns401() throws Exception {

        String json = objectMapper.writeValueAsString(professionalRequest());

        mockMvc.perform(post("/api/professionals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isUnauthorized());
        verify(professionalService, never()).createProfessional(any(ProfessionalRequestDTO.class));
    }

    @Test
    void createProfessional_blankEmail_returns400() throws Exception {

        ProfessionalRequestDTO professionalRequestDTO = professionalRequest();
        professionalRequestDTO.setEmail("");

        String json = objectMapper.writeValueAsString(professionalRequestDTO);

        mockMvc.perform(post("/api/professionals").with(user("user").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(professionalService, never()).createProfessional(any(ProfessionalRequestDTO.class));
    }

    @Test
    void createProfessional_userRole_returns403() throws Exception {

        String json = objectMapper.writeValueAsString(professionalRequest());

        mockMvc.perform(post("/api/professionals")
                .with(user("user").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isForbidden());
        verify(professionalService, never()).createProfessional(any(ProfessionalRequestDTO.class));
    }

    @Test
    void updateProfessional_validRequest_returns204() throws Exception {

        Long id = 1L;
        String json = objectMapper.writeValueAsString(professionalRequest());

        mockMvc.perform(put("/api/professionals/{id}", id).with(user("user").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNoContent());
        verify(professionalService).updateProfessional(eq(id), any(ProfessionalRequestDTO.class));
    }

    @Test
    void updateProfessional_invalidId_returns404() throws Exception {
        Long id = 1L;
        String json = objectMapper.writeValueAsString(professionalRequest());

        doThrow(new ResourceNotFoundException("Professional not found"))
                .when(professionalService).updateProfessional(eq(id), any(ProfessionalRequestDTO.class));

        mockMvc.perform(put("/api/professionals/{id}", id)
                .with(user("user").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isNotFound());

        verify(professionalService).updateProfessional(eq(id), any(ProfessionalRequestDTO.class));
    }

    @Test
    void patchProfessional_validId_returns204() throws Exception {
        Long id = 1L;

        String json = objectMapper.writeValueAsString(professionalPatch());

        mockMvc.perform(patch("/api/professionals/{id}", id)
                        .with(user("user").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNoContent());
        verify(professionalService).patchProfessional(eq(id), any(ProfessionalPatchDTO.class));
    }

    @Test
    void patchProfessional_invalidId_returns404() throws Exception {
        Long id = 1L;
        String json = objectMapper.writeValueAsString(professionalPatch());

        doThrow(new ResourceNotFoundException("Professional not found"))
                .when(professionalService).patchProfessional(eq(id), any(ProfessionalPatchDTO.class));

        mockMvc.perform(patch("/api/professionals/{id}", id)
                .with(user("user").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isNotFound());

        verify(professionalService).patchProfessional(eq(id), any(ProfessionalPatchDTO.class));
    }
}