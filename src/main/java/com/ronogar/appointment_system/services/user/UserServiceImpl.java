package com.ronogar.appointment_system.services.user;

import com.ronogar.appointment_system.dtos.user.UserPatchDTO;
import com.ronogar.appointment_system.dtos.user.UserRequestDTO;
import com.ronogar.appointment_system.dtos.user.UserResponseDTO;
import com.ronogar.appointment_system.exceptions.ResourceNotFoundException;
import com.ronogar.appointment_system.models.User;
import com.ronogar.appointment_system.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private User toEntity(UserRequestDTO userRequestDTO) {
        User user = new User();
        user.setFirstName(userRequestDTO.getFirstName());
        user.setLastName(userRequestDTO.getLastName());
        user.setEmail(userRequestDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        user.setPhone(userRequestDTO.getPhone());
        return user;
    }

    private UserResponseDTO toDto(User user) {
        UserResponseDTO userResponseDTO = new UserResponseDTO();

        userResponseDTO.setId(user.getId());
        userResponseDTO.setFirstName(user.getFirstName());
        userResponseDTO.setLastName(user.getLastName());
        userResponseDTO.setEmail(user.getEmail());
        userResponseDTO.setPhone(user.getPhone());

        return userResponseDTO;
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    public UserResponseDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
    }

    @Override
    public UserResponseDTO getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("User with email " + email + " not found"));
    }

    @Override
    public List<UserResponseDTO> getUserByLastName(String lastname) {

        List<User> users = userRepository.findByLastName(lastname);

        if (users.isEmpty()) {
            throw new ResourceNotFoundException("User with last name " + lastname + " not found");
        }
        return users.stream().map(this::toDto).toList();
    }

    @Override
    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        User user = toEntity(userRequestDTO);
        User saved = userRepository.save(user);
        return toDto(saved);
    }

    @Override
    public void updateUser(Long id, UserRequestDTO userRequestDTO) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));

        user.setFirstName(userRequestDTO.getFirstName());
        user.setLastName(userRequestDTO.getLastName());
        user.setEmail(userRequestDTO.getEmail());
        user.setPhone(userRequestDTO.getPhone());
        userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        userRepository.deleteById(id);
    }

    @Override
    public void patchUser(Long id, UserPatchDTO userPatchDTO) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));

        if (userPatchDTO.getFirstName() != null) {
            user.setFirstName(userPatchDTO.getFirstName());
        }
        if (userPatchDTO.getLastName() != null) {
            user.setLastName(userPatchDTO.getLastName());
        }
        if (userPatchDTO.getEmail() != null) {
            user.setEmail(userPatchDTO.getEmail());
        }
        if (userPatchDTO.getPhone() != null) {
            user.setPhone(userPatchDTO.getPhone());
        }
        userRepository.save(user);
    }
}
