package com.wise23.chariteed.controller;

import com.wise23.chariteed.model.Instruction;
import com.wise23.chariteed.model.PatientData;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class InstructionsToPatientForm {

    PatientData patient;

    Set<Instruction> instructionSet = new HashSet<>();

    Integer firstFeedbackDays;

    Integer secondFeedbackDays;

    String practitionerComment;

    private long id;

    private MultipartFile file;

    private String password;
}
