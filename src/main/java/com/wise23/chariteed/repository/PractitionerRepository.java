package com.wise23.chariteed.repository;
import com.wise23.chariteed.model.Practitioner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface PractitionerRepository extends JpaRepository<Practitioner, Long>{
}
