package com.ronogar.appointment_system.dtos.Appointment;

import com.ronogar.appointment_system.enums.AppointmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentRequestDTO {

    @Schema(description = "Appointment User ID", example = "2")
    @NotNull
    private Long userId;

    @Schema(description = "Appointment Professional ID", example = "103")
    @NotNull
    private Long professionalId;

    @Schema(description = "Date Time appointment", example = "2025-06-15T10:30:00")
    @NotNull
    private LocalDateTime dateTime;

    @Schema(description = "Appointment status", example = "CONFIRMED")
    @NotNull
    private AppointmentStatus status;

}
