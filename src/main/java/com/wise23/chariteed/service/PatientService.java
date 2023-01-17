package com.wise23.chariteed.service;

import java.util.Random;

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