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

import org.hl7.fhir.r4.model.Patient;

import com.google.gson.*;
import com.google.zxing.WriterException;

import java.io.IOException;
import java.sql.SQLException;

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
    public String doctorHome() {
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

        Patient patient = null;

        // In case that the ID is not existing.
        try {
            patient = patientService.client.read().resource(Patient.class).withId(id).execute();
        } catch (Exception e) {
            System.out.println("ERROR: Patient ID does not exist.");
            return "/doctor/dashboard/error";
        }

        String patientData = patientService.fhirContext.newJsonParser().setPrettyPrint(true)
                .encodeResourceToString(patient);

        generator.setPassword(patientService.generatePassword(patientData));

        // QR code generation
        QRCodeGenerator.createQRImage(id);

        JsonObject name = JsonParser.parseString(patientData).getAsJsonObject().get("name").getAsJsonArray().get(0)
                .getAsJsonObject();

        // User creation with lots of dummy data
        byte[] file_bytes = generator.getFile().getBytes();
        User user = new User(name.get("given").getAsJsonArray().get(0).getAsString(), name.get("family").getAsString(),
                "test@mail.de", generator.getPassword(), "2222222222", Role.PATIENT, id,
                new SerialBlob(file_bytes));

        if (userRepository.existsByEmailAndMobile(user.getEmail(), user.getMobile())) {
            logger.error("ERROR: Patient Account already exists!");
            return "/doctor/dashboard/error";
        }

        userService.saveUser(user);
        System.out.println("Patient " + user.getLastName() + " created\nPassword is: " + generator.getPassword());

        return "/doctor/dashboard/patientGenerated";
    }
}
