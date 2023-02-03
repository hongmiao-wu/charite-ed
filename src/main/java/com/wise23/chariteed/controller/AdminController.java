package com.wise23.chariteed.controller;

import com.wise23.chariteed.model.Role;
import com.wise23.chariteed.model.InstructionToPatient;
import com.wise23.chariteed.model.PatientData;
import com.wise23.chariteed.model.PractitionerGenerator;
import com.wise23.chariteed.model.UserData;
import com.wise23.chariteed.repository.PatientDataRepository;
import com.wise23.chariteed.repository.UserDataRepository;
import com.wise23.chariteed.service.PractitionerService;
import com.wise23.chariteed.service.PatientService;

import com.wise23.chariteed.service.UserDataService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class AdminController {

    Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    PatientService patientService;

    @Autowired
    UserDataService userDataService;

    @Autowired
    UserDataRepository userDataRepository;

    @Autowired
    PractitionerService practitionerService;

    @Autowired
    PatientDataRepository patientDataRepository;

    @RequestMapping(value = { "/admin/dashboard" }, method = RequestMethod.GET)
    public String adminHome(Model model) {

        List<UserData> practitioners = userDataRepository.findByRole(Role.PRACTITIONER);
        List<UserData> patients = userDataRepository.findByRole(Role.PATIENT);

        // for (int i = 0; i < practitioners.size(); i++) {
        // System.out.println("ID: " + practitioners.get(i).getId());

        // PractitionerData bla =
        // practitionerService.findById(practitioners.get(i).getId());

        // if (bla != null) {
        // System.out.println("WUH: " + bla.getFhirId());
        // }
        // }

        List<Set<InstructionToPatient>> instructionList = new ArrayList<Set<InstructionToPatient>>();

        for (int i = 0; i < patients.size(); i++) {
            UserData patient = patients.get(i);
            System.out.println(patient.getFirstName() + " " + patient.getId());
            PatientData patientData = patientService.findById(patient.getId());

            // model.addAttribute("instructions", patientData.getInstructions());

            instructionList.add(patientData.getInstructions());

            for (InstructionToPatient instruction : patientData.getInstructions()) {
                System.out.println("INSTRUCTION: " + instruction.getInstruction().getTitle());
                System.out.println("Comment: " + instruction.getPractitionerComment());
            }
        }

        model.addAttribute("practitioners", practitioners);
        model.addAttribute("patients", patients);
        model.addAttribute("ratingDescription", patientService.getRatingDescription());
        model.addAttribute("instructionsList", instructionList);

        return "/admin/dashboard";
    }

    @RequestMapping(value = { "/admin/dashboard/generateDoctor" }, method = RequestMethod.GET)
    public String generatePractitioner(Model model) {
        model.addAttribute("generateDoctor", new PractitionerGenerator());
        return "/admin/dashboard/generateDoctor";
    }

    @PostMapping("/admin/dashboard/generateDoctor")
    public String generatePractitionerSubmit(@ModelAttribute PractitionerGenerator generator, Model model) {

        String passwd = PractitionerService.generatePassword(generator.getFirstName(), generator.getLastName());

        generator.setPassword(passwd);

        UserData UserData = new UserData(generator.getFirstName(), generator.getLastName(), generator.getEmail(),
                passwd,
                generator.getPhoneNumber(), Role.PRACTITIONER);

        if (userDataRepository.existsByEmailAndMobile(UserData.getEmail(), UserData.getMobile())) {
            logger.error("ERROR: Doctors Account already exists!");
            return "/admin/dashboard/error";
        }

        try {
            userDataService.saveUserData(UserData);
        } catch (Exception e) {
            System.out.println("ERROR: Input has the wrong format.");
            return "/admin/dashboard/error";
        }

        model.addAttribute("doctor", generator);

        System.out.println("Doctor " + UserData.getLastName() + " created\nPassword is: " + generator.getPassword());

        return "/admin/dashboard/doctorGenerated";
    }

    @PostMapping("/admin/deleteUserData")
    public String deleteUserData(@RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("mobile") String mobile) {
        userDataService.deleteByFullNameAndMobile(firstName, lastName, mobile);
        return "redirect:/admin/dashboard";
    }
}
