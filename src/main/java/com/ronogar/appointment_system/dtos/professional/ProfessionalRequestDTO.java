package com.ronogar.appointment_system.dtos.professional;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class ProfessionalRequestDTO {

    @Schema(description = "First name of the professional", example = "Facundo")
    @NotBlank
    private  String firstName;

    @Schema(description = "Last name of the professional", example = "Bertolini")
    @NotBlank
    private String lastName;

    @Schema(description = "Email of the professional", example = "Professional@gmail.com")
    @Email
    @NotBlank
    private String email;

    @Schema(description = "Phone number of the professional", example = "91276128")
    @NotBlank
    private String phone;

    @Schema(description = "Specialty of the professional", example = "Welder")
    @NotBlank
    private String specialty;

    @Schema(description = "Years of Experience of the professional", example = "9")
    @NotNull
    private Integer yearsOfExperience;

    @Schema(description = "Whether the professional is available", example = "true")
    @NotNull
    private Boolean available;
}
