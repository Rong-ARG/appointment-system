package com.ronogar.appointment_system.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ronogar.appointment_system.config.CustomUserDetailsService;
import com.ronogar.appointment_system.config.SecurityConfig;
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

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    private final ProfessionalResponseDTO professionalResponseBuilder = new ProfessionalResponseDTO(
            1L, "Juan", "Rodriguez", "CoffeMan@gmail.com",
            "213512", "welder", 2, true);

    private final ProfessionalRequestDTO professionalRequestBuilder = new ProfessionalRequestDTO(
            "Juan", "Rodriguez", "CoffeMan@gmail.com", "1235",
            "welder", 2, true, "password123");

    @Test
    void getAllProfessionals_returns200() throws Exception {
        List<ProfessionalResponseDTO> professionalsList = List.of(professionalResponseBuilder);

        when(professionalService.getAllProfessionals()).thenReturn(professionalsList);

        mockMvc.perform(get("/api/professionals").with(user("user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("CoffeMan@gmail.com"));
    }

    @Test
    void getProfessionalById_ValidId_returns200() throws Exception {
        Long professionalId = 1L;
        ProfessionalResponseDTO professional = professionalResponseBuilder;

        when(professionalService.getProfessionalById(professionalId)).thenReturn(professional);

        mockMvc.perform(get("/api/professionals/{id}", professionalId).with(user("user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Rodriguez"));
    }

    @Test
    void getProfessionalById_InvalidId_returns404() throws Exception {

        when(professionalService.getProfessionalById(1L))
                .thenThrow(new ResourceNotFoundException("Professional not found"));

        mockMvc.perform(get("/api/professionals/{id}", 1L)
                        .with(user("user").roles("USER")))
                .andExpect(status().isNotFound());
    }

    @Test
    void getProfessionalByEmail_ValidEmail_returns200() throws Exception {
        String email = "CoffeMan@gmail.com";

        ProfessionalResponseDTO professional = professionalResponseBuilder;

        when(professionalService.getProfessionalByEmail(email)).thenReturn(professional);

        mockMvc.perform(get("/api/professionals/email/{email}", email)
                        .with(user("user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("CoffeMan@gmail.com"));
    }

    @Test
    void getProfessionalByEmail_InvalidEmail_returns404() throws Exception {

        String email = "Something@gmail.com";
        when(professionalService.getProfessionalByEmail(email))
                .thenThrow(new ResourceNotFoundException("Professional not found"));

        mockMvc.perform(get("/api/professionals/email/{email}", email)
                        .with(user("user").roles("USER")))
                .andExpect(status().isNotFound());
    }

    @Test
    void getProfessionalByLastName_ValidLastName_returns200() throws Exception {
        String lastName = "Rodriguez";
        List<ProfessionalResponseDTO> professional = List.of(professionalResponseBuilder);

        when(professionalService.getProfessionalByLastName(lastName)).thenReturn(professional);

        mockMvc.perform(get("/api/professionals/lastname/{lastname}", lastName)
                        .with(user("user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].lastName").value("Rodriguez"));
    }

    @Test
    void getProfessionalByLastName_InvalidLastName_returns404() throws Exception {
        String lastName = "Rodriguez";

        when(professionalService.getProfessionalByLastName(lastName))
                .thenThrow(new ResourceNotFoundException("Professional not found"));

        mockMvc.perform(get("/api/professionals/lastname/{lastname}", lastName)
                        .with(user("user").roles("USER")))
                .andExpect(status().isNotFound());
    }

    @Test
    void getProfessionalBySpecialty_ValidSpecialty_returns200() throws Exception {
        String specialty = "welder";
        List<ProfessionalResponseDTO> professional = List.of(professionalResponseBuilder);

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
    void createProfessional_Valid_returns201() throws Exception {
        ProfessionalRequestDTO professionalRequestDTO = professionalRequestBuilder;

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(professionalRequestDTO);

        when(professionalService.createProfessional(any(ProfessionalRequestDTO.class))).thenReturn(professionalResponseBuilder);

        mockMvc.perform(post("/api/professionals")
                        .with(user("user").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.specialty").value("welder"));
    }
}