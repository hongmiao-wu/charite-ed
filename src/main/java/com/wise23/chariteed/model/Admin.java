package com.wise23.chariteed.model;
import lombok.*;
import org.checkerframework.common.aliasing.qual.Unique;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.validation.constraints.Email;

@Entity
@Table(name="admins")
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class Admin {
    @SequenceGenerator(name = "admin_sequence", sequenceName = "admin_sequence", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "admin_sequence")
    private Long adminId;
    @NotNull @Unique
    private Long userId;
    @NotNull @Unique
    @Email(message = "Please enter a valid email address")
    private String email;
}
