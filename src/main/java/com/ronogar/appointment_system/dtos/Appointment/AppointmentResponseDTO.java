package com.ronogar.appointment_system.dtos.Appointment;

import com.ronogar.appointment_system.dtos.professional.ProfessionalResponseDTO;
import com.ronogar.appointment_system.dtos.user.UserResponseDTO;
import com.ronogar.appointment_system.enums.AppointmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentResponseDTO {

    @Schema(description = "Appointment ID", example = "9")
    private Long id;

    @Schema(description = "Date Time appointment", example = "2025-06-15T10:30:00")
    private LocalDateTime dateTime;

    @Schema(description = "Appointment status", example = "CONFIRMED")
    private AppointmentStatus status;

    @Schema(description = "Appointment Professional ID", example = "103")
    private ProfessionalResponseDTO professional;

    @Schema(description = "Appointment User ID", example = "2")
    private UserResponseDTO user;
}
