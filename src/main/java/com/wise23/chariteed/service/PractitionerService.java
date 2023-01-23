package com.wise23.chariteed.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.wise23.chariteed.GetPropertiesBean;
import com.wise23.chariteed.config.FhirConfig;
import com.wise23.chariteed.model.PractitionerData;
import com.wise23.chariteed.repository.PractitionerDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<PractitionerData> getAllPractitioners() {
        return practitionerDataRepository.findAll();
    }
}
