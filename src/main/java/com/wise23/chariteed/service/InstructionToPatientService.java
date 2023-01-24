package com.wise23.chariteed.service;

import com.wise23.chariteed.model.InstructionToPatient;
import com.wise23.chariteed.model.dto.PatientFeedbackData;
import com.wise23.chariteed.repository.InstructionToPatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class InstructionToPatientService {

    @Autowired
    InstructionToPatientRepository instructionToPatientRepository;

    public InstructionToPatient assignInstructionToPatíent(InstructionToPatient instructionToPatient) {
        instructionToPatient.setGivenAt(LocalDateTime.now());

        return instructionToPatientRepository.save(instructionToPatient);
    }

    public InstructionToPatient getInstructionToPatientById(Long id) {
        return instructionToPatientRepository.getReferenceById(id);
    }

    public InstructionToPatient updateInstruction(InstructionToPatient itp, PatientFeedbackData feedbackData) {
        itp.setFeedbackGiven(true);
        itp.setFeedbackRating(feedbackData.getFeedbackRating());
        itp.setPatientComment(feedbackData.getPatientComment());
        return instructionToPatientRepository.save(itp);
    }

    public InstructionToPatient acknowledgeFeedback(InstructionToPatient itp) {
        itp.setFeedbackOpened(true);

        return instructionToPatientRepository.save(itp);
    }
}
