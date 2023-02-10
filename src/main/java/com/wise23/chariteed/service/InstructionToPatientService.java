package com.wise23.chariteed.service;

import com.wise23.chariteed.controller.InstructionsToPatientForm;
import com.wise23.chariteed.model.Instruction;
import com.wise23.chariteed.model.InstructionToPatient;
import com.wise23.chariteed.model.dto.PatientFeedbackData;
import com.wise23.chariteed.repository.InstructionToPatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class InstructionToPatientService {

    @Autowired
    InstructionToPatientRepository instructionToPatientRepository;

    public List<InstructionToPatient> handleForm(InstructionsToPatientForm form) {
        List<InstructionToPatient> instructionList = new ArrayList<InstructionToPatient>();
        for (Instruction instruction : form.getInstructionSet()) {
            InstructionToPatient itp = new InstructionToPatient();
            itp.setPatient(form.getPatient());
            itp.setInstruction(instruction);
            itp.setGivenAt(LocalDateTime.now());
            itp.setPractitionerComment(form.getPractitionerComment());

            if (form.getFirstFeedbackDays() == null) {
                // Should probably be 7 days in the future
                itp.setFirstFeedbackDays(0);
            } else {
                itp.setFirstFeedbackDays(form.getFirstFeedbackDays());
            }

            // if (form.getSecondFeedbackDays() == null) {
            // // Should probably be 7 days in the future
            // itp.setSecondFeedbackDays(0);
            // } else {
            // itp.setSecondFeedbackDays(form.getSecondFeedbackDays());
            // }

            itp = instructionToPatientRepository.save(itp);
            instructionList.add(itp);
        }
        return instructionList;
    }

    public InstructionToPatient getInstructionToPatientById(Long id) {
        return instructionToPatientRepository.getReferenceById(id);
    }

    public InstructionToPatient updateInstructionFirstFeedback(InstructionToPatient itp,
            PatientFeedbackData feedbackData) {
        itp.setFirstFeedbackGiven(true);
        itp.setFirstFeedbackRating(feedbackData.getFeedbackRating());
        itp.setFirstPatientComment(feedbackData.getPatientComment());
        return instructionToPatientRepository.save(itp);
    }

    public InstructionToPatient updateInstructionSecondFeedback(InstructionToPatient itp,
            PatientFeedbackData feedbackData) {
        itp.setSecondFeedbackGiven(true);
        itp.setSecondFeedbackRating(feedbackData.getFeedbackRating());
        itp.setSecondPatientComment(feedbackData.getPatientComment());
        return instructionToPatientRepository.save(itp);
    }

    public InstructionToPatient acknowledgeFeedback(InstructionToPatient itp) {

        if (itp.getFirstFeedbackGiven()) {
            itp.setFirstFeedbackOpened(true);
        }
        if (itp.getSecondFeedbackGiven()) {
            itp.setSecondFeedbackOpened(true);
        }

        return instructionToPatientRepository.save(itp);
    }
}
