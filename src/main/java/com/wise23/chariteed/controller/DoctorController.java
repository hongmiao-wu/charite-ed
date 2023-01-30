package com.wise23.chariteed.controller;

import com.wise23.chariteed.constant.QRCodeGenerator;
import com.wise23.chariteed.model.Role;
import com.wise23.chariteed.model.User;
import com.wise23.chariteed.repository.UserRepository;
import com.wise23.chariteed.model.PatientGenerator;
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

import com.google.gson.*;
import com.google.zxing.WriterException;

import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;
import java.util.List;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

@Controller
public class DoctorController {

    PatientService patientService = new PatientService();

    Logger logger = LoggerFactory.getLogger(DoctorController.class);

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @RequestMapping(value = { "/doctor/dashboard" }, method = RequestMethod.GET)
    public String doctorHome(Principal principal, Model model) {
        User doctor = userService.getUser(principal.getName());
        model.addAttribute("doctor", doctor);
        return "doctor/dashboard";
    }

    @RequestMapping(value = { "/doctor/dashboard/generatePatient" }, method = RequestMethod.GET)
    public String generatePatient(Model model) {
        model.addAttribute("generatePatient", new PatientGenerator());
        return "/doctor/dashboard/generatePatient";
    }

    @PostMapping("/doctor/dashboard/generatePatient")
    public String generatePatientSubmit(@ModelAttribute PatientGenerator generator, Model model)
            throws WriterException, IOException, SerialException, SQLException {
        String id = Long.toString(generator.getId());
        model.addAttribute("patient", generator);

        String patientData = patientService.getPatientData(id);
        List<String> patientCondition = patientService.getCondition(id);

        if (patientCondition == null) {
            return "/doctor/dashboard/error";
        }

        JsonObject name = JsonParser.parseString(patientData).getAsJsonObject().get("name").getAsJsonArray().get(0)
                .getAsJsonObject();

        String firstName = name.get("given").getAsJsonArray().get(0).getAsString();
        String lastName = name.get("family").getAsString();

        User user = userRepository.findByFirstNameAndLastName(firstName, lastName);

        if (user == null) {
            // User creation with lots of dummy data
            generator.setPassword(patientService.generatePassword(patientData));
            byte[] file_bytes = generator.getFile().getBytes();
            user = new User(firstName, lastName, "test@mail.de", generator.getPassword(), "2222222222",
                    Role.PATIENT,
                    id, new SerialBlob(file_bytes), patientCondition);

            if (userRepository.existsByEmailAndMobile(user.getEmail(), user.getMobile())) {
                logger.error("ERROR: Patient Account already exists!");
                return "/doctor/dashboard/error";
            }

            userService.saveUser(user);
        } else if (user.getConditions().get(0) != patientCondition.get(0)) {
            user.setConditions(patientCondition);
            userRepository.save(user);
        }

        // QR code generation
        QRCodeGenerator.createQRImage(id);

        System.out.println("Patient " + user.getLastName() + " created\nPassword is: " + generator.getPassword());

        return "/doctor/dashboard/patientGenerated";
    }
}
