package com.wise23.chariteed.controller;

import com.wise23.chariteed.model.InstructionToPatient;
import com.wise23.chariteed.model.PatientData;
import com.wise23.chariteed.model.PractitionerData;
import com.wise23.chariteed.model.dto.PatientFeedbackData;
import com.wise23.chariteed.service.InstructionToPatientService;
import com.wise23.chariteed.service.PatientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.hl7.fhir.r4.model.Patient;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    PatientService patientService;

    @Autowired
    InstructionToPatientService instructionToPatientService;

    String feedbackMessage = "Your latest instruction requires a feedback, please leave your feedback";

    @GetMapping("/admin/patient/{id}")
    @ResponseBody
    public String getPatientById(@PathVariable("id") String id) {
        Patient patient = patientService.client.read().resource(Patient.class).withId(id).execute();
        return patientService.fhirContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(patient);
    }

    @GetMapping("/view/{patientFhirID}")
    public String showPatientById(@PathVariable Long patientFhirID, Model model) {

        try {
            Patient fhirPatient = patientService.client.read().resource(Patient.class).withId(patientFhirID).execute();
            PatientData patientData = patientService.findByFhirId(patientFhirID);

            model.addAttribute("fhirPatient", fhirPatient);
            model.addAttribute("patientData", patientData);
        }
        catch (Exception e) {
            log.debug(e.getMessage());
        }


        InstructionToPatient latestInstruction = patientService.getLatestInstruction(patientFhirID);
        if (latestInstruction != null) {
            model.addAttribute("latestInstructionID", latestInstruction.getId());
        }

        if (patientService.timeForFeedback(latestInstruction)) {
            model.addAttribute("feedbackIsNeededMessage", feedbackMessage);
            model.addAttribute("instructionIDforFeedback", latestInstruction.getId());
        }


        if (latestInstruction != null && !latestInstruction.getFeedbackGiven()) {
            PatientFeedbackData patientFeedbackData = new PatientFeedbackData();
            patientFeedbackData.setInstructionToPatient(latestInstruction);
            patientFeedbackData.setFeedbackRating(latestInstruction.getFeedbackRating());
            patientFeedbackData.setPatientComment(latestInstruction.getPatientComment());
            model.addAttribute("patientFeedbackData", patientFeedbackData);
        }

        model.addAttribute("ratingDescription", patientService.getRatingDescription());


        return "patient/dashboard";
    }

    @GetMapping("/view/all")
    public String showAllPatients(Model model) {
        List<PatientData> allPatients = patientService.getAllPatients();
        model.addAttribute("allPatients", allPatients);

        return "patient/allPatients";
    }

    @PostMapping("/patientComment")
    public String patientLeavesFeedback(@ModelAttribute PatientFeedbackData feedbackData) {

        Long itpID = feedbackData.getInstructionToPatient().getId();
        InstructionToPatient dbITP = instructionToPatientService.getInstructionToPatientById(itpID);
        InstructionToPatient updatedITP = instructionToPatientService.updateInstruction(dbITP, feedbackData);

        return "redirect:/patient/view/" + dbITP.getPatient().getFhirId();

    }
}
