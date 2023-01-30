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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.hl7.fhir.r4.model.Patient;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


@Slf4j
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

    String feedbackMessage = "Your latest instruction requires a feedback, please leave your feedback";



    @RequestMapping(method = RequestMethod.GET)
    public String homePage(Principal principal, Model model) {
        UserData patient = userDataService.getUserData(principal.getName());
        model.addAttribute("patient", patient);
        return "patient/dashboard";
    }

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
