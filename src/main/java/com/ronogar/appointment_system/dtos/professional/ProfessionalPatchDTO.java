package com.ronogar.appointment_system.dtos.professional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class ProfessionalPatchDTO {

    private  String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String specialty;
    private Integer yearsOfExperience;
    private Boolean available;
}
