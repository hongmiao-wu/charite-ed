package com.wise23.chariteed.controller;


import com.wise23.chariteed.model.Instruction;
import com.wise23.chariteed.model.PatientData;
import com.wise23.chariteed.model.PractitionerData;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
public class InstructionsToPatientForm {

    PractitionerData practitioner;

    PatientData patient;

    Set<Instruction> instructionSet = new HashSet<>();

    Integer firstFeedbackDays;

    Integer secondFeedbackDays;

    String practitionerComment;
}
