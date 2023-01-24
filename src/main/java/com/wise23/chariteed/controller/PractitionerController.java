package com.wise23.chariteed.controller;

import com.wise23.chariteed.model.InstructionToPatient;
import com.wise23.chariteed.model.PractitionerData;
import com.wise23.chariteed.service.InstructionToPatientService;
import com.wise23.chariteed.service.PatientService;
import com.wise23.chariteed.service.PractitionerService;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Practitioner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/practitioner")
public class PractitionerController {

    @Autowired
    PractitionerService practitionerService;

    @Autowired
    PatientService patientService;

    @Autowired
    InstructionToPatientService instructionToPatientService;


    @GetMapping("/view/all")
    public String showAllPractitioners(Model model) {
        List<PractitionerData> practitioners = practitionerService.getAllPractitioners();
        model.addAttribute("practitioners", practitioners);

        return "practitioner/allPractitioners";

    }

    @GetMapping("/view/{fhirID}")
    public String showPractitioner(@PathVariable Long fhirID, Model model) {

        try {
            Practitioner fhirPractitioner = practitionerService.client.read().resource(Practitioner.class).withId(fhirID).execute();
            PractitionerData practitionerData = practitionerService.findByFhirId(fhirID);

            model.addAttribute("fhirPractitioner", fhirPractitioner);
            model.addAttribute("practitionerData", practitionerData);
        }
        catch (Exception e) {
            log.debug(e.getMessage());
        }

        model.addAttribute("ratingDescription", patientService.getRatingDescription());

        return "/practitioner/instructionsGiven";
    }

    @RequestMapping("/itp-acknowledged/{itpID}")
    public String acknowledgeFeedback(@PathVariable Long itpID) {

        InstructionToPatient dbITP = instructionToPatientService.getInstructionToPatientById(itpID);
        instructionToPatientService.acknowledgeFeedback(dbITP);

        return "redirect:/practitioner/view/" + dbITP.getPractitioner().getFhirId();

    }


}
