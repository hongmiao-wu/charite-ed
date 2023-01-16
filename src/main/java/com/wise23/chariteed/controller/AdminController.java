package com.wise23.chariteed.controller;

import com.wise23.chariteed.model.Role;
import com.wise23.chariteed.model.User;
import com.wise23.chariteed.repository.UserRepository;
import com.wise23.chariteed.model.DoctorGenerator;
import com.wise23.chariteed.service.DoctorService;
import com.wise23.chariteed.service.PatientService;
import com.wise23.chariteed.service.UserService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.beans.factory.annotation.Autowired;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class AdminController {

    PatientService patientService = new PatientService();

    Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @RequestMapping(value = { "/admin/dashboard" }, method = RequestMethod.GET)
    public String adminHome() {
        return "/admin/dashboard";
    }

    @RequestMapping(value = { "/admin/dashboard/generateDoctor" }, method = RequestMethod.GET)
    public String generatePatient(Model model) {
        model.addAttribute("generateDoctor", new DoctorGenerator());
        return "/admin/dashboard/generateDoctor";
    }

    @PostMapping("/admin/dashboard/generateDoctor")
    public String generatePatientSubmit(@ModelAttribute DoctorGenerator generator, Model model) {

        String passwd = DoctorService.generatePassword(generator.getFirstName(), generator.getLastName());

        generator.setPassword(passwd);

        User user = new User(generator.getFirstName(), generator.getLastName(), generator.getEmail(), passwd,
                generator.getPhoneNumber(), Role.DOCTOR);

        if (userRepository.existsByEmailAndMobile(user.getEmail(), user.getMobile())) {
            logger.error("ERROR: Doctors Account already exists!");
            return "/admin/dashboard/error";
        }

        try {
            userService.saveUser(user);
        } catch (Exception e) {
            System.out.println("ERROR: Input has the wrong format.");
            return "/admin/dashboard/error";
        }

        model.addAttribute("doctor", generator);

        System.out.println("Doctor " + user.getLastName() + " created\nPassword is: " + generator.getPassword());

        return "/admin/dashboard/doctorGenerated";
    }
}
