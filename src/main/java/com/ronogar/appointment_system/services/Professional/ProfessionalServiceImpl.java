package com.ronogar.appointment_system.services.Professional;

import com.ronogar.appointment_system.dtos.professional.ProfessionalPatchDTO;
import com.ronogar.appointment_system.dtos.professional.ProfessionalRequestDTO;
import com.ronogar.appointment_system.dtos.professional.ProfessionalResponseDTO;
import com.ronogar.appointment_system.exceptions.ResourceNotFoundException;
import com.ronogar.appointment_system.models.Professional;
import com.ronogar.appointment_system.models.User;
import com.ronogar.appointment_system.repositories.ProfessionalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfessionalServiceImpl implements ProfessionalService {

    private final ProfessionalRepository professionalRepository;

    public ProfessionalServiceImpl(ProfessionalRepository professionalRepository) {
        this.professionalRepository = professionalRepository;
    }

    private Professional toEntity(ProfessionalRequestDTO professionalRequestDTO) {
        Professional professional = new Professional();
        professional.setFirstName(professionalRequestDTO.getFirstName());
        professional.setLastName(professionalRequestDTO.getLastName());
        professional.setEmail(professionalRequestDTO.getEmail());
        professional.setPhone(professionalRequestDTO.getPhone());
        professional.setAvailable(professionalRequestDTO.getAvailable());
        professional.setSpecialty(professionalRequestDTO.getSpecialty());
        professional.setYearsOfExperience(professionalRequestDTO.getYearsOfExperience());
        return professional;
    }

    private ProfessionalResponseDTO toDto(Professional professional) {
        ProfessionalResponseDTO responseDTO = new ProfessionalResponseDTO();
        responseDTO.setFirstName(professional.getFirstName());
        responseDTO.setLastName(professional.getLastName());
        responseDTO.setEmail(professional.getEmail());
        responseDTO.setPhone(professional.getPhone());
        responseDTO.setAvailable(professional.getAvailable());
        responseDTO.setSpecialty(professional.getSpecialty());
        responseDTO.setYearsOfExperience(professional.getYearsOfExperience());
        return responseDTO;
    }


    @Override
    public List<ProfessionalResponseDTO> getAllProfessionals() {
        return professionalRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    public ProfessionalResponseDTO getProfessionalById(Long id) {
        return professionalRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("professional with id: " + id + " not found"));
    }

    @Override
    public ProfessionalResponseDTO getProfessionalByEmail(String email) {
        return professionalRepository.findByEmail(email)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("professional with email: " + email + " not found"));
    }

    @Override
    public List<ProfessionalResponseDTO> getProfessionalByLastName(String lastName) {
        List<Professional> professionals = professionalRepository.findByLastName(lastName);
        if (professionals.isEmpty()) {
            throw new ResourceNotFoundException("professional with last name: " + lastName + " not found");
        }
        return professionals.stream().map(this::toDto).toList();
    }

    @Override
    public ProfessionalResponseDTO createProfessional(ProfessionalRequestDTO professionalRequestDTO) {
        Professional newUser = toEntity(professionalRequestDTO);
        Professional saved = professionalRepository.save(newUser);
        return toDto(saved);

    }

    @Override
    public void updateProfessional(Long id, ProfessionalRequestDTO professionalRequestDTO) {
        Professional professional = professionalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("professional with id: " + id + " not found"));
        professional.setFirstName(professionalRequestDTO.getFirstName());
        professional.setLastName(professionalRequestDTO.getLastName());
        professional.setEmail(professionalRequestDTO.getEmail());
        professional.setPhone(professionalRequestDTO.getPhone());
        professional.setAvailable(professionalRequestDTO.getAvailable());
        professional.setSpecialty(professionalRequestDTO.getSpecialty());
        professional.setYearsOfExperience(professionalRequestDTO.getYearsOfExperience());
        professionalRepository.save(professional);
    }

    @Override
    public void deleteProfessional(Long id) {
        professionalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("professional with id: " + id + " not found"));
        professionalRepository.deleteById(id);
    }

    @Override
    public void patchProfessional(Long id, ProfessionalPatchDTO professional) {
       Professional professional1 =  professionalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("professional with id: " + id + " not found"));
        if (professional.getEmail() != null) {
            professional1.setEmail(professional.getEmail());
        }
        if (professional.getFirstName() != null) {
            professional1.setFirstName(professional.getFirstName());
        }
        if (professional.getLastName() != null) {
            professional1.setLastName(professional.getLastName());
        }
        if (professional.getSpecialty() != null) {
            professional1.setSpecialty(professional.getSpecialty());
        }
        if (professional.getYearsOfExperience() != null) {
            professional1.setYearsOfExperience(professional.getYearsOfExperience());
        }
        if (professional.getAvailable() != null) {
            professional1.setAvailable(professional.getAvailable());
        }
        if (professional.getPhone() != null) {
            professional1.setPhone(professional.getPhone());
        }
        professionalRepository.save(professional1);
    }
}
