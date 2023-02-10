package com.wise23.chariteed.service;

import java.util.List;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Patient;
import com.wise23.chariteed.model.InstructionToPatient;
import com.wise23.chariteed.model.PatientData;
import com.wise23.chariteed.model.DateAndConditions;
import com.wise23.chariteed.model.Encounters;
import com.wise23.chariteed.repository.InstructionToPatientRepository;
import com.wise23.chariteed.repository.PatientDataRepository;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.*;
import com.wise23.chariteed.constant.GetPropertiesBean;
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

    public String getPatientData(String fhirID) {
        Patient patient = null;

        // In case that the ID is not existing.
        try {
            patient = this.client.read().resource(Patient.class).withId(fhirID).execute();
        } catch (Exception e) {
            System.out.println("ERROR: Patient ID does not exist.");
            return "/practitioner/dashboard/error";
        }

        return this.fhirContext.newJsonParser().setPrettyPrint(true)
                .encodeResourceToString(patient);
    }

    public List<DateAndConditions> getEncounter(String fhirID) {
        Bundle bundle = this.client
                .search()
                .forResource(Encounter.class)
                .where(Encounter.PATIENT.hasId(
                        fhirID))
                .returnBundle(Bundle.class)
                .execute();

        List<Bundle.BundleEntryComponent> entries = bundle.getEntry();

        if (entries.size() == 0) {
            System.out.println("ERROR: No condition available.");
            return null;
        }

        List<DateAndConditions> dateAndConditions = new ArrayList<>();

        for (Bundle.BundleEntryComponent entry : entries) {
            if (entry.getResource().fhirType().equals("Encounter")) {
                Encounter encounter = (Encounter) entry.getResource();
                dateAndConditions.add(new DateAndConditions(encounter.getPeriod().getEnd(),
                        getConditions(encounter.getIdElement().getIdPart())));
            }
        }

        for (DateAndConditions dateAndStrings : dateAndConditions) {
            System.out.println("DATE: " + dateAndStrings.getDate());
            System.out.println("CONDITIONS: " + dateAndStrings.getConditions());
        }

        return dateAndConditions;
    }

    private List<String> getConditions(String fhirID) {
        Bundle bundle = this.client
                .search()
                .forResource(Condition.class)
                .where(Condition.ENCOUNTER.hasId(fhirID))
                .returnBundle(Bundle.class)
                .execute();

        List<Bundle.BundleEntryComponent> entries = bundle.getEntry();

        if (entries.size() == 0) {
            System.out.println("ERROR: No condition available.");
            return null;
        }

        List<String> conditions = new ArrayList<>();

        for (Bundle.BundleEntryComponent entry : entries) {
            if (entry.getResource().fhirType().equals("Condition")) {
                Condition condition = (Condition) entry.getResource();
                conditions.add(condition.getCode().getCoding().get(0).getDisplay());
            }
        }

        return conditions;
    }

    public Encounters getDateAndConditionsData(List<DateAndConditions> dateAndConditions,
            List<InstructionToPatient> instructions, PatientData patient) {
        Encounters dateAndConditionsData = new Encounters();
        dateAndConditionsData.setDate(dateAndConditions.get(0).getDate());
        dateAndConditionsData.setConditions(dateAndConditions.get(0).getConditions());
        dateAndConditionsData.setInstructions(instructions);
        dateAndConditionsData.setPatient(patient);

        return dateAndConditionsData;
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

    public List<PatientData> getAllPatients() {
        return patientDataRepository.findAll();
    }

    public PatientData findByFhirId(Long fhirID) {
        return patientDataRepository.findPatientDataByFhirId(fhirID).orElse(null);
    }

    public InstructionToPatient getLatestInstruction(Long patientFhirID) {
        return instructionToPatientRepository.findLatestInstructionOfPatient(patientFhirID).orElse(null);
    }

    public List<InstructionToPatient> getInstructionsOfPatientWithoutFeedback(Long patientFhirID) {
        return instructionToPatientRepository
                .findInstructionsOfPatientByFeedbackGivenIsFalseOrSecondFeedbackGivenIsFalse(patientFhirID);
    }

    public List<InstructionToPatient> filterByFeedbackDeadline(List<InstructionToPatient> instructions, int wave) {
        return instructions.stream().filter(itp -> timeForFeedback(itp, wave)).collect(Collectors.toList());
    }

    public Boolean timeForFeedback(InstructionToPatient instructionToPatient, int wave) {
        if (instructionToPatient == null) {
            return false;
        }

        LocalDateTime instructionTime = instructionToPatient.getGivenAt();
        Long timePassed = ChronoUnit.DAYS.between(instructionTime, LocalDateTime.now());

        if (wave == 1) {
            return timePassed >= instructionToPatient.getFirstFeedbackDays();
        }
        if (wave == 2) {
            return timePassed >= instructionToPatient.getSecondFeedbackDays();
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

    public PatientData findById(Long ID) {
        return patientDataRepository.findById(ID).orElse(null);
    }
}