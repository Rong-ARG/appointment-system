package com.ronogar.appointment_system.dtos.professional;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class ProfessionalPatchDTO {

    @Schema(description = "First name of the professional", example = "Facundo")
    private  String firstName;

    @Schema(description = "Last name of the professional", example = "Bertolini")
    private String lastName;

    @Schema(description = "Email of the professional", example = "Professional@gmail.com")
    private String email;

    @Schema(description = "Phone number of the professional", example = "91276128")
    private String phone;

    @Schema(description = "Specialty of the professional", example = "Welder")
    private String specialty;

    @Schema(description = "Years of Experience of the professional", example = "9")
    private Integer yearsOfExperience;

    @Schema(description = "Whether the professional is available", example = "true")
    private Boolean available;
}
