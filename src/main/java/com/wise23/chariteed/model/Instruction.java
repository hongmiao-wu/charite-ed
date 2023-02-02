package com.wise23.chariteed.model;

import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "instructions")
@Getter @Setter @NoArgsConstructor
public class Instruction {
    @SequenceGenerator(name = "instructions_sequence", sequenceName = "instructions_sequence", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "instructions_sequence")
    @Column(name = "instruction_id")
    private Long id;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", updatable = false)
    private String createdBy;

    private Boolean feedbackNeeded = true;

    private String title;

    private String content;

}
