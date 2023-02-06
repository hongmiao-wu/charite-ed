package com.wise23.chariteed.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "instruction_to_patient")
public class InstructionToPatient {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "instruction_to_patient_id", unique = true, nullable = false)
    private Long id;

    /*
     * @ManyToMany
     * 
     * @JoinTable(name = "instruction_to_patient_has_instructions",
     * joinColumns = {@JoinColumn(name = "instruction_to_patient_id",
     * referencedColumnName = "instruction_to_patient_id")},
     * inverseJoinColumns = {@JoinColumn(name = "instruction_id",
     * referencedColumnName = "instruction_id")}
     * )
     * Set<Instruction> instructionSet = new HashSet<>();
     */

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "instruction_id")
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

    @Column(name = "first_feedback_days")
    Integer firstFeedbackDays;

    @Column(name = "first_feedback_rating")
    Integer firstFeedbackRating = null;

    @Column(name = "first_patient_comment")
    String firstPatientComment;

    @Column(name = "first_feedback_given")
    Boolean firstFeedbackGiven = false;

    @Column(name = "first_feedback_opened")
    Boolean firstFeedbackOpened = false;

    @Column(name = "second_feedback_days")
    Integer secondFeedbackDays;

    @Column(name = "second_feedback_rating")
    Integer secondFeedbackRating;

    @Column(name = "second_patient_comment")
    String secondPatientComment;

    @Column(name = "second_feedback_given")
    Boolean secondFeedbackGiven = false;

    @Column(name = "second_feedback_opened")
    Boolean secondFeedbackOpened = false;

}
