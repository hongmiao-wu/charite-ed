package com.wise23.chariteed.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "practitioner_data")
public class PractitionerData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "practitioner_id", unique = true, nullable = false)
    Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    UserData user;

    @Column(name = "fhir_id", unique = true)
    Long fhirId;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "practitioner")
    Set<InstructionToPatient> givenInstructions;

    private String firstname;

    private String lastname;
}
