package com.wise23.chariteed.repository;
import com.wise23.chariteed.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface PatientRepository extends JpaRepository<Patient, Long>{
}
