package com.ronogar.appointment_system.controllers;

import com.ronogar.appointment_system.dtos.auth.AuthRequestDTO;
import com.ronogar.appointment_system.dtos.auth.AuthResponseDTO;
import com.ronogar.appointment_system.services.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/login")
public class AuthController {

    private final AuthService authService;

    @PostMapping
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO authRequestDTO){

        AuthResponseDTO authResponseDTO = authService.login(authRequestDTO);

        return ResponseEntity.ok(authResponseDTO);
    }
}
