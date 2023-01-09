package com.wise23.chariteed.controller;

import com.wise23.chariteed.model.User;
import com.wise23.chariteed.repository.UserRepository;
import com.wise23.chariteed.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@Controller
public class AuthController {
    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @RequestMapping(value = { "/login" }, method = RequestMethod.GET)
    public String login() {
        return "auth/login";
    }

    @RequestMapping(value = { "/register" }, method = RequestMethod.GET)
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @RequestMapping(value = { "/register" }, method = RequestMethod.POST)
    public String registerUser(Model model, @Valid User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "Failed to register user!");
            return "auth/register";
        }

        if (userRepository.existsByEmailAndMobile(user.getEmail(), user.getMobile())) {
            model.addAttribute("errorMessage", "ERROR: Patient Account already exists!");
            return "auth/register";
        }

        userService.saveUser(user);
        model.addAttribute("successMessage", "User registered successfully!");

        return "auth/login";
    }
}
