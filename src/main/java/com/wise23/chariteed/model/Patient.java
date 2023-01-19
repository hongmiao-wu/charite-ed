package com.wise23.chariteed.model;
import lombok.*;
import org.checkerframework.common.aliasing.qual.Unique;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import javax.persistence.Entity;

@Entity
@Table(name="patients")
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class Patient {
    @Id
    private Long patientId;
    @NotNull @Unique
    private Long userId;
}
