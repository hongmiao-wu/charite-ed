package com.wise23.chariteed.controller;

import com.wise23.chariteed.model.InstructionToPatient;
import com.wise23.chariteed.model.PatientData;
import com.wise23.chariteed.service.PatientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.hl7.fhir.r4.model.Patient;

@Slf4j
@Controller
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    PatientService patientService;

    String feedbackMessage = "Your latest instruction requires a feedback, please leave your feedback";

    @GetMapping("/admin/patient/{id}")
    @ResponseBody
    public String getPatientById(@PathVariable("id") String id) {
        Patient patient = patientService.client.read().resource(Patient.class).withId(id).execute();
        return patientService.fhirContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(patient);
    }

    @GetMapping("/{fhir_ID}")
    public String showPatientById(@PathVariable Long fhir_ID, Model model) {

        try {
            Patient fhirPatient = patientService.client.read().resource(Patient.class).withId(fhir_ID).execute();
            PatientData patientData = patientService.findByFhirId(fhir_ID);

            model.addAttribute("fhirPatient", fhirPatient);
            model.addAttribute("patientData", patientData);
        }
        catch (Exception e) {
            log.debug(e.getMessage());
        }


        InstructionToPatient feedbackNeededForInstruction = patientService.timeForFeedback(fhir_ID);
        if (feedbackNeededForInstruction != null) {
            model.addAttribute("feedbackIsNeededMessage", feedbackMessage);
            model.addAttribute("instructionIDforFeedback", feedbackNeededForInstruction.getId());
        }

        return "patient/dashboard";
    }
}
