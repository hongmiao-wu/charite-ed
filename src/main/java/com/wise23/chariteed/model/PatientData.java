package com.wise23.chariteed.model;


import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Table(name = "patient_data")
public class PatientData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "patient_id", unique = true, nullable = false)
    Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user;

    @Column(name = "fhir_id", unique = true)
    Long fhirId;


    @OneToMany(fetch = FetchType.EAGER, mappedBy = "patient")
    Set<InstructionToPatient> instructions;

}
