package com.ronogar.appointment_system.dtos.user;

import com.ronogar.appointment_system.models.Appointment;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class UserRequestDTO {

    @NotBlank
    private String username;
    @NotBlank
    private String lastName;
    @NotBlank
    private String password;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String phone;

}
