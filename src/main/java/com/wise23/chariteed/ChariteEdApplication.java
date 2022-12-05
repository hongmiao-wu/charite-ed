package com.wise23.chariteed;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChariteEdApplication {

	public static String server_url = "https://hapi.fhir.org/baseR4";

	public static void main(String[] args) {
		SpringApplication.run(ChariteEdApplication.class, args);
	}
}
