package com.ronogar.appointment_system.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ronogar.appointment_system.config.CustomUserDetailsService;
import com.ronogar.appointment_system.config.SecurityConfig;
import com.ronogar.appointment_system.dtos.appointment.AppointmentPatchDTO;
import com.ronogar.appointment_system.dtos.appointment.AppointmentRequestDTO;
import com.ronogar.appointment_system.dtos.appointment.AppointmentResponseDTO;
import com.ronogar.appointment_system.enums.AppointmentStatus;
import com.ronogar.appointment_system.exceptions.ResourceNotFoundException;
import com.ronogar.appointment_system.services.appointment.AppointmentService;
import com.ronogar.appointment_system.services.auth.JwtService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.ronogar.appointment_system.testUtil.TestDataFactory.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(AppointmentController.class)
@Import(SecurityConfig.class)
public class AppointmentControllerTest {

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private AppointmentService appointmentService;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void getAllAppointments_authAdmin_returns200() throws Exception {

        List<AppointmentResponseDTO> appointmentResponseDTOList = List.of(appointmentResponse());

        when(appointmentService.getAppointments()).thenReturn(appointmentResponseDTOList);
        mockMvc.perform(get("/api/appointments")
                        .with(user("user").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[0].professional.lastName").value("Rodriguez"))
                .andExpect(jsonPath("$[0].user.lastName").value("Maradona"));

        verify(appointmentService, times(1)).getAppointments();
    }

    @Test
    void getAllAppointments_userRole_returns403() throws Exception {

        mockMvc.perform(get("/api/appointments")
                        .with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
        verify(appointmentService, never()).getAppointments();
    }

    @Test
    void getAllAppointments_anonymous_returns401() throws Exception {

        mockMvc.perform(get("/api/appointments"))
                .andExpect(status().isUnauthorized());

        verify(appointmentService, never()).getAppointments();
    }

    @Test
    void getMyAppointments_authenticatedUser_returns200() throws Exception {
        List<AppointmentResponseDTO> appointmentResponseDTOList = List.of(appointmentResponse());
        when(appointmentService.getMyAppointments()).thenReturn(appointmentResponseDTOList);

        mockMvc.perform(get("/api/appointments/mine")
                        .with(user("user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[0].user.lastName").value("Maradona"))
                .andExpect(jsonPath("$[0].professional.lastName").value("Rodriguez"));

        verify(appointmentService, times(1)).getMyAppointments();
    }

    @Test
    void getMyAppointments_anonymous_returns401() throws Exception {
        mockMvc.perform(get("/api/appointments/mine"))
                .andExpect(status().isUnauthorized());

        verify(appointmentService, never()).getMyAppointments();
    }

    @Test
    void getAppointmentOfProfessionals_authenticatedUser_returns200() throws Exception {
        List<AppointmentResponseDTO> appointmentResponseDTOList = List.of(appointmentResponse());
        when(appointmentService.getAppointmentOfProfessionals()).thenReturn(appointmentResponseDTOList);
        mockMvc.perform(get("/api/appointments/mineProf")
                        .with(user("user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[0].professional.lastName").value("Rodriguez"))
                .andExpect(jsonPath("$[0].user.lastName").value("Maradona"));

        verify(appointmentService, times(1)).getAppointmentOfProfessionals();
    }

    @Test
    void getAppointmentOfProfessionals_anonymous_returns401() throws Exception {
        mockMvc.perform(get("/api/appointments/mineProf"))
                .andExpect(status().isUnauthorized());
        verify(appointmentService, never()).getAppointmentOfProfessionals();
    }

    @Test
    void getAppointmentById_authenticatedUser_returns200() throws Exception {
        Long id = 2L;

        when(appointmentService.getAppointmentById(id)).thenReturn(appointmentResponse());

        mockMvc.perform(get("/api/appointments/{id}", id)
                        .with(user("user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.professional.lastName").value("Rodriguez"))
                .andExpect(jsonPath("$.user.lastName").value("Maradona"));

        verify(appointmentService, times(1)).getAppointmentById(id);
    }

    @Test
    void getAppointmentById_invalidId_returns404() throws Exception {

        when(appointmentService.getAppointmentById(100L)).thenThrow(new ResourceNotFoundException("Appointment not found"));

        mockMvc.perform(get("/api/appointments/{id}", 100L)
                        .with(user("user").roles("USER")))
                .andExpect(status().isNotFound());
        verify(appointmentService).getAppointmentById(100L);
    }

    @Test
    void getAppointmentById_anonymous_returns401() throws Exception {
        mockMvc.perform(get("/api/appointments/{id}", 100L))
                .andExpect(status().isUnauthorized());
        verify(appointmentService, never()).getAppointmentById(100L);
    }

    @Test
    void createAppointment_validRequest_returns201() throws Exception {

        String json = objectMapper.writeValueAsString(appointmentRequest());

        when(appointmentService.createAppointment(any(AppointmentRequestDTO.class))).thenReturn(appointmentResponse());

        mockMvc.perform(post("/api/appointments")
                        .with(user("user").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L));

        verify(appointmentService).createAppointment(any(AppointmentRequestDTO.class));
    }

    @Test
    void createAppointment_nullUserId_returns400() throws Exception {
        AppointmentRequestDTO appointmentRequestDTO = appointmentRequest();
        appointmentRequestDTO.setUserId(null);

        String json = objectMapper.writeValueAsString(appointmentRequestDTO);

        mockMvc.perform(post("/api/appointments")
                        .with(user("user").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(appointmentService, never()).createAppointment(any(AppointmentRequestDTO.class));
    }

    @Test
    void createAppointment_anonymous_returns401() throws Exception {

        String json = objectMapper.writeValueAsString(appointmentRequest());

        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized());

        verify(appointmentService, never()).createAppointment(any(AppointmentRequestDTO.class));
    }

    @Test
    void deleteAppointmentById_validId_returns204() throws Exception {
        Long id = 2L;

        mockMvc.perform(delete("/api/appointments/{id}", id)
                        .with(user("user").roles("USER")))
                .andExpect(status().isNoContent());

        verify(appointmentService, times(1)).deleteAppointmentById(id);
    }

    @Test
    void deleteAppointmentById_invalidId_returns404() throws Exception {

        doThrow(new ResourceNotFoundException("Appointment not found"))
                .when(appointmentService).deleteAppointmentById(100L);

        mockMvc.perform(delete("/api/appointments/{id}", 100L)
                        .with(user("user").roles("USER")))
                .andExpect(status().isNotFound());

        verify(appointmentService).deleteAppointmentById(100L);
    }

    @Test
    void deleteAppointmentById_anonymous_returns401() throws Exception {
        mockMvc.perform(delete("/api/appointments/{id}", 100L))
                .andExpect(status().isUnauthorized());
        verify(appointmentService, never()).deleteAppointmentById(100L);
    }

    @Test
    void patchAppointment_validId_returns204() throws Exception {
        Long id = 2L;
        String json = objectMapper.writeValueAsString(appointmentPatch());

        mockMvc.perform(patch("/api/appointments/{id}", id)
                        .with(user("user").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNoContent());

        ArgumentCaptor<AppointmentPatchDTO> captor = ArgumentCaptor.forClass(AppointmentPatchDTO.class);
        verify(appointmentService).patchAppointment(eq(id), captor.capture());
        assertEquals(AppointmentStatus.CONFIRMED, captor.getValue().getStatus());
    }

    @Test
    void patchAppointment_invalidId_returns404() throws Exception {

        String json = objectMapper.writeValueAsString(appointmentPatch());

        doThrow(new ResourceNotFoundException("Appointment not found"))
                .when(appointmentService).patchAppointment(eq(100L), any(AppointmentPatchDTO.class));

        mockMvc.perform(patch("/api/appointments/{id}", 100L)
                        .with(user("user").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());

        verify(appointmentService).patchAppointment(eq(100L), any(AppointmentPatchDTO.class));
    }

    @Test
    void patchAppointment_anonymous_returns401() throws Exception {

        String json = objectMapper.writeValueAsString(appointmentPatch());

        mockMvc.perform(patch("/api/appointments/{id}", 100L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized());

        verify(appointmentService, never()).patchAppointment(eq(100L), any(AppointmentPatchDTO.class));
    }

    @Test
    void patchAppointment_invalidStatus_returns400() throws Exception {
        String json = "{\"status\": \"INVALID\"}";

        mockMvc.perform(patch("/api/appointments/{id}", 100L)
                .with(user("user").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());

        verify(appointmentService, never()).patchAppointment(eq(100L), any(AppointmentPatchDTO.class));
    }
}
