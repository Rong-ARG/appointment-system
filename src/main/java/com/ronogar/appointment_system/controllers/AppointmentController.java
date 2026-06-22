package com.ronogar.appointment_system.controllers;

import com.ronogar.appointment_system.dtos.Appointment.AppointmentPatchDTO;
import com.ronogar.appointment_system.dtos.Appointment.AppointmentRequestDTO;
import com.ronogar.appointment_system.dtos.Appointment.AppointmentResponseDTO;
import com.ronogar.appointment_system.services.appointment.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public ResponseEntity<List<AppointmentResponseDTO>> getAllAppointments() {
        return ResponseEntity.ok(appointmentService.getAppointments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> getAppointmentById(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.getAppointmentById(id));
    }

    @PostMapping
    public ResponseEntity<AppointmentResponseDTO> createAppointment(@Valid @RequestBody AppointmentRequestDTO appointmentRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.createAppointment(appointmentRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointmentById(@PathVariable Long id) {
        appointmentService.deleteAppointmentById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> patchAppointment(@PathVariable Long id, @Valid @RequestBody AppointmentPatchDTO appointmentPatchDTO) {
        appointmentService.patchAppointment(id, appointmentPatchDTO);
        return ResponseEntity.noContent().build();
    }
}
