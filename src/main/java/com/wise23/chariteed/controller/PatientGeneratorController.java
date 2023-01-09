package com.wise23.chariteed.controller;

import com.wise23.chariteed.QRCodeGenerator;
import com.wise23.chariteed.model.Role;
import com.wise23.chariteed.model.User;
import com.wise23.chariteed.model.PatientGenerator;
import com.wise23.chariteed.service.PatientService;
import com.wise23.chariteed.service.UserService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
@RequestMapping("admin/dashboard")
public class PatientGeneratorController {

    PatientService patientService = new PatientService();

    Logger logger = LoggerFactory.getLogger(PatientGeneratorController.class);

    @Autowired
    UserService userService;

    @GetMapping("/generatePatient")
    public String generatePatient(Model model) {
        model.addAttribute("generatePatient", new PatientGenerator());
        return "admin/dashboard/generatePatient";
    }

    @PostMapping("/generatePatient")
    public String generatePatientSubmit(@ModelAttribute PatientGenerator generator, Model model)
            throws WriterException, IOException, SerialException, SQLException {
        String id = Long.toString(generator.getId());
        model.addAttribute("generatePatient", generator);

        Patient patient = null;

        // In case that the ID is not existing.
        try {
            patient = patientService.client.read().resource(Patient.class).withId(id).execute();
        } catch (Exception e) {
            System.out.println("ERROR: Patient ID does not exist.");
            return "admin/dashboard/error";
        }

        String patientData = patientService.fhirContext.newJsonParser().setPrettyPrint(true)
                .encodeResourceToString(patient);

        generator.setPassword(patientService.generatePassword(patientData));

        // QR code generation
        QRCodeGenerator.createQRImage(id);

        JsonObject name = JsonParser.parseString(patientData).getAsJsonObject().get("name").getAsJsonArray().get(0)
                .getAsJsonObject();

        User user = new User();
        // Lot of dummy data
        user.setFirstName(name.get("given").getAsJsonArray().get(0).getAsString());
        user.setLastName(name.get("family").getAsString());
        user.setMobile("2222222222");
        user.setEmail("test@mail.de");
        user.setPassword(generator.getPassword());
        user.setRole(Role.USER);
        user.setID(id);

        byte[] file_bytes = generator.getFile().getBytes();
        user.setFile(new SerialBlob(file_bytes));

        if (userService.isUserPresent(user)) {
            logger.error("ERROR: Patient Account already exists!");
            return "admin/dashboard/generatePatient";
        }

        userService.saveUser(user);
        System.out.println("User " + user.getFirstName() + " created\n Password is: " + generator.getPassword());

        return "admin/dashboard/patientGenerated";
    }
}