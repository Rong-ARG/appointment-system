package com.ronogar.appointment_system.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class Professional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private  String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String specialty;
    private Integer yearsOfExperience;
    private Boolean available;

    @OneToMany(mappedBy = "professional",fetch = FetchType.LAZY)
    List<Appointment> appointments;


}
