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
    private Long id;
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    private String type;
    private String content;
}
