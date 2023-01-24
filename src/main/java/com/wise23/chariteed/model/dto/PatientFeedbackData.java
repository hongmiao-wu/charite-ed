package com.wise23.chariteed.model.dto;

import com.wise23.chariteed.model.InstructionToPatient;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PatientFeedbackData {

    InstructionToPatient instructionToPatient;
    String patientComment = "";
    Integer feedbackRating = 0;
}
