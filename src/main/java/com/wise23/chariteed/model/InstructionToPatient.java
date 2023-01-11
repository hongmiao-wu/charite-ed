package com.wise23.chariteed.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name ="patient_instruction")
@Getter @Setter @NoArgsConstructor
public class InstructionToPatient {
    @Id
    private Long id;
    @ManyToOne
    @JoinColumn(name="user_id")
    User user;
    @ManyToOne
    @JoinColumn(name="instruction_id")
    Instruction instruction;
    LocalDateTime givenAt;
}
