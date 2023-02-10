package com.wise23.chariteed.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wise23.chariteed.model.Encounters;
import com.wise23.chariteed.model.PatientData;

@Repository
public interface EncounterRepository extends JpaRepository<Encounters, Long> {
    List<Encounters> findByPatient(PatientData patient);
}
