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


    @Column(name = "given_at", updatable = false)
    LocalDateTime givenAt;

    @Column(name = "practitioner_comment")
    String practitionerComment;

    @Column(name = "patient_comment")
    String patientComment;

    @Column(name = "feedback_rating")
    Integer feedbackRating;

    @Column(name = "feedback_given")
    Boolean feedbackGiven = false;

    @Column(name = "feedback_opened")
    Boolean feedbackOpened = false;

}
