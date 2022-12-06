package com.wise23.chariteed.controller;

import com.wise23.chariteed.QRCodeGenerator;
import com.wise23.chariteed.service.PatientGenerator;
import com.wise23.chariteed.service.PatientService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import org.hl7.fhir.r4.model.Patient;

import com.google.gson.*;
import com.google.zxing.WriterException;

import java.util.Random;
import java.io.IOException;

@Controller
public class PatientGeneratorController {

    PatientService patientService = new PatientService();

    @GetMapping("admin/dashboard/generatePatient")
    public String generatePatientForm(Model model) {
        model.addAttribute("generatePatient", new PatientGenerator());
        return "admin/dashboard/generatePatient";
    }

    @PostMapping("admin/dashboard/generatePatient")
    public String generatePatientSubmit(@ModelAttribute PatientGenerator generator, Model model)
            throws WriterException, IOException {
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

        String passwd = generatePassword(patient, patientData);
        generator.setPassword(passwd);

        // QR code generation
        QRCodeGenerator.createQRImage(id);

        System.out.println("Password is: " + passwd);

        return "admin/dashboard/patientGenerated";
    }

    private String generatePassword(Patient patient, String patientData) {
        JsonObject name = JsonParser.parseString(patientData).getAsJsonObject().get("name").getAsJsonArray().get(0)
                .getAsJsonObject();
        String birthYear = JsonParser.parseString(patientData).getAsJsonObject().get("birthDate").getAsString()
                .split("-")[0];
        String firstName = name.get("given").getAsJsonArray().get(0).getAsString();
        String surname = name.get("family").getAsString();
        Random random = new Random();
        String random_id = String.format("%04d", random.nextInt(10000));

        return firstName + "_" + surname + "_" + birthYear + "_" + random_id;
    }

}