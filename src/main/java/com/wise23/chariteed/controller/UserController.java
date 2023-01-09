package com.wise23.chariteed.controller;

import com.wise23.chariteed.model.User;
import com.wise23.chariteed.service.PatientService;
import com.wise23.chariteed.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.sql.Blob;
import java.sql.SQLException;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/dashboard")
public class UserController {

    PatientService patientService = new PatientService();

    @Autowired
    UserService userService;

    @Autowired
    ResourceLoader resourceLoader;

    @RequestMapping(method = RequestMethod.GET)
    public String homePage(Principal principal, Model model) throws SQLException {
        User user = userService.getUser(principal.getName());
        model.addAttribute("user", user);

        return "user/dashboard";
    }

    @GetMapping("/download")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(HttpServletResponse response, Principal principal)
            throws SQLException, IOException {

        User user = userService.getUser(principal.getName());
        Blob test = user.getFile();

        Resource file = convertBlobToResource(test);

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + "test.pdf" + "\"").body(file);
    }

    private Resource convertBlobToResource(Blob blob) {
        InputStream inputStream;
        try {
            inputStream = blob.getBinaryStream();
        } catch (SQLException e) {
            throw new RuntimeException("Error getting binary stream from blob", e);
        }
        return new InputStreamResource(inputStream);
    }
}
