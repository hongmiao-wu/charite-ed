package com.wise23.chariteed.repository;

import com.wise23.chariteed.model.Instruction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InstructionRepository extends JpaRepository<Instruction, Long> {

    Optional<Instruction> findById(Long id);

    List<Instruction> findAll();
}
