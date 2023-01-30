package com.wise23.chariteed.model;
import lombok.*;
import org.checkerframework.common.aliasing.qual.Unique;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import javax.persistence.Entity;

@Entity
@Table(name="practitioners")
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class Practitioner {
    @SequenceGenerator(name = "practitioner_sequence", sequenceName = "practitioner_sequence", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "practitioner_sequence")
    private Long practitionerId;
    @NotNull @Unique
    private Long userId;
    private String prefix;
    private String firstname;
    private String lastname;
}
