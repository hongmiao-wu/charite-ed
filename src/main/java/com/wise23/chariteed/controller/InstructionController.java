package com.wise23.chariteed.controller;

import com.wise23.chariteed.model.Instruction;
import com.wise23.chariteed.service.InstructionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Slf4j
@Controller
@RequestMapping("/instruction")
public class InstructionController {

    @Autowired
    InstructionService instructionService;


    @RequestMapping("/view")
    public String viewInstruction(Model model) {
        return "instruction/viewInstruction";
    }

    @GetMapping("/create")
    public String createInstruction(Model model) {
        model.addAttribute("instruction", new Instruction());

        return "instruction/createInstruction";
    }

    @PostMapping("/create")
    public String saveInstruction(@ModelAttribute Instruction instruction, Principal principal, Model model) {

        String username = (principal != null) ? principal.getName() : null;
        Instruction savedInstruction = instructionService.saveInstruction(username, instruction);

        model.addAttribute("instruction", savedInstruction);
        return "/instruction/viewInstruction";
    }


}
