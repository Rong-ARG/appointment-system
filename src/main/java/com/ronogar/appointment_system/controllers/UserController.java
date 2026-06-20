package com.ronogar.appointment_system.controllers;

import com.ronogar.appointment_system.dtos.user.UserPatchDTO;
import com.ronogar.appointment_system.dtos.user.UserRequestDTO;
import com.ronogar.appointment_system.dtos.user.UserResponseDTO;
import com.ronogar.appointment_system.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/lastname/{lastName}")
    public ResponseEntity<List<UserResponseDTO>> getUsersByLastName(@PathVariable String lastName) {
        return ResponseEntity.ok(userService.getUserByLastName(lastName));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDTO> getUsersByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userRequestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable Long id ,@Valid @RequestBody UserRequestDTO userRequestDTO) {
        userService.updateUser(id, userRequestDTO);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> patchUser(@PathVariable Long id,@Valid @RequestBody UserPatchDTO userPatchDTO) {
        userService.patchUser(id, userPatchDTO);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}

