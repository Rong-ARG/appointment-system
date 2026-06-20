package com.ronogar.appointment_system.dtos.user;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class UserPatchDTO {

    private String firstName;
    private String lastName;
    private String phone;

    @Email
    private String email;


}
