package com.ronogar.appointment_system.services.professional;

import com.ronogar.appointment_system.dtos.professional.ProfessionalPatchDTO;
import com.ronogar.appointment_system.dtos.professional.ProfessionalRequestDTO;
import com.ronogar.appointment_system.dtos.professional.ProfessionalResponseDTO;
import com.ronogar.appointment_system.dtos.professional.ProfessionalSelfRequestDTO;
import com.ronogar.appointment_system.enums.Role;
import com.ronogar.appointment_system.exceptions.DuplicateResourceException;
import com.ronogar.appointment_system.exceptions.ResourceNotFoundException;
import com.ronogar.appointment_system.models.Account;
import com.ronogar.appointment_system.models.Professional;
import com.ronogar.appointment_system.repositories.AccountRepository;
import com.ronogar.appointment_system.repositories.ProfessionalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProfessionalServiceImpl implements ProfessionalService {

    private final ProfessionalRepository professionalRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;

    private Professional toEntity(ProfessionalRequestDTO dto) {
        Professional professional = new Professional();
        professional.setFirstName(dto.getFirstName());
        professional.setLastName(dto.getLastName());
        professional.setPhone(dto.getPhone());
        professional.setAvailable(dto.getAvailable());
        professional.setSpecialty(dto.getSpecialty());
        professional.setYearsOfExperience(dto.getYearsOfExperience());
        return professional;
    }

    private Professional toEntity(ProfessionalSelfRequestDTO dto) {
        Professional professional = new Professional();
        professional.setFirstName(dto.getFirstName());
        professional.setLastName(dto.getLastName());
        professional.setPhone(dto.getPhone());
        professional.setAvailable(dto.getAvailable());
        professional.setSpecialty(dto.getSpecialty());
        professional.setYearsOfExperience(dto.getYearsOfExperience());
        return professional;
    }

    private ProfessionalResponseDTO toDto(Professional professional) {
        ProfessionalResponseDTO responseDTO = new ProfessionalResponseDTO();
        responseDTO.setId(professional.getId());
        responseDTO.setFirstName(professional.getFirstName());
        responseDTO.setLastName(professional.getLastName());
        responseDTO.setEmail(professional.getAccount().getEmail());
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
        return professionalRepository.findByAccountEmail(email)
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
        Account account = accountRepository.findByEmail(professionalRequestDTO.getEmail())
                .map(this::attachProfessionalRole)
                .orElseGet(() -> createAccount(professionalRequestDTO));

        Professional professional = toEntity(professionalRequestDTO);
        professional.setAccount(account);
        Professional saved = professionalRepository.save(professional);
        return toDto(saved);
    }

    @Override
    public ProfessionalResponseDTO createOwnProfessionalProfile(String email, ProfessionalSelfRequestDTO dto) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Account with email " + email + " not found"));

        attachProfessionalRole(account);

        Professional professional = toEntity(dto);
        professional.setAccount(account);
        Professional saved = professionalRepository.save(professional);
        return toDto(saved);
    }

    private Account attachProfessionalRole(Account account) {
        if (account.getProfessional() != null) {
            throw new DuplicateResourceException(
                    "A professional profile already exists for email " + account.getEmail());
        }
        account.getRoles().add(Role.PROFESSIONAL);
        return accountRepository.save(account);
    }

    private Account createAccount(ProfessionalRequestDTO professionalRequestDTO) {
        Account account = new Account();
        account.setEmail(professionalRequestDTO.getEmail());
        account.setPassword(passwordEncoder.encode(professionalRequestDTO.getPassword()));
        account.setRoles(new HashSet<>(Set.of(Role.PROFESSIONAL)));
        return accountRepository.save(account);
    }

    @Override
    public void updateProfessional(Long id, ProfessionalRequestDTO professionalRequestDTO) {
        Professional professional = professionalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("professional with id: " + id + " not found"));

        professional.setFirstName(professionalRequestDTO.getFirstName());
        professional.setLastName(professionalRequestDTO.getLastName());
        professional.setPhone(professionalRequestDTO.getPhone());
        professional.setAvailable(professionalRequestDTO.getAvailable());
        professional.setSpecialty(professionalRequestDTO.getSpecialty());
        professional.setYearsOfExperience(professionalRequestDTO.getYearsOfExperience());

        Account account = professional.getAccount();
        account.setPassword(passwordEncoder.encode(professionalRequestDTO.getPassword()));
        account.setEmail(professionalRequestDTO.getEmail());
        accountRepository.save(account);

        professionalRepository.save(professional);
    }

    @Override
    public void deleteProfessional(Long id) {
        Professional professional = professionalRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("professional with id: " + id + " not found"));
        Account account = professional.getAccount();
        professionalRepository.deleteById(id);

        if (account.getUser() == null) {
            accountRepository.delete(account);
        } else {
            account.getRoles().remove(Role.PROFESSIONAL);
            accountRepository.save(account);
        }
    }


    @Override
    public void patchProfessional(Long id, ProfessionalPatchDTO professionalPatchDTO) {
        Professional professional1 = professionalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("professional with id: " + id + " not found"));
        if (professionalPatchDTO.getEmail() != null) {
            Account account = professional1.getAccount();
            account.setEmail(professionalPatchDTO.getEmail());
            accountRepository.save(account);

        }
        if (professionalPatchDTO.getFirstName() != null) {
            professional1.setFirstName(professionalPatchDTO.getFirstName());
        }
        if (professionalPatchDTO.getLastName() != null) {
            professional1.setLastName(professionalPatchDTO.getLastName());
        }
        if (professionalPatchDTO.getSpecialty() != null) {
            professional1.setSpecialty(professionalPatchDTO.getSpecialty());
        }
        if (professionalPatchDTO.getYearsOfExperience() != null) {
            professional1.setYearsOfExperience(professionalPatchDTO.getYearsOfExperience());
        }
        if (professionalPatchDTO.getAvailable() != null) {
            professional1.setAvailable(professionalPatchDTO.getAvailable());
        }
        if (professionalPatchDTO.getPhone() != null) {
            professional1.setPhone(professionalPatchDTO.getPhone());
        }
        professionalRepository.save(professional1);
    }
}
