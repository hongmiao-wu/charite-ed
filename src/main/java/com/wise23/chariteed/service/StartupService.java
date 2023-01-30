package com.wise23.chariteed.service;

import com.wise23.chariteed.model.UserData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.wise23.chariteed.model.Role;

@Service
public class StartupService {

    @Autowired
    UserDataService userDataService;

    @EventListener(ApplicationReadyEvent.class)
    public void createAdmin() {
        UserData admin = new UserData("root", "root", "admin@example.com", "password", "9991234567", Role.ADMIN);
        userDataService.saveUserData(admin);
    }
    @EventListener(ApplicationReadyEvent.class)
    public void createPatient() {
        UserData patient = new UserData("patient", "patient", "patient@example.com", "password", "9991234568", Role.PATIENT);
        userDataService.saveUserData(patient);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void createPractitioner() {
        UserData practitioner = new UserData("practitioner", "practitioner", "practitioner@example.com", "password", "9991234569", Role.PRACTITIONER);
        userDataService.saveUserData(practitioner);
    }
}