package com.wise23.chariteed.controller;

import com.wise23.chariteed.model.User;
import com.wise23.chariteed.service.PatientService;
import com.wise23.chariteed.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.security.Principal;

@Controller
public class UserController {

    PatientService patientService = new PatientService();

    @Autowired
    UserService userService;

    @RequestMapping(value = { "/dashboard" }, method = RequestMethod.GET)
    public String homePage(Principal principal, Model model) {
        User user = userService.getUser(principal.getName());
        model.addAttribute("user", user);

        return "user/dashboard";
    }

    @GetMapping("/{filename:.+}")
    @ResponseBody
    public ResponseEntity<File> serveFile(@PathVariable String filename, Principal principal) {
        User user = userService.getUser(principal.getName());
        File test = user.getFile();
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + test.getName() + "\"").body(test);
    }
}
