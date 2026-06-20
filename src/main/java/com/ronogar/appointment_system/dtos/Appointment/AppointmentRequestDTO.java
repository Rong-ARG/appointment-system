package com.ronogar.appointment_system.dtos.Appointment;

import com.ronogar.appointment_system.enums.AppointmentStatus;
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

    @NotNull
    private Long userId;
    @NotNull
    private Long professionalId;
    @NotNull
    private LocalDateTime dateTime;
    @NotNull
    private AppointmentStatus status;

}
