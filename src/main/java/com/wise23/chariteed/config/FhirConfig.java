package com.wise23.chariteed.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ca.uhn.fhir.context.FhirContext;

@Configuration
public class FhirConfig {

    @Bean
    public static FhirContext getFhirContext() {
        return FhirContext.forR4();
    }
}
