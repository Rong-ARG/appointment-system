package com.ronogar.appointment_system.controllers;

import com.ronogar.appointment_system.dtos.auth.AuthRequestDTO;
import com.ronogar.appointment_system.dtos.auth.AuthResponseDTO;
import com.ronogar.appointment_system.services.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@Tag(name = "Auth",description = "Authentication endpoints")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/login")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Log in", description = "Authenticates with email and password and returns a JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful, token returned"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Bad credentials")
    })
    @PostMapping
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO authRequestDTO){

        AuthResponseDTO authResponseDTO = authService.login(authRequestDTO);

        return ResponseEntity.ok(authResponseDTO);
    }
}
