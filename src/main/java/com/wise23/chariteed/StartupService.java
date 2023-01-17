package com.wise23.chariteed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.wise23.chariteed.model.Role;
import com.wise23.chariteed.model.User;
import com.wise23.chariteed.service.UserService;

@Service
public class StartupService {

    @Autowired
    UserService userService;

    @EventListener(ApplicationReadyEvent.class)
    public void createAdmin() {
        User admin = new User("root", "root", "admin@example.com", "password", "9991234567", Role.ADMIN);
        userService.saveUser(admin);
    }
}