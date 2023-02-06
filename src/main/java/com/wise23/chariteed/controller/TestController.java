package com.wise23.chariteed.controller;

import com.wise23.chariteed.model.PatientData;
import com.wise23.chariteed.model.PractitionerData;
import com.wise23.chariteed.model.Role;
import com.wise23.chariteed.model.UserData;
import com.wise23.chariteed.repository.PatientDataRepository;
import com.wise23.chariteed.repository.PractitionerDataRepository;
import com.wise23.chariteed.service.UserDataService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/test")
public class TestController {

    @Autowired
    PractitionerDataRepository practitionerDataRepository;

    @Autowired
    PatientDataRepository patientDataRepository;

    @Autowired
    UserDataService userDataService;

    @RequestMapping("/generatesubjects")
    public String generateTestSubjects() {
        UserData test_practitioner = new UserData("Angela", "Merkel", "angie@merk.test", "testpassword", "9878987898",
                Role.PRACTITIONER);

        userDataService.saveUserData(test_practitioner);

        System.out.println("PRACID: " + test_practitioner.getId());

        // PatientData patient = new PatientData();
        // patient.setFhirId(Long.valueOf(591378));
        // patient.setId((long) 23);
        // patientDataRepository.save(patient);

        PractitionerData practitioner = new PractitionerData();
        practitioner.setFhirId(Long.valueOf(1859));
        practitioner.setId(test_practitioner.getId());
        practitionerDataRepository.save(practitioner);

        return "redirect:/practitioner/view/all";
    }
}
