package com.wise23.chariteed.service;

import com.wise23.chariteed.model.Instruction;
import com.wise23.chariteed.model.User;
import com.wise23.chariteed.repository.InstructionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class InstructionService {

    @Autowired
    InstructionRepository instructionRepository;

    @Autowired
    UserService userService;

    public Instruction saveInstruction(String username, Instruction instruction) {
        instruction.setCreatedAt(LocalDateTime.now());
        instruction.setCreatedBy(username);
        return instructionRepository.save(instruction);
    }
}
