package com.wise23.chariteed.controller;

import com.wise23.chariteed.model.PatientData;
import com.wise23.chariteed.model.PractitionerData;
import com.wise23.chariteed.repository.PatientDataRepository;
import com.wise23.chariteed.repository.PractitionerDataRepository;
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


    @RequestMapping("/generatesubjects")
    @ResponseBody
    public String generateTestSubjects() {
        PatientData patient  = new PatientData();
        patient.setFhirId(Long.valueOf(591378));
        patientDataRepository.save(patient);

        PractitionerData practitioner = new PractitionerData();
        practitioner.setFhirId(Long.valueOf(1859));
        practitionerDataRepository.save(practitioner);

        return "done";
    }
}
