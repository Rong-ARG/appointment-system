package com.ronogar.appointment_system.dtos.Appointment;

import com.ronogar.appointment_system.dtos.professional.ProfessionalResponseDTO;
import com.ronogar.appointment_system.dtos.user.UserResponseDTO;
import com.ronogar.appointment_system.enums.AppointmentStatus;
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

    private Long id;

    private LocalDateTime dateTime;
    private AppointmentStatus status;

    private ProfessionalResponseDTO professional;
    private UserResponseDTO user;
}
