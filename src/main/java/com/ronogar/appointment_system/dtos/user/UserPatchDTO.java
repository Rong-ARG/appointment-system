package com.ronogar.appointment_system.dtos.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class UserPatchDTO {

    @Schema(description = "User firsts name", example = "Pablo")
    private String firstName;

    @Schema(description = "User last name", example = "Abucewicz")
    private String lastName;

    @Schema(description = "phone number of the user", example = "98123521")
    private String phone;

    @Schema(description = "User email", example = "User@gmail.com")
    @Email
    private String email;


}
