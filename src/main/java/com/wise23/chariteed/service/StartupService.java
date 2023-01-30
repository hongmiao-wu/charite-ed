package com.wise23.chariteed.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.wise23.chariteed.model.Role;
import com.wise23.chariteed.model.User;

@Service
public class StartupService {

    @Autowired
    UserService userService;

    @EventListener(ApplicationReadyEvent.class)
    public void createAdmin() {
        User admin = new User("root", "root", "admin@example.com", "password", "9991234567", Role.ADMIN);
        userService.saveUser(admin);
    }
    @EventListener(ApplicationReadyEvent.class)
    public void createPatient() {
        User patient = new User("patient", "patient", "patient@example.com", "password", "9991234568", Role.PATIENT);
        userService.saveUser(patient);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void createPractitioner() {
        User practitioner = new User("practitioner", "practitioner", "practitioner@example.com", "password", "9991234569", Role.PRACTITIONER);
        userService.saveUser(practitioner);
    }
}