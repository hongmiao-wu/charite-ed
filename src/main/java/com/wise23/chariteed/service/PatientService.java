package com.wise23.chariteed.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.*;
import com.wise23.chariteed.constant.GetPropertiesBean;
import com.wise23.chariteed.config.FhirConfig;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.springframework.stereotype.Service;

@Service
public class PatientService {
    public FhirContext fhirContext;
    public IGenericClient client;

    @Autowired
    public PatientService() {
        this.fhirContext = FhirConfig.getFhirContext();
        this.client = fhirContext.newRestfulGenericClient(GetPropertiesBean.getTestserverURL());
    }

    public String getPatientData(String fhirID) {
        Patient patient = null;

        // In case that the ID is not existing.
        try {
            patient = this.client.read().resource(Patient.class).withId(fhirID).execute();
        } catch (Exception e) {
            System.out.println("ERROR: Patient ID does not exist.");
            return "/doctor/dashboard/error";
        }

        return this.fhirContext.newJsonParser().setPrettyPrint(true)
                .encodeResourceToString(patient);
    }

    public List<String> getCondition(String fhirID) {
        List<String> conditionsList = new ArrayList<>();

        Bundle bundle = this.client
                .search()
                .forResource(Condition.class)
                .where(Condition.PATIENT.hasId(
                        fhirID))
                .returnBundle(Bundle.class)
                .execute();

        List<Bundle.BundleEntryComponent> entries = bundle.getEntry();

        if (entries.size() == 0) {
            System.out.println("ERROR: No condition available.");
            return null;
        }

        for (Bundle.BundleEntryComponent entry : entries) {
            if (entry.getResource().fhirType().equals("Condition")) {
                Condition condition = (Condition) entry.getResource();
                conditionsList.add(condition.getCode().getCoding().get(0).getDisplay());
            }
        }

        return conditionsList;
    }

    public String generatePassword(String patientData) {
        // Parse the JSON data into a JsonElement
        JsonElement rootElement = JsonParser.parseString(patientData);

        // Convert the JsonElement to a JsonObject
        JsonObject rootObject = rootElement.getAsJsonObject();

        // Get the name array from the root object
        JsonArray nameArray = rootObject.getAsJsonArray("name");

        // Get the first element of the name array (since there is only one name object
        // in this example)
        JsonObject nameObject = nameArray.get(0).getAsJsonObject();

        // Get the family and given fields from the name object
        String family = nameObject.get("family").getAsString();
        String given = nameObject.get("given").getAsJsonArray().get(0).getAsString();

        // Get the birthDate field from the root object
        String birthDate = rootObject.get("birthDate").getAsString();

        // Extract the year from the birthDate
        String year = birthDate.substring(0, 4);

        // Generate a random four-digit number
        Random rand = new Random();
        int random_id = rand.nextInt(9000) + 1000;

        // Concatenate the fields into a single string
        return given + "_" + family + "_" + year + "_" + random_id;
    }
}