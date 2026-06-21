package com.ronogar.appointment_system.controllers;

import com.ronogar.appointment_system.dtos.professional.ProfessionalPatchDTO;
import com.ronogar.appointment_system.dtos.professional.ProfessionalRequestDTO;
import com.ronogar.appointment_system.dtos.professional.ProfessionalResponseDTO;
import com.ronogar.appointment_system.services.Professional.ProfessionalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/professionals")
public class ProfessionalController {

    private final ProfessionalService professionalService;

    public ProfessionalController(ProfessionalService professionalService) {
        this.professionalService = professionalService;
    }

    @GetMapping
    public ResponseEntity<List<ProfessionalResponseDTO>> getAllProfessionals() {
        return ResponseEntity.ok(professionalService.getAllProfessionals());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfessionalResponseDTO> getProfessionalById(@PathVariable Long id) {
        return ResponseEntity.ok(professionalService.getProfessionalById(id));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ProfessionalResponseDTO> getProfessionalByEmail(@PathVariable String email) {
        return ResponseEntity.ok(professionalService.getProfessionalByEmail(email));
    }

    @GetMapping("/lastname/{lastname}")
    public ResponseEntity<List<ProfessionalResponseDTO>> getProfessionalByLastname(@PathVariable String lastname) {
        return ResponseEntity.ok(professionalService.getProfessionalByLastName(lastname));
    }

    @PostMapping
    public ResponseEntity<ProfessionalResponseDTO> createProfessional(@Valid @RequestBody ProfessionalRequestDTO professionalRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(professionalService.createProfessional(professionalRequestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateProfessional(@PathVariable Long id,@Valid @RequestBody ProfessionalRequestDTO professionalRequestDTO) {
        professionalService.updateProfessional(id, professionalRequestDTO);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfessional(@PathVariable Long id) {
        professionalService.deleteProfessional(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void>  patchProfessional (@PathVariable Long id,@Valid @RequestBody ProfessionalPatchDTO professionalPatchDTO) {
        professionalService.patchProfessional(id, professionalPatchDTO);
        return ResponseEntity.noContent().build();
    }

}


