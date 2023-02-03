package com.wise23.chariteed.controller;

import com.wise23.chariteed.model.Instruction;
import com.wise23.chariteed.service.InstructionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/instruction")
public class InstructionController {

    @Autowired
    InstructionService instructionService;

    @RequestMapping("/view/{id}")
    public String viewInstruction(@PathVariable Long id, Model model) {
        Instruction dbInstruction = instructionService.getInstruction(id);
        model.addAttribute("instruction", dbInstruction);

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
        return "redirect:/practitioner/dashboard";
    }

    @GetMapping("/view/all")
    public String getAllInstructions(Model model) {
        List<Instruction> allInstructions = instructionService.getAllInstructions();
        model.addAttribute("allInstructions", allInstructions);

        return "instruction/allInstructions";
    }

    @RequestMapping("/delete/{id}")
    public String deleteInstruction(@PathVariable Long id) {
        instructionService.deleteInstruction(id);

        return "redirect:/instruction/view/all";
    }

    @RequestMapping("/extern")
    public String externalMedicationStatements(Model model) {
        List<Instruction> hapiFhirInstructions = instructionService.fetchHapiFhirInstructionsBundle();
        model.addAttribute("allInstructions", hapiFhirInstructions);

        return "/instruction/medicalstatements";
    }

    @RequestMapping("/create/{msFhirID}")
    public String createInstructionFromExternal(@PathVariable Long msFhirID, Model model) {
        Instruction msInstruction = instructionService.fetchHapiFhirInstructionByFhirId(msFhirID);
        model.addAttribute("instruction", msInstruction);

        return "instruction/createInstruction";
    }

}
