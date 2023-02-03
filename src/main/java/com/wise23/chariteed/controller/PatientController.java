package com.wise23.chariteed.controller;

import com.wise23.chariteed.model.InstructionToPatient;
import com.wise23.chariteed.model.PatientData;
import com.wise23.chariteed.model.UserData;
import com.wise23.chariteed.model.dto.PatientFeedbackData;
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

import javax.servlet.http.HttpServletResponse;
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

    String firstFeedbackMessage = "Your instruction requires the first feedback, please leave your feedback";
    String secondFeedbackMessage = "Your instruction requires the second feedback, please leave your feedback";

    @RequestMapping(value = "/eNumbers", method = RequestMethod.GET)
    public String eNumbers() {
        return "patient/dashboard/eNumbers";
    }

    @GetMapping("/download")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(HttpServletResponse response, Principal principal) {

        UserData user = userDataService.getUserData(principal.getName());
        Blob test = user.getFile();

        Resource file = convertBlobToResource(test);

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + "doctors_letter.pdf" + "\"").body(file);
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

        UserData patient = userDataService.getUserData(principal.getName());
        Long patientFhirID = Long.valueOf(patient.getFhirID()).longValue();

        PatientData patientData = patientService.findById(patient.getId());

        model.addAttribute("patientData", patientData);
        model.addAttribute("fhirPatient", patient);

        List<InstructionToPatient> instructionsWithoutFeedback = patientService
                .getInstructionsOfPatientWithoutFeedback(patientFhirID);

        List<InstructionToPatient> firstFeedbackInstructions = patientService
                .filterByFeedbackDeadline(instructionsWithoutFeedback, 1);
        model.addAttribute("instructionsForFirstFeedback", firstFeedbackInstructions);
        model.addAttribute("itpForFirstFeedbackIds",
                firstFeedbackInstructions.stream().map(InstructionToPatient::getId).collect(Collectors.toList()));
        model.addAttribute("firstFeedbackIsNeededMessage", firstFeedbackMessage);

        List<InstructionToPatient> secondFeedbackInstructions = patientService
                .filterByFeedbackDeadline(instructionsWithoutFeedback, 2);
        model.addAttribute("instructionsForSecondFeedback", secondFeedbackInstructions);
        model.addAttribute("itpForSecondFeedbackIds",
                secondFeedbackInstructions.stream().map(InstructionToPatient::getId).collect(Collectors.toList()));
        model.addAttribute("secondFeedbackIsNeededMessage", secondFeedbackMessage);

        PatientFeedbackData patientFeedbackData = new PatientFeedbackData();
        model.addAttribute("patientFeedbackData", patientFeedbackData);

        model.addAttribute("ratingDescription", patientService.getRatingDescription());

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
