package com.ronogar.appointment_system.testUtil;

import com.ronogar.appointment_system.dtos.appointment.AppointmentPatchDTO;
import com.ronogar.appointment_system.dtos.appointment.AppointmentRequestDTO;
import com.ronogar.appointment_system.dtos.appointment.AppointmentResponseDTO;
import com.ronogar.appointment_system.dtos.auth.AuthRequestDTO;
import com.ronogar.appointment_system.dtos.auth.AuthResponseDTO;
import com.ronogar.appointment_system.dtos.professional.ProfessionalPatchDTO;
import com.ronogar.appointment_system.dtos.professional.ProfessionalRequestDTO;
import com.ronogar.appointment_system.dtos.professional.ProfessionalResponseDTO;
import com.ronogar.appointment_system.dtos.user.UserPatchDTO;
import com.ronogar.appointment_system.dtos.user.UserRequestDTO;
import com.ronogar.appointment_system.dtos.user.UserResponseDTO;
import com.ronogar.appointment_system.enums.AppointmentStatus;

import java.time.LocalDateTime;

public class TestDataFactory {


    public static ProfessionalResponseDTO professionalResponse() {

        ProfessionalResponseDTO responseDTO = new ProfessionalResponseDTO(
                1L, "Juan", "Rodriguez", "CoffeMan@gmail.com",
                "213512", "welder", 2, true);

        return responseDTO;
    }

    public static ProfessionalRequestDTO professionalRequest() {

        ProfessionalRequestDTO requestDTO = new ProfessionalRequestDTO(
                "Juan", "Rodriguez", "CoffeMan@gmail.com",
                "1235", "welder", 2, true,
                "password123");

        return requestDTO;
    }

    public static ProfessionalPatchDTO professionalPatch() {
        ProfessionalPatchDTO patchDTO = new ProfessionalPatchDTO("Juan", "Rodriguez", "CoffeMan@gmail.com"
                , "123512", "welder", 2, true);
        return patchDTO;
    }

    public static UserResponseDTO userResponse() {

        UserResponseDTO userResponseDTO = new UserResponseDTO(
                1L, "Diego", "Maradona"
                , "Marado@gmail.com", "11111"
        );

        return userResponseDTO;

    }

    public static UserRequestDTO userRequest() {

        UserRequestDTO userRequestDTO = new UserRequestDTO(
                "Martin", "Fernandez",
                "12345", "Martin@gmail.com",
                "210512");

        return userRequestDTO;
    }

    public static UserPatchDTO userPatch() {
        UserPatchDTO userPatchDTO = new UserPatchDTO(
                "Martin", "Fernandez"
                , "12345", "Martin@gmail.com");
        return userPatchDTO;
    }

    public static AppointmentResponseDTO appointmentResponse() {

        AppointmentResponseDTO responseDTO = new AppointmentResponseDTO(
                2L, LocalDateTime.of(2025, 6, 15, 10, 30)
                , AppointmentStatus.PENDING, professionalResponse(), userResponse());

        return responseDTO;
    }

    public static AppointmentRequestDTO appointmentRequest() {

        AppointmentRequestDTO appointmentRequestDTO = new AppointmentRequestDTO(1L, 1L
                , LocalDateTime.of(2025, 10, 15, 21, 30, 30)
                , AppointmentStatus.PENDING);

        return appointmentRequestDTO;
    }

    public static AppointmentPatchDTO appointmentPatch() {
        AppointmentPatchDTO appointmentPatchDTO = new AppointmentPatchDTO(AppointmentStatus.CONFIRMED);

        return appointmentPatchDTO;
    }

    public static AuthRequestDTO authRequest() {
        AuthRequestDTO authRequestDTO = new AuthRequestDTO();
        authRequestDTO.setEmail("Something@gmail.com");
        authRequestDTO.setPassword("1234");
        return authRequestDTO;
    }

    public static AuthResponseDTO authResponse() {
        AuthResponseDTO authResponseDTO = new AuthResponseDTO("coffe-jwt-key");
        return authResponseDTO;
    }

}
