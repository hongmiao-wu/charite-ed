package com.wise23.chariteed.service;

import java.util.Random;

import com.google.gson.*;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;

public class PatientService {
    public FhirContext fhirContext;
    public IGenericClient client;

    public PatientService() {
        this.fhirContext = FhirContext.forR4();
        this.client = fhirContext.newRestfulGenericClient("https://hapi.fhir.org/baseR4");
    }

    public String generatePassword(String patientData) {
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