package com.ronogar.appointment_system.dtos.Appointment;

import com.ronogar.appointment_system.enums.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class AppointmentPatchDTO {

    private AppointmentStatus status;

}
