package com.wise23.chariteed.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name ="instruction_to_patient")
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

    @Column(name = "feedback_in_days")
    Integer feedbackInDays;

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
