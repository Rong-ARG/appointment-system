package com.ronogar.appointment_system.services.appointment;

import com.ronogar.appointment_system.dtos.Appointment.AppointmentPatchDTO;
import com.ronogar.appointment_system.dtos.Appointment.AppointmentRequestDTO;
import com.ronogar.appointment_system.dtos.Appointment.AppointmentResponseDTO;

import java.util.List;

public interface AppointmentService {

    List<AppointmentResponseDTO> getAppointments();
    AppointmentResponseDTO getAppointmentById(Long id);

    AppointmentResponseDTO createAppointment(AppointmentRequestDTO appointmentRequestDTO);
    void deleteAppointmentById(Long id);
    void patchAppointment(Long id, AppointmentPatchDTO appointmentPatchDTO);
}
