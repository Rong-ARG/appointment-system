package com.ronogar.appointment_system.services.Professional;

import com.ronogar.appointment_system.dtos.professional.ProfessionalPatchDTO;
import com.ronogar.appointment_system.dtos.professional.ProfessionalRequestDTO;
import com.ronogar.appointment_system.dtos.professional.ProfessionalResponseDTO;

import java.util.List;

public interface ProfessionalService {

    List<ProfessionalResponseDTO> getAllProfessionals();
    ProfessionalResponseDTO getProfessionalById(Long id);
    ProfessionalResponseDTO getProfessionalByEmail(String email);
    List<ProfessionalResponseDTO> getProfessionalByLastName(String lastName);

    ProfessionalResponseDTO createProfessional(ProfessionalRequestDTO professionalRequestDTO);
    void updateProfessional(Long id, ProfessionalRequestDTO professionalRequestDTO);
    void deleteProfessional(Long id);
    void patchProfessional(Long id, ProfessionalPatchDTO professionalPatchDTO);
}
