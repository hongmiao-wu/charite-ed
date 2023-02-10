package com.wise23.chariteed.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "date_and_conditions")
public class Encounters {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "date_and_conditions_id")
    private Long id;

    @Column(name = "date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @Column(name = "conditions")
    @ElementCollection
    private List<String> conditions;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private PatientData patient;

    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(name = "date_and_conditions_to_instruction_to_patient", joinColumns = {
            @JoinColumn(name = "date_and_conditions_id") }, inverseJoinColumns = {
                    @JoinColumn(name = "instruction_to_patient_id") })
    private List<InstructionToPatient> instructions;
}
