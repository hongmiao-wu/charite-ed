package com.wise23.chariteed.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "patient_data")
public class PatientData {

    @Id
    // @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "patient_id", unique = true, nullable = false)
    Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    UserData user;

    @Column(name = "fhir_id", unique = true)
    Long fhirId;

    @OneToMany(mappedBy = "patient")
    Set<InstructionToPatient> instructions;

}
