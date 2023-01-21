package com.wise23.chariteed.repository;

import com.wise23.chariteed.model.InstructionToPatient;
import com.wise23.chariteed.model.PatientData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InstructionToPatientRepository extends JpaRepository<InstructionToPatient, Long> {


    @Query("SELECT i FROM InstructionToPatient i WHERE i.patient.fhirId = ?1 AND i.givenAt IN (SELECT MAX(i_latest.givenAt) FROM InstructionToPatient i_latest WHERE i_latest.patient.fhirId = ?1)")
    Optional<InstructionToPatient> findLatestInstructionOfPatient(Long fhirID);
}