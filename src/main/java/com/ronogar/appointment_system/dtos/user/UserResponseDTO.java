package com.ronogar.appointment_system.dtos.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class UserResponseDTO {

    @Schema(description = "User ID", example = "231")
    private Long id;

    @Schema(description = "User firsts name", example = "Pablo")
    private String firstName;

    @Schema(description = "User last name", example = "Abucewicz")
    private String lastName;

    @Schema(description = "User email", example = "User@gmail.com")
    private String email;

    @Schema(description = "phone number of the user", example = "98123521")
    private String phone;
}
