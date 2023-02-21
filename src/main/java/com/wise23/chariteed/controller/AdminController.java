package com.wise23.chariteed.controller;

import com.wise23.chariteed.model.Role;
import com.wise23.chariteed.model.Encounters;
import com.wise23.chariteed.model.InstructionToPatient;
import com.wise23.chariteed.model.PatientData;
import com.wise23.chariteed.model.PractitionerGenerator;
import com.wise23.chariteed.model.UserData;
import com.wise23.chariteed.repository.EncounterRepository;
import com.wise23.chariteed.repository.PatientDataRepository;
import com.wise23.chariteed.repository.UserDataRepository;
import com.wise23.chariteed.service.PractitionerService;
import com.wise23.chariteed.service.InstructionToPatientService;
import com.wise23.chariteed.service.PatientService;

import com.wise23.chariteed.service.UserDataService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

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

    @Autowired
    InstructionToPatientService instructionToPatientService;

    @Autowired
    EncounterRepository encounterRepository;

    @RequestMapping(value = { "/admin/dashboard" }, method = RequestMethod.GET)
    public String adminHome(Model model) {

        List<UserData> practitioners = userDataRepository.findByRole(Role.PRACTITIONER);
        List<UserData> patients = userDataRepository.findByRole(Role.PATIENT);

        model.addAttribute("practitioners", practitioners);
        model.addAttribute("patients", patients);

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

    @GetMapping("/admin/showPatient")
    public String showUser(@RequestParam("patientID") Long patientID, Model model) {
        PatientData patient = patientService.findById(patientID);
        Optional<UserData> userData = userDataRepository.findById(patient.getId());
        List<Encounters> encounters = encounterRepository.findByPatient(patient);

        model.addAttribute("patient", userData);
        model.addAttribute("encounters", encounters);
        model.addAttribute("ratingDescription", patientService.getRatingDescription());
        return "/admin/showPatient";
    }

    @RequestMapping("admin/itp-acknowledged/{itpID}")
    public String acknowledgeFeedback(@PathVariable Long itpID) {

        InstructionToPatient dbITP = instructionToPatientService.getInstructionToPatientById(itpID);
        instructionToPatientService.acknowledgeFeedback(dbITP);

        return "redirect:/admin/dashboard";

    }
}
