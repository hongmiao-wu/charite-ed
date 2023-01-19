package com.wise23.chariteed.repository;

import com.wise23.chariteed.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface AdminRepository extends JpaRepository<Admin, Long>{
}
