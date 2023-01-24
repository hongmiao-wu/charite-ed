package com.wise23.chariteed.controller;

import com.wise23.chariteed.model.InstructionToPatient;
import com.wise23.chariteed.model.PractitionerData;
import com.wise23.chariteed.service.InstructionService;
import com.wise23.chariteed.service.InstructionToPatientService;
import com.wise23.chariteed.service.PatientService;
import com.wise23.chariteed.service.PractitionerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/itp")
@Controller
public class InstructionToPatientController {

    @Autowired
    InstructionToPatientService instructionToPatientService;

    @Autowired
    PractitionerService practitionerService;

    @Autowired
    InstructionService instructionService;

    @Autowired
    PatientService patientService;

    @RequestMapping("/create/{practitionerFhirID}")
    public String generateInstructionToPatient(Model model, @PathVariable Long practitionerFhirID) {

        PractitionerData practitioner = practitionerService.findByFhirId(practitionerFhirID);

        if (practitioner == null) {
            return "redirect:/practitioner/view/all";
        }
        InstructionToPatient instructionToPatient = new InstructionToPatient();
        instructionToPatient.setPractitioner(practitioner);
        model.addAttribute("instructionToPatient", instructionToPatient);

        model.addAttribute("allInstructions", instructionService.getAllInstructions());
        model.addAttribute("allPatients", patientService.getAllPatients());

        return "/instructionToPatient/assign";
    }

    @RequestMapping("/assign")
    public String assignInstructionToPatient(@ModelAttribute InstructionToPatient instructionToPatient) {

        instructionToPatientService.assignInstructionToPatíent(instructionToPatient);

        return "redirect:/practitioner/view/" + instructionToPatient.getPractitioner().getFhirId();
    }


}
