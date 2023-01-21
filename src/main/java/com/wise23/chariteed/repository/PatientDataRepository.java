package com.wise23.chariteed.repository;

import com.wise23.chariteed.model.PatientData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientDataRepository extends JpaRepository<PatientData, Long> {

    Optional<PatientData> findPatientDataByFhirId(Long fhirId);
}
