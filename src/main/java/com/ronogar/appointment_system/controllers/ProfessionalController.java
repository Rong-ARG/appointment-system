package com.ronogar.appointment_system.controllers;

import com.ronogar.appointment_system.dtos.professional.ProfessionalPatchDTO;
import com.ronogar.appointment_system.dtos.professional.ProfessionalRequestDTO;
import com.ronogar.appointment_system.dtos.professional.ProfessionalResponseDTO;
import com.ronogar.appointment_system.services.Professional.ProfessionalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Professionals", description = "Professional management endpoints")
@RestController
@RequestMapping("/api/professionals")
public class ProfessionalController {

    private final ProfessionalService professionalService;

    public ProfessionalController(ProfessionalService professionalService) {
        this.professionalService = professionalService;
    }

    @Operation(summary = "Get all professionals", description = "Returns a list of all registered professionals")
    @GetMapping
    public ResponseEntity<List<ProfessionalResponseDTO>> getAllProfessionals() {
        return ResponseEntity.ok(professionalService.getAllProfessionals());
    }

    @Operation(summary = "Get professional by ID", description = "Returns a single professionals matching the given ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Professional retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Professional not found with the provided ID")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProfessionalResponseDTO> getProfessionalById(@PathVariable Long id) {
        return ResponseEntity.ok(professionalService.getProfessionalById(id));
    }

    @Operation(summary = "Get professional by email", description = "Returns a single professional matching the given email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Professional retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Professional not found with the provided email"),
            @ApiResponse(responseCode = "400", description = "email not valid")
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<ProfessionalResponseDTO> getProfessionalByEmail(@PathVariable String email) {
        return ResponseEntity.ok(professionalService.getProfessionalByEmail(email));
    }

    @Operation(summary = "Get professional by last name", description = "Returns a list of professional with matched with the last name provided")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of professional retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "professionals not founds with the last name provided")
    })
    @GetMapping("/lastname/{lastname}")
    public ResponseEntity<List<ProfessionalResponseDTO>> getProfessionalByLastname(@PathVariable String lastname) {
        return ResponseEntity.ok(professionalService.getProfessionalByLastName(lastname));
    }

    @Operation(summary = "Create professional", description = "Create a new professional and returns the created resourced")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Professional created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data — check required fields")
    })
    @PostMapping
    public ResponseEntity<ProfessionalResponseDTO> createProfessional(@Valid @RequestBody ProfessionalRequestDTO professionalRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(professionalService.createProfessional(professionalRequestDTO));
    }

    @Operation(summary = "Update professional", description = "Fully update a existing professional by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Professional fully updated successfully"),
            @ApiResponse(responseCode = "404", description = "Professional with the id provided not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request data — check required fields")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateProfessional(@PathVariable Long id, @Valid @RequestBody ProfessionalRequestDTO professionalRequestDTO) {
        professionalService.updateProfessional(id, professionalRequestDTO);
        return ResponseEntity.noContent().build();
    }
    @Operation(summary = "Delete professional", description = "Delete Professional by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Delete professional successfully"),
            @ApiResponse(responseCode = "404", description = "Professional with the id provided not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfessional(@PathVariable Long id) {
        professionalService.deleteProfessional(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Partially update professional", description = "Update one or more fields of a existing professional by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Professional partially updated successfully"),
            @ApiResponse(responseCode = "404", description = "Professional with the ID provided not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request data — check required fields")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<Void> patchProfessional(@PathVariable Long id, @Valid @RequestBody ProfessionalPatchDTO professionalPatchDTO) {
        professionalService.patchProfessional(id, professionalPatchDTO);
        return ResponseEntity.noContent().build();
    }

}


