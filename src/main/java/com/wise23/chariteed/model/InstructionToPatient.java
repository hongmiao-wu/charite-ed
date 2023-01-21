package com.wise23.chariteed.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name ="instruction_to_patient")
@Data
@NoArgsConstructor
public class InstructionToPatient {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "instruction_to_patient_id", unique = true, nullable = false)
    private Long id;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="instruction_id")
    Instruction instruction;

    @ManyToOne
    @JoinColumn(name = "practitioner_id")
    PractitionerData practitioner;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    PatientData patient;


    @CreationTimestamp
    @Column(name = "given_at", updatable = false)
    LocalDateTime givenAt;

    @Column(name = "comment")
    String comment;
}
