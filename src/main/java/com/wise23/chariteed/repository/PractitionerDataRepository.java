package com.wise23.chariteed.repository;

import com.wise23.chariteed.model.PractitionerData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PractitionerDataRepository extends JpaRepository<PractitionerData, Long> {

    Optional<PractitionerData> findPractitionerDataByUser_Email(String user_email);

    Optional<PractitionerData> findPractitionerDataByFhirId(Long fhirId);

    Optional<PractitionerData> findPractitionerDataById(Long Id);

    List<PractitionerData> findAll();
}
