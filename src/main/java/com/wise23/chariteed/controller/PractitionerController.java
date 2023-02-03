package com.wise23.chariteed.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.zxing.WriterException;
import com.wise23.chariteed.constant.QRCodeGenerator;
import com.wise23.chariteed.model.*;
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

    @PostMapping("/dashboard/generatePatient")
    public String generatePatientSubmit(@ModelAttribute PatientGenerator generator, Model model)
            throws WriterException, IOException, SerialException, SQLException {
        String id = Long.toString(generator.getId());
        model.addAttribute("patient", generator);

        String patientData = patientService.getPatientData(id);
        String patientCondition = patientService.getCondition(id);

        if (patientCondition == null) {
            return "/practitioner/dashboard/error";
        }

        generator.setPassword(patientService.generatePassword(patientData));

        JsonObject name = JsonParser.parseString(patientData).getAsJsonObject().get("name").getAsJsonArray().get(0)
                .getAsJsonObject();

        // User creation with lots of dummy data
        byte[] file_bytes = generator.getFile().getBytes();
        UserData user = new UserData(name.get("given").getAsJsonArray().get(0).getAsString(),
                name.get("family").getAsString(),
                "test@mail.de", generator.getPassword(), "2222222222", Role.PATIENT, id,
                new SerialBlob(file_bytes), patientCondition);

        if (userDataService.userExists(user)) {
            logger.error("ERROR: Patient Account already exists!");
            return "/practitioner/dashboard/error";
        }

        // QR code generation
        QRCodeGenerator.createQRImage(id);

        userDataService.saveUserData(user);
        System.out.println("Patient " + user.getLastName() + " created\nPassword is: " + generator.getPassword());

        return "redirect:/practitioner/dashboard/patientGenerated";
    }

    @RequestMapping("/itp-acknowledged/{itpID}")
    public String acknowledgeFeedback(@PathVariable Long itpID) {

        InstructionToPatient dbITP = instructionToPatientService.getInstructionToPatientById(itpID);
        instructionToPatientService.acknowledgeFeedback(dbITP);

        return "redirect:/practitioner/view/" + dbITP.getPractitioner().getFhirId();

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
        String patientCondition = patientService.getCondition(id);

        if (patientCondition == null) {
            return "/practitioner/dashboard/error";
        }

        form.setPassword(patientService.generatePassword(patientData));

        JsonObject name = JsonParser.parseString(patientData).getAsJsonObject().get("name").getAsJsonArray().get(0)
                .getAsJsonObject();

        // User creation with lots of dummy data
        byte[] file_bytes = form.getFile().getBytes();
        UserData user = new UserData(name.get("given").getAsJsonArray().get(0).getAsString(),
                name.get("family").getAsString(),
                "test@mail.de", form.getPassword(), "2222222222", Role.PATIENT, id,
                new SerialBlob(file_bytes), patientCondition);

        if (userDataService.userExists(user)) {
            logger.error("ERROR: Patient Account already exists!");
            return "/practitioner/dashboard/error";
        }

        // QR code generation
        QRCodeGenerator.createQRImage(id);

        userDataService.saveUserData(user);
        System.out.println("Patient " + user.getLastName() + " created\nPassword is: " + form.getPassword());

        PatientData patient = new PatientData();
        patient.setFhirId(Long.valueOf(id));
        patient.setId(user.getId());
        patientDataRepository.save(patient);

        System.out.println(user.getId() + " " + patient.getId());

        form.setPatient(patient);
        instructionToPatientService.handleForm(form);

        return "/practitioner/dashboard/patientGenerated";

        // return "redirect:/practitioner/view/" + form.getPractitioner().getFhirId();
        // return "redirect:/practitioner/dashboard";
    }

}
