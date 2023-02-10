package com.wise23.chariteed.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.zxing.WriterException;
import com.wise23.chariteed.constant.QRCodeGenerator;
import com.wise23.chariteed.model.*;
import com.wise23.chariteed.repository.EncounterRepository;
import com.wise23.chariteed.repository.PatientDataRepository;
import com.wise23.chariteed.service.InstructionService;
import com.wise23.chariteed.service.InstructionToPatientService;
import com.wise23.chariteed.service.PatientService;
import com.wise23.chariteed.service.PractitionerService;
import com.wise23.chariteed.service.UserDataService;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Practitioner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;
import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

@Slf4j
@Controller
@RequestMapping("/practitioner")
public class PractitionerController {

    @Autowired
    PractitionerService practitionerService;

    @Autowired
    PatientService patientService;

    @Autowired
    InstructionToPatientService instructionToPatientService;

    @Autowired
    UserDataService userDataService;

    @Autowired
    InstructionService instructionService;

    @Autowired
    PatientDataRepository patientDataRepository;

    @Autowired
    EncounterRepository encounterRepository;

    Logger logger = LoggerFactory.getLogger(PractitionerController.class);

    @GetMapping("/view/all")
    public String showAllPractitioners(Model model) {
        List<PractitionerData> practitioners = practitionerService.getAllPractitioners();
        model.addAttribute("practitioners", practitioners);

        return "practitioner/allPractitioners";

    }

    @GetMapping("/view/{fhirID}")
    public String showPractitioner(@PathVariable Long fhirID, Model model) {

        try {
            Practitioner fhirPractitioner = practitionerService.client.read().resource(Practitioner.class)
                    .withId(fhirID).execute();
            PractitionerData practitionerData = practitionerService.findByFhirId(fhirID);

            model.addAttribute("fhirPractitioner", fhirPractitioner);
            model.addAttribute("practitionerData", practitionerData);
        } catch (Exception e) {
            log.debug(e.getMessage());
        }

        model.addAttribute("ratingDescription", patientService.getRatingDescription());

        return "/practitioner/instructionsGiven";
    }

    @RequestMapping(value = { "/dashboard" }, method = RequestMethod.GET)
    public String doctorHome(Principal principal, Model model) {
        UserData doctor = userDataService.getUserData(principal.getName());
        List<Instruction> allInstructions = instructionService.getAllInstructions();
        model.addAttribute("doctor", doctor);
        model.addAttribute("allInstructions", allInstructions);
        return "/practitioner/dashboard";
    }

    @RequestMapping(value = { "/dashboard/generatePatient" }, method = RequestMethod.GET)
    public String generatePatient(Model model) {
        model.addAttribute("generatePatient", new PatientGenerator());
        return "/practitioner/dashboard/generatePatient";
    }

    @RequestMapping("/create")
    public String generateInstructionToPatient(Model model) {

        InstructionsToPatientForm form = new InstructionsToPatientForm();
        model.addAttribute("instructionsToPatientForm", form);
        model.addAttribute("allInstructions", instructionService.getAllInstructions());

        return "/instructionToPatient/assign";
    }

    @RequestMapping("/assign")
    public String assignInstructionToPatient(@ModelAttribute InstructionsToPatientForm form, Model model)
            throws IOException, SerialException, SQLException, WriterException {

        String id = Long.toString(form.getId());
        model.addAttribute("patient", form);

        String patientData = patientService.getPatientData(id);
        List<DateAndConditions> dateAndConditions = patientService.getEncounter(id);

        if (dateAndConditions.isEmpty()) {
            return "/practitioner/dashboard/error";
        }

        JsonObject name = JsonParser.parseString(patientData).getAsJsonObject().get("name").getAsJsonArray().get(0)
                .getAsJsonObject();

        String mail = name.get("family").getAsString() + "@mail.de";
        String firstName = name.get("given").getAsJsonArray().get(0).getAsString();
        String lastName = name.get("family").getAsString();

        UserData patientUser = userDataService.userExists(firstName, lastName, mail);

        if (patientUser != null) {
            // If patient alreasy exist
            PatientData patient = patientService.findById(patientUser.getId());

            if (patient.getDateAndConditions().size() == dateAndConditions.size()) {
                // Nothing to update
                return "redirect:/practitioner/dashboard";
            }

            form.setPatient(patient);
            List<InstructionToPatient> listOfInstructions = instructionToPatientService.handleForm(form);

            Encounters dateAndConditionsData = patientService.getDateAndConditionsData(dateAndConditions,
                    listOfInstructions, patient);
            encounterRepository.save(dateAndConditionsData);

            return "redirect:/practitioner/dashboard";
        } else {
            // If patient does not already exist
            // User creation with lots of dummy data
            Random rand = new Random();
            int phoneNumber = 1000000000 + rand.nextInt(900000000);
            byte[] file_bytes = form.getFile().getBytes();
            form.setPassword(patientService.generatePassword(patientData));

            patientUser = new UserData(firstName, lastName, mail, form.getPassword(), Integer.toString(phoneNumber),
                    Role.PATIENT, id, new SerialBlob(file_bytes));

            // QR code generation
            QRCodeGenerator.createQRImage(id);

            userDataService.saveUserData(patientUser);
            System.out.println("Patient " + patientUser.getLastName() + " created\nPassword is: " + form.getPassword());

            PatientData patient = new PatientData();
            patient.setFhirId(Long.valueOf(id));
            patient.setId(patientUser.getId());
            patientDataRepository.save(patient);

            System.out.println(patientUser.getId() + " " + patient.getId());

            // ID to connect the instruction with the conditions
            form.setPatient(patient);
            List<InstructionToPatient> listOfInstructions = instructionToPatientService.handleForm(form);
            Encounters dateAndConditionsData = patientService.getDateAndConditionsData(dateAndConditions,
                    listOfInstructions, patient);
            dateAndConditionsData = encounterRepository.save(dateAndConditionsData);

            return "/practitioner/dashboard/patientGenerated";
        }
    }
}
