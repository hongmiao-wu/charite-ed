package com.wise23.chariteed.controller;

import com.wise23.chariteed.model.Encounters;
import com.wise23.chariteed.model.InstructionToPatient;
import com.wise23.chariteed.model.PatientData;
import com.wise23.chariteed.model.UserData;
import com.wise23.chariteed.model.dto.PatientFeedbackData;
import com.wise23.chariteed.repository.EncounterRepository;
import com.wise23.chariteed.service.InstructionToPatientService;
import com.wise23.chariteed.service.PatientService;

import com.wise23.chariteed.service.UserDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.InputStream;
import java.security.Principal;
import java.sql.Blob;
import java.sql.SQLException;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    PatientService patientService;

    @Autowired
    UserDataService userDataService;

    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    InstructionToPatientService instructionToPatientService;

    @Autowired
    EncounterRepository encounterRepository;

    String firstFeedbackMessage = "Your instruction requires feedback, please leave your feedback";
    // String secondFeedbackMessage = "Your instruction requires the second
    // feedback, please leave your feedback";

    @RequestMapping(value = "/eNumbers", method = RequestMethod.GET)
    public String eNumbers() {
        return "patient/dashboard/eNumbers";
    }

    @GetMapping("/download")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@RequestParam("encounterId") Long encounterId) {
        Encounters encounter = encounterRepository.findById(encounterId).orElse(null);

        Blob test = encounter.getFile();
        Resource doctorsLetter = convertBlobToResource(test);

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + "doctors_letter.pdf" + "\"").body(doctorsLetter);
    }

    private Resource convertBlobToResource(Blob blob) {
        InputStream inputStream;
        try {
            inputStream = blob.getBinaryStream();
        } catch (SQLException e) {
            throw new RuntimeException("Error getting binary stream from blob", e);
        }
        return new InputStreamResource(inputStream);
    }

    @GetMapping("/dashboard")
    public String showPatientById(Model model, Principal principal) {

        UserData patientUser = userDataService.getUserData(principal.getName());
        Long patientFhirID = Long.valueOf(patientUser.getFhirID()).longValue();

        PatientData patient = patientService.findById(patientUser.getId());

        List<Encounters> encounters = encounterRepository.findByPatient(patient);
        Collections.reverse(encounters);

        List<InstructionToPatient> instructionsWithoutFeedback = patientService
                .getInstructionsOfPatientWithoutFeedback(patientFhirID);

        List<InstructionToPatient> firstFeedbackInstructions = patientService
                .filterByFeedbackDeadline(instructionsWithoutFeedback, 1);

        PatientFeedbackData patientFeedbackData = new PatientFeedbackData();

        model.addAttribute("patientFeedbackData", patientFeedbackData);
        model.addAttribute("ratingDescription", patientService.getRatingDescription());
        model.addAttribute("encounters", encounters);
        model.addAttribute("patientData", patient);
        model.addAttribute("fhirPatient", patientUser);
        model.addAttribute("itpForFirstFeedbackIds",
                firstFeedbackInstructions.stream().map(InstructionToPatient::getId).collect(Collectors.toList()));
        model.addAttribute("firstFeedbackIsNeededMessage", firstFeedbackMessage);

        return "patient/dashboard";
    }

    @GetMapping("/view/all")
    public String showAllPatients(Model model) {
        List<PatientData> allPatients = patientService.getAllPatients();
        model.addAttribute("allPatients", allPatients);

        return "patient/allPatients";
    }

    @PostMapping("/patientComment/first")
    public String patientLeavesFirstFeedback(@ModelAttribute PatientFeedbackData feedbackData) {

        Long itpID = feedbackData.getInstructionToPatient().getId();
        InstructionToPatient dbITP = instructionToPatientService.getInstructionToPatientById(itpID);
        InstructionToPatient updatedITP = instructionToPatientService.updateInstructionFirstFeedback(dbITP,
                feedbackData);

        return "redirect:/patient/dashboard";
    }

    @PostMapping("/patientComment/second")
    public String patientLeavesSecondFeedback(@ModelAttribute PatientFeedbackData feedbackData) {

        Long itpID = feedbackData.getInstructionToPatient().getId();
        InstructionToPatient dbITP = instructionToPatientService.getInstructionToPatientById(itpID);
        InstructionToPatient updatedITP = instructionToPatientService.updateInstructionSecondFeedback(dbITP,
                feedbackData);

        return "redirect:/patient/dashboard";
    }

}
