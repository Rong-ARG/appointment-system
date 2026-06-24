package com.ronogar.appointment_system.controllers;

import com.ronogar.appointment_system.dtos.Appointment.AppointmentPatchDTO;
import com.ronogar.appointment_system.dtos.Appointment.AppointmentRequestDTO;
import com.ronogar.appointment_system.dtos.Appointment.AppointmentResponseDTO;
import com.ronogar.appointment_system.services.appointment.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Appointment", description = "Appointment management endpoints")
@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @Operation(summary = "Get all appointment", description = "Returns a list of all appointments")
    @GetMapping
    public ResponseEntity<List<AppointmentResponseDTO>> getAllAppointments() {
        return ResponseEntity.ok(appointmentService.getAppointments());
    }

    @Operation(summary = "Get appointment", description = "Returns appointment matched with the ID provided")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Appointment retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Appointment with the ID provided not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> getAppointmentById(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.getAppointmentById(id));
    }

    @Operation(summary = "Create appointment", description = "Create a new appointment and return the created resources")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Appointment created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid Request data")
    })
    @PostMapping
    public ResponseEntity<AppointmentResponseDTO> createAppointment(@Valid @RequestBody AppointmentRequestDTO appointmentRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.createAppointment(appointmentRequestDTO));
    }
    @Operation(summary = "Delete appointment", description = "Delete a appointment by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Appointment deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Appointment with the id provided not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointmentById(@PathVariable Long id) {
        appointmentService.deleteAppointmentById(id);
        return ResponseEntity.noContent().build();
    }
    @Operation(summary = "Partially update a appointment", description = "Update one or more fields of a existing appointment by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Partially updated successfully"),
            @ApiResponse(responseCode = "404", description = "Appointment with the id provided not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<Void> patchAppointment(@PathVariable Long id, @Valid @RequestBody AppointmentPatchDTO appointmentPatchDTO) {
        appointmentService.patchAppointment(id, appointmentPatchDTO);
        return ResponseEntity.noContent().build();
    }
}
