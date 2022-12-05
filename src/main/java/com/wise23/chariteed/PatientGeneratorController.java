package com.wise23.chariteed;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import org.hl7.fhir.r4.model.Patient;

import com.google.gson.*;
import com.google.zxing.WriterException;

import java.util.Random;
import java.io.File;
import java.io.IOException;

@Controller
public class PatientGeneratorController {

    PatientService patientService = new PatientService();

    @GetMapping("/generatePatient")
    public String generatePatientForm(Model model) {
        model.addAttribute("generatePatient", new PatientGenerator());
        return "generatePatient";
    }

    @PostMapping("/generatePatient")
    public String generatePatientSubmit(@ModelAttribute PatientGenerator generator, Model model)
            throws WriterException, IOException {
        String id = Long.toString(generator.getId());
        model.addAttribute("generatePatient", generator);

        Patient patient = patientService.client.read().resource(Patient.class).withId(id).execute();
        String patientData = patientService.fhirContext.newJsonParser().setPrettyPrint(true)
                .encodeResourceToString(patient);

        String passwd = generatePassword(patient, patientData);

        // QR code generation
        // This is still the local url
        String patient_url = "http://localhost:8081/patient/" + id;
        String fileName = "url_patient_" + id + ".png";
        int size = 125;
        String fileType = "png";
        File qrFile = new File(fileName);
        QRCodeGenerator.createQRImage(qrFile, patient_url, size, fileType);

        System.out.println("URL is: " + patient_url + "\n" + "Password is: " + passwd);

        return "result";
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