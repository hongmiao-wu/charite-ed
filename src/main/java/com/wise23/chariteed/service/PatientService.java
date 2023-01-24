package com.wise23.chariteed.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.wise23.chariteed.model.InstructionToPatient;
import com.wise23.chariteed.model.PatientData;
import com.wise23.chariteed.repository.InstructionToPatientRepository;
import com.wise23.chariteed.repository.PatientDataRepository;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.*;
import com.wise23.chariteed.GetPropertiesBean;
import com.wise23.chariteed.config.FhirConfig;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.springframework.stereotype.Service;

@Service
public class PatientService {

    @Autowired
    PatientDataRepository patientDataRepository;

    @Autowired
    InstructionToPatientRepository instructionToPatientRepository;

    public FhirContext fhirContext;
    public IGenericClient client;

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

    public List<PatientData> getAllPatients(){
        return patientDataRepository.findAll();
    }

    public PatientData findByFhirId(Long fhirID) {
        return patientDataRepository.findPatientDataByFhirId(fhirID).orElse(null);
    }

    public InstructionToPatient getLatestInstruction(Long patientFhirID) {
        return instructionToPatientRepository.findLatestInstructionOfPatient(patientFhirID).orElse(null);
    }

    public Boolean timeForFeedback(InstructionToPatient instructionToPatient) {
        if (instructionToPatient == null) {
            return false;
        }

        LocalDateTime instructionTime = instructionToPatient.getGivenAt();
        Long timePassed = ChronoUnit.DAYS.between(instructionTime, LocalDateTime.now());
        if (timePassed >= instructionToPatient.getFeedbackInDays()) {
            return true;
        }

        return false;
    }

    public Map<Integer, String> getRatingDescription() {
        Map<Integer, String> ratingDescription = new HashMap<>();
        ratingDescription.put(1, "Significantly worse");
        ratingDescription.put(2, "Slightly worse");
        ratingDescription.put(3, "Neutral");
        ratingDescription.put(4, "Slightly better");
        ratingDescription.put(5, "Significantly better");
        return ratingDescription;
    }
}