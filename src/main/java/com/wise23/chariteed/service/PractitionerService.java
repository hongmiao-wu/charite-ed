package com.wise23.chariteed.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.wise23.chariteed.constant.GetPropertiesBean;
import com.wise23.chariteed.config.FhirConfig;
import com.wise23.chariteed.model.PractitionerData;
import com.wise23.chariteed.repository.PractitionerDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class PractitionerService {

    @Autowired
    PractitionerDataRepository practitionerDataRepository;

    public FhirContext fhirContext;

    public IGenericClient client;

    public PractitionerService() {
        this.fhirContext = FhirConfig.getFhirContext();
        this.client = fhirContext.newRestfulGenericClient(GetPropertiesBean.getTestserverURL());
    }

    public PractitionerData findByFhirId(Long fhirID) {
        return practitionerDataRepository.findPractitionerDataByFhirId(fhirID).orElse(null);
    }

    public PractitionerData findById(Long ID) {
        return practitionerDataRepository.findPractitionerDataById(ID).orElse(null);
    }

    public List<PractitionerData> getAllPractitioners() {
        return practitionerDataRepository.findAll();
    }

    public static String generatePassword(String firstName, String lastName) {
        Random rand = new Random();
        int randomNumber = 100000 + rand.nextInt(900000);

        return firstName + "_" + lastName + "_" + randomNumber;
    }
}