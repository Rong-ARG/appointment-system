package com.ronogar.appointment_system.dtos.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequestDTO {

    @NotBlank(message = "email needed")
    @Email(message = "email must be valid")
    private String email;

    @NotBlank(message = "Password is obligatory")
    private String password;
}
