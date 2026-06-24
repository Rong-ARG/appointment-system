package com.ronogar.appointment_system.controllers;

import com.ronogar.appointment_system.dtos.user.UserPatchDTO;
import com.ronogar.appointment_system.dtos.user.UserRequestDTO;
import com.ronogar.appointment_system.dtos.user.UserResponseDTO;
import com.ronogar.appointment_system.services.User.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Tag(name = "Users", description = "User management endpoints")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Get all users", description = "return a list of all registered users")
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
    @Operation(summary = "Get user by last name", description = "Returns a list of users matching the given last name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No users found with the provided last name")
    })
    @GetMapping("/lastname/{lastName}")
    public ResponseEntity<List<UserResponseDTO>> getUsersByLastName(@PathVariable String lastName) {
        return ResponseEntity.ok(userService.getUserByLastName(lastName));
    }
    @Operation(summary = "Get user by email", description = "Returns a single user matching the given email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "404",description = "No user found with the provided email"),
            @ApiResponse(responseCode = "400", description = "email not valid")
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDTO> getUsersByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @Operation(summary = "Get user by ID",description = "Returns a single user matching the given ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found with the provided ID")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @Operation(summary = "Create user", description = "Creates a new user and returns the created resource")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data — check required fields")
    })
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userRequestDTO));
    }

    @Operation(summary = "Update user", description = "Fully update an existing user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data — check required fields"),
            @ApiResponse(responseCode = "404", description = "User not found with the provided ID")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable Long id ,@Valid @RequestBody UserRequestDTO userRequestDTO) {
        userService.updateUser(id, userRequestDTO);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete user", description = "Deletes an existing user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found with the provided ID")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @Operation(summary = "Partially update user", description = "Updates one or more fields of an existing user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User partially updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data — check required fields"),
            @ApiResponse(responseCode = "404", description = "User not found with the provided ID")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<Void> patchUser(@PathVariable Long id,@Valid @RequestBody UserPatchDTO userPatchDTO) {
        userService.patchUser(id, userPatchDTO);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}

