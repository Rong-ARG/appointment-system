package com.ronogar.appointment_system.services.appointment;

import com.ronogar.appointment_system.dtos.appointment.AppointmentPatchDTO;
import com.ronogar.appointment_system.dtos.appointment.AppointmentRequestDTO;
import com.ronogar.appointment_system.dtos.appointment.AppointmentResponseDTO;

import java.util.List;

public interface AppointmentService {

    List<AppointmentResponseDTO> getAppointments();
    AppointmentResponseDTO getAppointmentById(Long id);

    AppointmentResponseDTO createAppointment(AppointmentRequestDTO appointmentRequestDTO);
    void deleteAppointmentById(Long id);
    void patchAppointment(Long id, AppointmentPatchDTO appointmentPatchDTO);
}
