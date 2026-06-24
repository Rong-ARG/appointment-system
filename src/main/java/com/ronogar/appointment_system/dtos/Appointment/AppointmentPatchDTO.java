package com.ronogar.appointment_system.dtos.Appointment;

import com.ronogar.appointment_system.enums.AppointmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class AppointmentPatchDTO {

    @Schema(description = "Appointment status", example = "PENDING")
    private AppointmentStatus status;

}
