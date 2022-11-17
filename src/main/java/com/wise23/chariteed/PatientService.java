package com.wise23.chariteed;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;

public class PatientService {
    protected FhirContext fhirContext;
    protected IGenericClient client;

    public PatientService() {
        this.fhirContext = FhirContext.forR4();
        this.client = fhirContext.newRestfulGenericClient("https://hapi.fhir.org/baseR4");
    }
}