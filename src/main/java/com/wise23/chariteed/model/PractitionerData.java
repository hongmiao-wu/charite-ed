package com.wise23.chariteed.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Table(name = "practitioner_data")
public class PractitionerData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "practitioner_id", unique = true, nullable = false)
    Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "practitioner")
    Set<InstructionToPatient> givenInstructions;
}
