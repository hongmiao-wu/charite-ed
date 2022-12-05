package com.wise23.chariteed.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;

public class PatientService {
    public FhirContext fhirContext;
    public IGenericClient client;

    public PatientService() {
        this.fhirContext = FhirContext.forR4();
        this.client = fhirContext.newRestfulGenericClient(ChariteEdApplication.server_url);
    }
}