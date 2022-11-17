package com.wise23.chariteed;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.hl7.fhir.r4.model.Patient;

@RestController
public class PatientController {

    PatientService patientService = new PatientService();

    @GetMapping("/patient/{id}")
    public String getPatientById(@PathVariable("id") String id) {
        Patient patient = patientService.client.read().resource(Patient.class).withId(id).execute();
        return patientService.fhirContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(patient);
    }
}
