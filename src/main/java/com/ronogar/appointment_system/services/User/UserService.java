package com.ronogar.appointment_system.services.User;

import com.ronogar.appointment_system.dtos.user.UserPatchDTO;
import com.ronogar.appointment_system.dtos.user.UserRequestDTO;
import com.ronogar.appointment_system.dtos.user.UserResponseDTO;

import java.util.List;

public interface UserService {

    List<UserResponseDTO> getAllUsers();
    UserResponseDTO getUserById(Long id);
    UserResponseDTO getUserByEmail(String email);
    List<UserResponseDTO> getUserByLastName(String lastName);

    UserResponseDTO createUser(UserRequestDTO userRequestDTO);
    void updateUser(Long id, UserRequestDTO userRequestDTO);
    void deleteUser(Long id);
    void patchUser(Long id, UserPatchDTO userPatchDTO);
}
