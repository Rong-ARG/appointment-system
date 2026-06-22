package com.ronogar.appointment_system.services.appointment;

import com.ronogar.appointment_system.dtos.Appointment.AppointmentPatchDTO;
import com.ronogar.appointment_system.dtos.Appointment.AppointmentRequestDTO;
import com.ronogar.appointment_system.dtos.Appointment.AppointmentResponseDTO;
import com.ronogar.appointment_system.dtos.professional.ProfessionalResponseDTO;
import com.ronogar.appointment_system.dtos.user.UserResponseDTO;
import com.ronogar.appointment_system.exceptions.ResourceNotFoundException;
import com.ronogar.appointment_system.models.Appointment;
import com.ronogar.appointment_system.models.Professional;
import com.ronogar.appointment_system.models.User;
import com.ronogar.appointment_system.repositories.AppointmentRepository;
import com.ronogar.appointment_system.repositories.ProfessionalRepository;
import com.ronogar.appointment_system.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ProfessionalRepository professionalRepository;
    private final UserRepository userRepository;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository, ProfessionalRepository professionalRepository, UserRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.professionalRepository = professionalRepository;
        this.userRepository = userRepository;
    }

    public final Appointment toEntity(AppointmentRequestDTO appointmentRequestDTO) {
        Appointment appointment = new Appointment();
        appointment.setDateTime(appointmentRequestDTO.getDateTime());
        appointment.setDateTime(appointmentRequestDTO.getDateTime());
        appointment.setStatus(appointmentRequestDTO.getStatus());

        User user = userRepository.findById(appointmentRequestDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + appointmentRequestDTO.getUserId() + " not found"));

        Professional professional = professionalRepository.findById(appointmentRequestDTO.getProfessionalId())
                .orElseThrow(() -> new ResourceNotFoundException("Professional with id " + appointmentRequestDTO.getProfessionalId() + " not found"));

        appointment.setUser(user);
        appointment.setProfessional(professional);

        return appointment;
    }

    public final AppointmentResponseDTO toDto(Appointment appointment) {
        AppointmentResponseDTO dto = new AppointmentResponseDTO();
        dto.setId(appointment.getId());
        dto.setDateTime(appointment.getDateTime());
        dto.setStatus(appointment.getStatus());

        UserResponseDTO userDTO = new UserResponseDTO();
        userDTO.setId(appointment.getUser().getId());
        userDTO.setFirstName(appointment.getUser().getFirstName());
        userDTO.setLastName(appointment.getUser().getLastName());
        userDTO.setEmail(appointment.getUser().getEmail());
        userDTO.setPhone(appointment.getUser().getPhone());

        ProfessionalResponseDTO  professionalDTO = new ProfessionalResponseDTO();
        professionalDTO.setId(appointment.getProfessional().getId());
        professionalDTO.setFirstName(appointment.getProfessional().getFirstName());
        professionalDTO.setLastName(appointment.getProfessional().getLastName());
        professionalDTO.setEmail(appointment.getProfessional().getEmail());
        professionalDTO.setPhone(appointment.getProfessional().getPhone());

        dto.setUser(userDTO);
        dto.setProfessional(professionalDTO);

        return dto;
    }

    @Override
    public List<AppointmentResponseDTO> getAppointments() {
        return appointmentRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    public AppointmentResponseDTO getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("appointment with id " + id + " not found"));
    }

    @Override
    public AppointmentResponseDTO createAppointment(AppointmentRequestDTO appointmentRequestDTO) {
        Appointment newAppointment = (toEntity(appointmentRequestDTO));
        Appointment savedAppointment = appointmentRepository.save(newAppointment);
        return toDto(savedAppointment);
    }

    @Override
    public void deleteAppointmentById(Long id) {
        appointmentRepository.findById(id)
                .orElseThrow(() ->  new ResourceNotFoundException("appointment with id " + id + " not found"));
        appointmentRepository.deleteById(id);
    }

    @Override
    public void patchAppointment(Long id, AppointmentPatchDTO appointmentPatchDTO) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("appointment with id " + id + " not found"));
        if (appointmentPatchDTO.getStatus() != null) {
            appointment.setStatus(appointmentPatchDTO.getStatus());
        }
        appointmentRepository.save(appointment);

    }
}
