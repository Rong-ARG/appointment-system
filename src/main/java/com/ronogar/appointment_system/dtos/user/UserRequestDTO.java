package com.ronogar.appointment_system.dtos.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class UserRequestDTO {

    @Schema(description = "User firsts name", example = "Pablo")//That's me!
    @NotBlank
    private String firstName;

    @Schema(description = "User last name", example = "Abucewicz")
    @NotBlank
    private String lastName;

    @Schema(description = "Password of the user", example = "Password123wow")//wow
    @NotBlank
    private String password;

    @Schema(description = "User email", example = "User@gmail.com")
    @Email
    @NotBlank
    private String email;

    @Schema(description = "phone number of the user", example = "98123521")
    @NotBlank
    private String phone;

}
